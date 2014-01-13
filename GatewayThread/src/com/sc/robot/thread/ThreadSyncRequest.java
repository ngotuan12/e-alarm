package com.sc.robot.thread;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.sql.Connection;
import java.util.Vector;

import org.codehaus.jettison.json.JSONObject;

import com.fss.thread.ParameterType;
import com.fss.thread.ThreadConstant;
import com.fss.util.AppException;

/**
 * DEPQ 2011
 * 
 * @version 1.0
 */
public class ThreadSyncRequest extends com.fss.thread.ManageableThread
{
	Connection connection;
	private int iDelaytime = 30000;
	private String strHost;
	private int port;
	Socket client;

	public ThreadSyncRequest()
	{
	}

	@SuppressWarnings({
			"unchecked", "rawtypes"
	})
	public Vector getParameterDefinition()
	{
		Vector vtReturn = new Vector();

		vtReturn.addElement(createParameterDefinition("Host", "192.168.1.110",
				ParameterType.PARAM_TEXTBOX_MAX, "100"));
		vtReturn.addElement(createParameterDefinition("Port", "32000",
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

	protected void beforeSession() throws Exception
	{

	}

	protected void processSession() throws Exception
	{
		DataInputStream in = null;
		DataOutputStream out = null;
		while (miThreadCommand != ThreadConstant.THREAD_STOP)
		{
			try
			{
				logInfo("Check server " + strHost + ":" + port
						+ " connection...");
				client = new Socket(strHost, port);
				logInfo("Just connected to " + client.getRemoteSocketAddress());
				logInfo("Connect to server successful..");
				OutputStream outToServer = client.getOutputStream();
				out = new DataOutputStream(outToServer);
				JSONObject request = new JSONObject();
				request.put("cmd", "register_server");
				JSONObject content = new JSONObject();
				content.put("ver", 1);
				content.put("type", "request");
				content.put("cmd", "register_server");
				// body
				JSONObject body = new JSONObject();
				content.put("body", body);
				request.put("content", content);
				// System.out.println(request.toString());
				//
				logInfo("Register server..");
				out.writeUTF("A1" + request.toString() + "♦");
				//
				InputStream inFromServer = client.getInputStream();
				in = new DataInputStream(inFromServer);
//				logInfo("Server says " + in.readUTF());
				// InputStream inFromServer = client.getInputStream();
				// DataInputStream in = new DataInputStream(inFromServer);
				// logInfo("Server says " + in.readUTF());
			}
			catch (Exception ex)
			{
				logError("Can't connect server");
				logError(ex.getMessage());
			}
//			if (client != null && out != null && in != null)
//			{
//				try
//				{
//
//					/*
//					 * Keep on reading from/to the socket till we receive the
//					 * "Ok" from the server, once we received that then we
//					 * break.
//					 */
//					String responseLine;
//					while ((responseLine = in.readLine()) != null)
//					{
//					}
//
//					/*
//					 * Close the output stream, close the input stream, close
//					 * the socket.
//					 */
//				}
//				catch (Exception e)
//				{
//					logError("IOException:  " + e);
//				}
//			}
			Thread.sleep(iDelaytime * 1000);
		}
	}

	protected void afterSession() throws Exception
	{
	}

}
