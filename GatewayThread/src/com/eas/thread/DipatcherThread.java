package com.eas.thread;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Vector;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;

import com.fss.sql.Database;
import com.fss.thread.ThreadConstant;
import com.fss.util.AppException;
import com.sc.robot.util.AppServer;
import com.sc.robot.util.Util;

public class DipatcherThread extends com.fss.thread.ManageableThread
{
	private Connection conn;
	private int iDelaytime = 30000;
	PreparedStatement pstm;
	PreparedStatement pstmupdate;
	BufferedReader is = null;
	DataOutputStream out = null;
	Socket socket = null;
	public boolean isAnnounce = false;
	private String currentCmdID = "";

	public DipatcherThread()
	{
	}

	@SuppressWarnings({
			"unchecked", "rawtypes"
	})
	public Vector getParameterDefinition()
	{
		Vector vtReturn = new Vector();
		// Add all
		vtReturn.addAll(super.getParameterDefinition());
		return vtReturn;
	}

	public void fillParameter() throws AppException
	{
		iDelaytime = loadUnsignedInteger("DelayTime");
		super.fillParameter();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.fss.thread.ManageableThread#beforeSession()
	 */
	protected void beforeSession() throws Exception
	{
		conn = AppServer.getConnection("MySQL");
		pstm = conn
				.prepareStatement("SELECT * FROM gateway_request WHERE status = '2' ");
		pstmupdate = conn
				.prepareStatement("UPDATE gateway_request SET status = '1',response = ? WHERE id = ?  ");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.fss.thread.ManageableThread#processSession()
	 */
	protected void processSession() throws Exception
	{
		JSONArray arrRequest;
		// new socket
		Thread thread = new Thread()
		{
			@Override
			public void run()
			{
				try
				{
					socket = new Socket("127.0.0.1", 32432);
					// announce socket
					announce();
					// check response
					boolean connect = true;
					String responseLine;
					while (connect)
					{
						responseLine = is.readLine();
						if (responseLine != null)
						{
							synchronized (currentCmdID)
							{
								// update database
								pstmupdate.setString(1, responseLine);
								pstmupdate.setString(2, currentCmdID);
								pstmupdate.executeUpdate();
								// end response
								currentCmdID = null;
							}
						}
					}
				}
				catch (Exception e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		thread.start();
		while (miThreadCommand != ThreadConstant.THREAD_STOP && socket != null
				&& socket.isConnected())
		{
			try
			{
				// get list request
				arrRequest = Util.convertToJSONArray(pstm.executeQuery());
				if (arrRequest.length() > 0)
				{
					for (int i = 0; i < arrRequest.length(); i++)
					{
						try
						{
							JSONObject content = (JSONObject) arrRequest.get(i);
							currentCmdID = String.valueOf(content.getInt("id"));
							// send request
							String strRequest = content.getString("request");
							logInfo("send request " + strRequest);
							sendRequest(new JSONObject(strRequest));
							// wait response
							while (currentCmdID != null)
							{
								continue;
							}
						}
						catch (Exception ex)
						{
							ex.printStackTrace();
							// update database
							pstmupdate.setString(1, ex.getMessage());
							pstmupdate.setString(2, currentCmdID);
							pstmupdate.executeUpdate();
							continue;
						}
					}
				}
			}
			catch (Exception ex)
			{
				logError("exception");
				logError(ex.getMessage());
			}
			finally
			{

			}
			Thread.sleep(iDelaytime * 1000);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.fss.thread.ManageableThread#afterSession()
	 */
	protected void afterSession() throws Exception
	{
		if (is != null) is.close();
		if (out != null) out.close();
		if (socket != null) socket.close();
		Database.closeObject(pstm);
		Database.closeObject(pstmupdate);
		Database.closeObject(conn);
	}

	public void sendRequest(JSONObject request)
	{
		try
		{
			// monitor.logInfo("Send request: "+ request);
			out.writeUTF("A" + request.toString() + "BB");
			out.flush();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public void announce() throws Exception
	{
		try
		{
			OutputStream outToServer = socket.getOutputStream();
			out = new DataOutputStream(outToServer);
			is = new BufferedReader(new InputStreamReader(
					socket.getInputStream()));
			// announce
			JSONObject request = new JSONObject();
			request.put("cmd", "announce");
			request.put("type", "request");
			// body
			JSONObject body = new JSONObject();
			body.put("G_MAC", "C4:2C:03:01:04:75");
			body.put("G_IP", "192.168.1.100");
			body.put("G_type", "DISPATCHER");
			request.put("body", body);
			sendRequest(request);
			isAnnounce = true;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw e;
		}
	}
}
