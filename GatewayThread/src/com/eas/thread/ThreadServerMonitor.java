package com.eas.thread;

import java.net.Socket;
import java.sql.Connection;
import java.util.Vector;

import com.fss.thread.ParameterType;
import com.fss.thread.ThreadConstant;
import com.fss.util.AppException;

/**
 * DEPQ 2011
 * 
 * @version 1.0
 */
public class ThreadServerMonitor extends com.fss.thread.ManageableThread
{
	Connection connection;
	private int iDelaytime = 30000;
	private String strHost;
	private int port;
	Socket client;
	ServerMonitor monitor;

	public ThreadServerMonitor()
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
		monitor = new ServerMonitor(strHost, port, this);

	}

	protected void processSession() throws Exception
	{
		monitor.setDaemon(true);
		monitor.start();
		while (miThreadCommand != ThreadConstant.THREAD_STOP)
		{
			try
			{
				if (monitor.isAnnounce)
				{
					monitor.getTime();
				}
			}
			catch (Exception ex)
			{
				logError("exception");
				logError(ex.getMessage());
			}
			Thread.sleep(iDelaytime * 10000);
		}
	}

	protected void afterSession() throws Exception
	{
	}

}
