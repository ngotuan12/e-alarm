package com.eas.thread;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

import org.codehaus.jettison.json.JSONObject;

public class DispatcherSocketThread extends Thread
{
	public String strHost;
	public int iport;
	public DipatcherThread monitor;
	public Socket socket;
	BufferedReader is = null;
	DataOutputStream out = null;
	public boolean isAnnounce = false;

	public DispatcherSocketThread(String host, int port, DipatcherThread monitor)
	{
		// super
		super();
		//
		this.strHost = host;
		this.iport = port;
		this.monitor = monitor;
	}

	@Override
	public void run()
	{
		try
		{
			announce();
			//
			String responseLine;
			boolean connect = true;
			while (connect)
			{
				responseLine = is.readLine();
				if (responseLine != null)
				{
					if (!isAnnounce && responseLine.indexOf("announce") > 0)
					{
						isAnnounce = true;
					}
					else
					{
						DipatcherThread.currentResponse = responseLine;
					}
				}
				else if (responseLine == null)
				{
					isAnnounce = false;
					socket.close();
					monitor.logWarn("Disconnected");
					Thread.sleep(10000);
					monitor.logWarn("reconnect");
					announce();
					// connect = false;
				}
			}
		}
		catch (Exception e)
		{
			monitor.logError(e.getMessage());
		}
		finally
		{
			try
			{
				is.close();
				out.close();
				socket.close();
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}
		}
	}

	public void announce() throws Exception
	{
		try
		{
			monitor.logInfo("Check server " + strHost + ":" + iport
					+ " connection...");
			socket = new Socket();
			socket.connect(new InetSocketAddress(strHost, iport));
			monitor.logInfo("Just connected to "
					+ socket.getRemoteSocketAddress());
			monitor.logInfo("Connect to server successful..");
			OutputStream outToServer = socket.getOutputStream();
			out = new DataOutputStream(outToServer);
			is = new BufferedReader(new InputStreamReader(
					socket.getInputStream()));
			// announce
			monitor.logInfo("Announce to server..");
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
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw e;
		}
	}

	public void onResponse(String strResponse)
	{

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

	public void getTime()
	{
		try
		{
			JSONObject request = new JSONObject();
			request.put("cmd", "get_time");
			request.put("type", "request");
			// body
			JSONObject body = new JSONObject();
			request.put("body", body);
			sendRequest(request);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
