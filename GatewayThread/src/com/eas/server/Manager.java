package com.eas.server;

import java.io.File;
import java.io.PrintStream;
import java.sql.Connection;

import com.fss.dictionary.Dictionary;
import com.fss.thread.ProcessorListener;
import com.fss.thread.ThreadConstant;
import com.fss.thread.ThreadManager;
import com.fss.thread.ThreadProcessor;
import com.fss.util.Global;
import com.fss.util.LogOutputStream;

public class Manager extends ThreadManager implements ProcessorListener
{
	public Manager(int port, ProcessorListener lsn) throws Exception
	{
		super(port, lsn);
	}

	public Manager() throws Exception
	{
		super();
	}

	public static Dictionary mdicConfig;
	public static final String SESSIONID = "SessionID";
	private static int SessionSeq = 0;
	private static long TransactionSeq = 0;
	public static com.fis.queue.QueueFlexibleTimeout queueRequest = new com.fis.queue.QueueFlexibleTimeout(
			new String[] {
				"SessionID"
			});
	public static String mstrBufferLog = "";

	public static synchronized int getSessionSeq()
	{
		return SessionSeq++;
	}

	public static synchronized long getTransactionSeq()
	{
		return TransactionSeq++;
	}

	public void onOpen(ThreadProcessor pProcessor) throws Exception
	{
	}

	public void onCreate(ThreadProcessor pProcessor) throws Exception
	{
	}

	public Connection getConnection() throws Exception
	{
		return null;
	}

	public static void main(String argv[]) throws Exception
	{

		ThreadManager manager;
		Manager lsn = new Manager();
		try
		{
			mdicConfig = new Dictionary(System.getProperty("user.dir")
					+ "/conf/WrapperConfig.txt");
			String mnuThreadManager = mdicConfig.getString("mnuThreadManager");
			mnuThreadManager = mnuThreadManager == null
					|| mnuThreadManager.equals("") ? "mnuThreadManager"
					: mnuThreadManager;
			String mstrErrorLog = mdicConfig.getString("ErrorLog");
			File fileLog = new File(System.getProperty("user.dir") + "/"
					+ mstrErrorLog);
			if (!fileLog.getParentFile().exists())
			{
				fileLog.getParentFile().mkdirs();
			}
			String mstrOutLog = mdicConfig.getString("OutLog");
			File fileOutLog = new File(System.getProperty("user.dir") + "/"
					+ mstrOutLog);
			if (!fileOutLog.getParentFile().exists())
			{
				fileOutLog.getParentFile().mkdirs();
			}
			//
			String mstrAlertLog = mdicConfig.getString("AlertLog");
			File fileAlertLog = new File(System.getProperty("user.dir") + "/"
					+ mstrAlertLog);
			if (!fileAlertLog.getParentFile().exists())
			{
				fileAlertLog.getParentFile().mkdirs();
			}
			//
			String mstrActionLog = mdicConfig.getString("ActionLog");
			File fileActionLog = new File(System.getProperty("user.dir") + "/"
					+ mstrActionLog);
			if (!fileActionLog.getParentFile().exists())
			{
				fileActionLog.getParentFile().mkdirs();
			}
			//
			mstrBufferLog = mdicConfig.getString("BufferLog");
			File fileBufferLog = new File(System.getProperty("user.dir") + "/"
					+ mstrBufferLog);
			if (!fileBufferLog.getParentFile().exists())
			{
				fileBufferLog.getParentFile().mkdirs();
			}
			//
			String mstrPort = mdicConfig.getString("PortID");
			mstrPort = mstrPort == null || mstrPort.equals("") ? "1111"
					: mstrPort;
			int mintPort = Integer.parseInt(mstrPort);
			String mstrMaxConnectionAllowed = mdicConfig
					.getString("MaxConnectionAllowed");
			mstrMaxConnectionAllowed = mstrMaxConnectionAllowed == null
					|| mstrMaxConnectionAllowed.equals("") ? "5"
					: mstrMaxConnectionAllowed;
			int miMaxConnectionAllow = Integer
					.parseInt(mstrMaxConnectionAllowed);
			//
			String messageTimeout = mdicConfig.getString("MessageTimeout");
			messageTimeout = messageTimeout == null
					|| messageTimeout.equals("") ? "300" : messageTimeout;
			int miTimeout = Integer.parseInt(messageTimeout) * 1000;
			queueRequest.setTimeOut(miTimeout);
			//
			String queueSize = mdicConfig.getString("MaxQueueSize");
			queueSize = queueSize == null || queueSize.equals("") ? "1000"
					: queueSize;
			int miQueueSize = Integer.parseInt(queueSize);
			queueRequest.setMaxQueueSize(miQueueSize);
			//
			ThreadConstant.setMenuThreadManager(mnuThreadManager);
			//
			PrintStream errorPs = new PrintStream(new LogOutputStream(
					mstrErrorLog));
			PrintStream outPs = new PrintStream(new LogOutputStream(mstrOutLog));
			System.setOut(outPs);
			System.setErr(errorPs);
			//
			manager = new Manager(mintPort, lsn);
			manager.setActionLogFile(mstrActionLog);
			manager.setAlertLogFile(new File(mstrAlertLog));
			manager.setMaxConnectionAllowed(miMaxConnectionAllow);
			manager.setLoadingMethod(ThreadManager.LOAD_FROM_FILE);
			Global.APP_NAME = "Telecom Cambodia Wrapper Serial System";
			Global.APP_VERSION = "1.0";

			manager.start();
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

	@Override
	public String getSelectDisbledThreadSQLCommand()
	{
		return "SELECT THREAD_ID FROM THREAD WHERE STARTUP_TYPE = 0 AND app_id = "
				+ mdicConfig.getString("AppID") + " ORDER BY THREAD_ID";
	}

	@Override
	public String getSelectLoadableThreadSQLCommand()
	{
		return "SELECT THREAD_ID,THREAD_NAME,CLASS_NAME,STARTUP_TYPE,COMMAND FROM THREAD WHERE STARTUP_TYPE > 0 AND app_id = "
				+ mdicConfig.getString("AppID") + " ORDER BY THREAD_ID";
	}
}
