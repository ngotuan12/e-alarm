package com.eas.thread;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Vector;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;

import com.eas.util.AppServer;
import com.eas.util.Util;
import com.fss.sql.Database;
import com.fss.thread.ParameterType;
import com.fss.thread.ThreadConstant;
import com.fss.util.AppException;

public class DipatcherThread extends com.fss.thread.ManageableThread
{
	private static Connection conn;
	private int iDelaytime = 30000;
	private static PreparedStatement pstm;
	public static PreparedStatement pstmupdate;
	public boolean isAnnounce = false;
	public static String currentCmdID = "";
	public static JSONObject currentRequest;
	public static String currentResponse = null;
	private DispatcherSocketThread dispatcherSocket;

	private String strHost;
	private int port;

	public DipatcherThread()
	{
	}

	@SuppressWarnings({
			"unchecked", "rawtypes"
	})
	public Vector getParameterDefinition()
	{
		Vector vtReturn = new Vector();

		vtReturn.addElement(createParameterDefinition("Host", "54.243.244.187",
				ParameterType.PARAM_TEXTBOX_MAX, "100"));
		vtReturn.addElement(createParameterDefinition("Port", "8888",
				ParameterType.PARAM_TEXTBOX_MASK, "99999"));
		// Add all
		vtReturn.addAll(super.getParameterDefinition());
		return vtReturn;
	}

	public void fillParameter() throws AppException
	{
		iDelaytime = loadUnsignedInteger("DelayTime");
		port = loadUnsignedInteger("Port");
		strHost = loadMandatory("Host");
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
				.prepareStatement("SELECT * FROM gateway_request,gateway WHERE gateway_request.status = '2' and gateway_request.gateway_id = gateway.id ");
		pstmupdate = conn
				.prepareStatement("UPDATE gateway_request SET status = '1',response = ? WHERE id = ?  ");
		dispatcherSocket = new DispatcherSocketThread(strHost, port, this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.fss.thread.ManageableThread#processSession()
	 */
	protected void processSession() throws Exception
	{
		dispatcherSocket.setDaemon(true);
		dispatcherSocket.start();
		while (miThreadCommand != ThreadConstant.THREAD_STOP)
		{
			if (dispatcherSocket.isAnnounce)
			{
				JSONArray arrRequest;
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
								JSONObject content = (JSONObject) arrRequest
										.get(i);
								currentCmdID = String.valueOf(content
										.getInt("id"));
								// send request
								String strRequest = content
										.getString("request");
								logInfo("Send request " + content.getString("mac_add"));
								logInfo("Content " + strRequest);	
								JSONObject jRequest = new JSONObject(strRequest);
								JSONObject request = new JSONObject();
								if (jRequest.getString("cmd") != null)
								{
									currentResponse = null;
									request.put("cmd", "send_gw_request");
									request.put("body", jRequest);
									request.put("G_MAC",
											content.getString("mac_add"));
									currentRequest = request;
									
									dispatcherSocket.sendRequest(request);
									int time = 0;
									while(currentResponse==null&&time<=5)
									{
										time++;
										Thread.sleep(1000);
									}
									if(currentResponse!=null)
									{
										logInfo("Response: "+currentResponse);
										pstmupdate.setString(1,
												currentResponse);
										pstmupdate.setString(2, currentCmdID);
										pstmupdate.executeUpdate();
									}
									else
									{
										logInfo("Response: "+"request time out");
										pstmupdate.setString(1,
												"Request time out");
										pstmupdate.setString(2, currentCmdID);
										pstmupdate.executeUpdate();
									}
								}
								else
								{
									logInfo("Response: "+"cmd not found");
									request.put("error",
											"cmd not found");
									// sendRequest(request);
									pstmupdate.setString(1,
											"chuoi nhap ko chinh xac");
									pstmupdate.setString(2, currentCmdID);
									pstmupdate.executeUpdate();
								}
							}
							catch (Exception ex)
							{
								ex.printStackTrace();
								// update database
								logInfo("Response: "+ex.getMessage());
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
		Database.closeObject(pstm);
		Database.closeObject(pstmupdate);
		Database.closeObject(conn);
	}
}
