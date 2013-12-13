package com.sc.robot.server;

import java.io.IOException;
import java.net.Socket;
import java.util.Hashtable;
import java.util.Vector;


import com.fss.dictionary.ErrorDictionary;
import com.fss.server.Server;
import com.fss.server.SystemInputStream;
import com.fss.server.SystemPrintStream;
import com.fss.server.telnet.TelnetInputStream;
import com.fss.server.telnet.TelnetSession;
import com.fss.util.AppException;
import com.fss.util.StringUtil;
import com.sc.robot.telnet.ThreadManagerTelnetSession;
import com.sc.robot.util.WrapperMessage;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2011
 * </p>
 * <p>
 * Company: FSS-FPT
 * </p>
 * 
 * @author Pham Quang De
 * @version 1.0
 */

public class TelnetServer extends TelnetSession {
	// //////////////////////////////////////////////////////
	// Constant
	// //////////////////////////////////////////////////////
	public static final String end = "---END\r\n";
	private int sessionID;
	public long miLastReceiveCommand = 0;
	private long miExpireInterval = 0;
	private int miMessageTimeout = 5 * 60 * 1000;
	private String mstrUserName = "depq";
	private String mstrPassword = "depq";
	private String keepAlive = "ALIVE";
	private String successCommand = "SUCCESS";
	private ThreadManagerTelnetSession manager;
	public Hashtable<Long, WrapperMessage> hashResponse = new Hashtable<Long, WrapperMessage>();

	// //////////////////////////////////////////////////////
	/**
	 * 
	 * @param channel
	 *            TelnetMessageChannel
	 * @param in
	 *            SystemInputStream
	 * @param out
	 *            SystemPrintStream
	 * @param err
	 *            SystemPrintStream
	 * @param s
	 *            Socket
	 * @param server
	 *            Server
	 */
	// //////////////////////////////////////////////////////
	public TelnetServer(SystemInputStream in, SystemPrintStream out,
			SystemPrintStream err, Socket s, Server server,
			ThreadManagerTelnetSession manager) {
		super(in, out, err, s, server);
		sessionID = Manager.getSessionSeq();
		this.manager = manager;
	}

	public void setExpireInterval(int expire) {
		miExpireInterval = expire;
	}

	public void setUserName(String user) {
		mstrUserName = user;
	}

	public void setPassword(String pass) {
		mstrPassword = pass;
	}

	public void setMessageTimeout(int timeout) {
		miMessageTimeout = timeout;
	}

	// //////////////////////////////////////////////////////
	/**
	 * DEPQ
	 * 
	 * @param strUserName
	 *            String
	 * @param strPassword
	 *            String
	 * @return boolean
	 * @throws IOException
	 */
	// //////////////////////////////////////////////////////
	public boolean login(String strUserName, String strPassword)
			throws IOException {
		try {
			if (checkLogin(strUserName, strPassword)) {
				String loginResult = "SUCCESS0001:Operation is successful\r\n\r\nThere is together 1 report\r\n\r\n---   END\r\n";
				print(loginResult);
				miLastReceiveCommand = System.currentTimeMillis();
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			try {
				if (out != null) {
					print(ErrorDictionary.getString(e) + "\r\n");
				}
				flush();
			} catch (Exception e1) {
			}
			return false;
		}
	}

	/**
	 * DEPQ: Kiem tra user/pass
	 * 
	 * @param user
	 *            String
	 * @param pass
	 *            String
	 * @return boolean
	 */
	public boolean checkLogin(String user, String pass) {
		if (mstrUserName.equalsIgnoreCase(user) && mstrPassword.equals(pass)) {
			return true;
		}
		return false;
	}

	// //////////////////////////////////////////////////////
	/**
	 * Logs the user into the system. This function will prompt the user for
	 * username and password.
	 * 
	 * @throws IOException
	 */
	// //////////////////////////////////////////////////////
	public void login() throws IOException {
		boolean connected = false;
		welcome();
		int loginAttempts = 0;
		while (!connected) {
			try {
				// Get user name & password
				TelnetInputStream tin = (TelnetInputStream) in;
				tin.negotiateEcho();
				print("Login:");
				String login = in.readLine();
				if (login != null) {
					// printend("Script Login is not correct\r\n");
					if (login.equals("")) {
						continue;
					}
				} else {
					forceEndSession();
					return;
				}
				if (!login.endsWith(";")) {
					printend("Script Login/Logout is must end with ;\r\n");
					continue;
				}
				login = login.substring(0, login.length() - 1);
				String[] array = login.split(":");
				String command = array[0];
				if (!command.equals("LGI")) {
					if (command.equals("LGO")) {
						printend("Logout success!\r\n");
						forceEndSession();
						return;
					}
					printend("Command Login is not correct \"LGI\"\r\n");
					continue;
				}
				if (array.length < 2) {
					printend("Script Login is not correct!\r\n");
					continue;

				}
				String[] userpass = array[1].split(",");
				for (int i = 0; i < userpass.length; i++) {
					String[] key_val = userpass[i].split("=");
					if (key_val[0].trim().equals("OPNAME")) {
						if (key_val.length < 2) {
							printend("You must put UserName into script Login!\r\n");
							continue;

						}
						userName = key_val[1].trim();
						int index1 = userName.indexOf("\"");
						int index2 = userName.indexOf("\"", index1);
						if (index1 < 0 || index2 < 0) {
							printend("You must put UserName into \"\"!\r\n");
							continue;
						}
						userName = userName.substring(1, userName.length() - 1);
					}
					if (key_val[0].trim().equals("PWD")) {
						if (key_val.length < 2) {
							printend("You must put Password into script Login!\r\n");
							continue;

						}
						password = key_val[1].trim();
						int index1 = password.indexOf("\"");
						int index2 = password.indexOf("\"", index1);
						if (index1 < 0 || index2 < 0) {
							printend("You must put Password into \"\" !\r\n");
							continue;
						}
						password = password.substring(1, password.length() - 1);
					}
				}
				if (userName == null || userName.equals("") || password == null) {
					printend("User/Pass login incorect!\r\n");
					continue;
				}
				boolean ec = in.getEcho();
				in.setEcho(false); // tell the input stream not to echo this
				in.setEcho(ec);
				print("\r\n");

				if (login(userName, password)) {
					connected = true;
				}

				// Check error
				if (!connected) {
					printend("Login incorrect.\r\n");
					if (++loginAttempts >= 5) {
						forceEndSession();
						return;
					}
				}
				if (out.checkError()) {
					forceEndSession();
					return;
				}
			} catch (Exception e) {
				if (!shutdown) {
					printend("INTERNAL error:" + e.toString() + "\r\n");
				}
				e.printStackTrace();
				forceEndSession();
				throw new IOException();
			}
		}
		out.setSession(this); // tell the output stream who the session is
		in.setSession(this); // tell the input stream who the session is
		if (server != null) {
			String welcomeMsg = server.getWelcomeMsg();
			print(welcomeMsg + "\r\n");
		}
		currentCommandFinished();
	}

	public boolean shutdown() {
		return shutdown;
	}

	// //////////////////////////////////////////////////////
	/**
	 * Show welcome message
	 */
	// //////////////////////////////////////////////////////
	public void welcome() {
		String welcome = "********************************************************************************\r\n"
				+ "*"
				+ StringUtil.align("Telnet Server MML Simulator",
						StringUtil.ALIGN_CENTER, 78)
				+ "*\r\n"
				+ "*"
				+ StringUtil.align("Version 1.0", StringUtil.ALIGN_CENTER, 78)
				+ "*\r\n"
				+ "*"
				+ StringUtil
						.align("(C) 2010 FPT Telecommunication Software Solution. All Rights Reserved.",
								StringUtil.ALIGN_CENTER, 78)
				+ "*\r\n"
				+ "********************************************************************************\r\n";
		print("\r\n" + welcome);
		flush();
	}

	// //////////////////////////////////////////////////////
	/**
	 * 
	 * @param strFullCommand
	 *            String
	 * @return Vector
	 */
	// //////////////////////////////////////////////////////
	public static Vector analyseCommand(String strFullCommand) {
		Vector vtReturn = new Vector();
		int iStart = -1;
		char cStart = 0;
		for (int iIndex = 0; iIndex < strFullCommand.length(); iIndex++) {
			char c = strFullCommand.charAt(iIndex);
			if (cStart != 0) {
				if (c == cStart) {
					vtReturn.addElement(strFullCommand.substring(iStart + 1,
							iIndex));
					iStart = -1;
					cStart = 0;
				}
			} else {
				if (c == '\'' || c == '\"') {
					if (iStart >= 0) {
						vtReturn.addElement(strFullCommand.substring(iStart,
								iIndex));
					}
					cStart = c;
					iStart = iIndex;
				} else {
					if (c > 32) {
						if (iStart < 0) {
							iStart = iIndex;
						}
					} else {
						if (iStart >= 0) {
							vtReturn.addElement(strFullCommand.substring(
									iStart, iIndex));
							iStart = -1;
						}
					}
				}
			}
		}
		if (iStart >= 0) {
			vtReturn.addElement(strFullCommand.substring(iStart,
					strFullCommand.length()));
		}
		return vtReturn;
	}

	// //////////////////////////////////////////////////////
	/**
	 * 
	 * @param strFullCommand
	 *            String
	 * @return String
	 * @throws Exception
	 */
	// //////////////////////////////////////////////////////
	public String chpwd(String strFullCommand) throws Exception {
		Vector vtArgument = analyseCommand(strFullCommand);
		if (vtArgument.size() != 1) {
			throw new AppException("Syntax error\r\nUsage:\r\n\tchpwd");
		}
		boolean bEcho = in.getEcho();
		try {
			in.setEcho(false);
			print("Old password: ");
			String strOldPassword = getNextCommand();
			print("\r\nNew password: ");
			String strNewPassword = getNextCommand();
			print("\r\nConfirm new password: ");
			String strConfirmPassword = getNextCommand();
			print("\r\n");
			if (!strConfirmPassword.equals(strNewPassword)) {
				throw new AppException("Confirm password incorrect");
			}
			// doi pass
			return "Password has been changed\r\n";
		} finally {
			in.setEcho(bEcho);
		}
	}

	// //////////////////////////////////////////////////////
	/**
	 * 
	 * @param strNewServer
	 *            String
	 * @throws Exception
	 */
	// //////////////////////////////////////////////////////
	protected void onChangeServer(String strNewServer) throws Exception {
	}

	// //////////////////////////////////////////////////////
	/**
	 * DEPQ: Thuc hien lenh telnet
	 * 
	 * @param strFullCommand
	 *            String
	 */
	// //////////////////////////////////////////////////////
	public void execute(String strFullCommand) {
		try {
			log("Start processing command:" + strFullCommand);
			miLastReceiveCommand = System.currentTimeMillis();
			if (strFullCommand == null || strFullCommand.equals("")) {
				return;
			}

			System.out.println("Receive:" + strFullCommand);
			strFullCommand = strFullCommand.substring(0,
					strFullCommand.length() - 1);
			if (strFullCommand.equals(keepAlive)) {
				log("Client send keep alive command");
				String s = "Response to client: STATUS=0,RETN=0000,DESC=Keeping you alive";
				printend(s);
				currentCommandFinished();
				log(s);
				return;
			}
			if (strFullCommand.equals(successCommand)) {
				log("Client request successful return");
				String s = "STATUS=0,RETN=0000,DESC=Operation is succeeded";
				printend(s);
				currentCommandFinished();
				log(s);
				return;
			}
			if (strFullCommand.toUpperCase().equals("QUITX")
					|| strFullCommand.toUpperCase().equals("LGO")) {
				printend("Destroy session and close connection");
				currentCommandFinished();
				forceEndSession();
				return;
			}
			//
			com.sc.robot.server.Transaction transaction = new com.sc.robot.server.Transaction();
			WrapperMessage message = new WrapperMessage(strFullCommand, this,
					transaction, System.currentTimeMillis());
			message.setAttribute(Manager.SESSIONID, String.valueOf(sessionID));
			message.setTransaction(transaction);
			if (isDiff) {
				message.setAttribute("TimeoutPrivate", miMessageTimeout);
			}
			Manager.queueRequest.attach(message);
			synchronized (Manager.queueRequest) {
				Manager.queueRequest.notifyAll();
			}
			if (!message.isProcessed()) {
				synchronized (transaction) {
					transaction.wait(miMessageTimeout);
				}
			}
			WrapperMessage response = hashResponse.get(new Long(transaction
					.getTransID()));
			if (response == null) {
				String s = "STATUS=1,RETN=0999,DESC=Message timeout";
				printend(s);
				log("Response to client: " + s);
			} else if (response.getResponseCode().equals("0")) {
				String s = "STATUS=0,RETN=" + response.getResponseCode()
						+ ",DESC=Operation is succeeded,"
						+ response.getResponse();
				printend(s);
				log("Response to client: " + s);
			} else {
				String s = "STATUS=1,RETN=" + response.getResponseCode()
						+ ",DESC=Operation is fail," + response.getResponse();
				printend(s);
				log("Response to client: " + s);
			}
			currentCommandFinished();
		} catch (Exception ex) {
			ex.printStackTrace();
			String s = "STATUS=-1,RESP=1000,DESP=INTERNAL ERROR: "
					+ ex.getMessage() + "\r\n";
			print(s);
			log(s);
			currentCommandFinished();
		}
	}

	public String executeCommand(String strCommandName, String strParameter) {
		if (strCommandName == null || strCommandName.equals("")) {
			return "STATUS=1,RESP=1001,DESP=COMMAND WRONG!";
		}
		String result = "";
		if (result == null || result.trim().equals("")) {
			result = "STATUS=1,RESP=1001,DESP=COMMAND WRONG!";
		} else {
			result = convertResult(result, strParameter);
		}
		return result;
	}

	public String convertResult(String source, String parameter) {
		if (parameter == null || parameter.equals("")) {
			return source;
		}
		String[] par = parameter.split(",");
		for (int i = 0; i < par.length; i++) {
			String[] key_val = par[i].split("=");
			if (key_val.length > 1) {
				return source.replaceAll("#" + key_val[0].trim().toUpperCase()
						+ "#", key_val[1].trim().toUpperCase());
			} else {
				return source;
			}
		}
		return source;
	}

	public boolean checkExpire() {
		if (miLastReceiveCommand == 0) {
			return false;
		}
		if (System.currentTimeMillis() - miLastReceiveCommand > miExpireInterval) {
			return true;
		}
		return false;
	}

	public void setLastReceiveCommand(long time) {
		miLastReceiveCommand = time;
	}

	public void printend(String string) {
		try {
			if (!string.endsWith(end)) {
				print(string + end);
			} else {
				print(string);
			}
		} catch (Exception ex) {
			manager.endSession(this);
		}
	}

	public void print(String string) {
		super.print(string);
		System.out.println(string);
	}

	public void log(String s) {
		if (manager != null) {
			manager.logMonitor(s);
		}
	}

	boolean isDiff = false;

	public void setQueueTimeOut(boolean isDiff) {
		this.isDiff = isDiff;
	}

	// public static void main(String str[])
	// {
	// try
	// {
	// final Vector<TelnetServer> mvtSession = new Vector<TelnetServer>();
	// class ExpireThread extends Thread
	// {
	//
	// public void ExpireThread()
	// {
	// }
	//
	// public void run()
	// {
	// while(true)
	// {
	// if(mvtSession == null || mvtSession.size() == 0)
	// {
	// try
	// {
	// Thread.sleep(1000);
	// }
	// catch(InterruptedException ex)
	// {
	// }
	// continue;
	// }
	// Vector<TelnetServer> vtRemoveSession = new Vector<TelnetServer>();
	// for(int iIndex = 0;iIndex < mvtSession.size();iIndex++)
	// {
	// TelnetServer server = mvtSession.elementAt(iIndex);
	// if(new Thread(server).isAlive())
	// {
	// continue;
	// }
	// if(server.shutdown)
	// {
	// server.forceEndSession();
	// vtRemoveSession.addElement(server);
	// }
	// if(server.checkExpire())
	// {
	// server.print("\r\nSession closed because of expired!");
	// server.forceEndSession();
	// vtRemoveSession.addElement(server);
	// }
	// }
	// for(int i = 0;i < vtRemoveSession.size();i++)
	// {
	// mvtSession.remove(vtRemoveSession.elementAt(i));
	// }
	// try
	// {
	// Thread.sleep(1000);
	// }
	// catch(InterruptedException ex1)
	// {
	// }
	// }
	// }
	// }
	//
	// /////////////////////////////
	// ExpireThread threadCheckExpire = new ExpireThread();
	// threadCheckExpire.start();
	// /////////////////////////////
	// FileInputStream fis = new FileInputStream(System.getProperty("user.dir")
	// +
	// "/conf/SimulatorServerConfig.txt");
	// String fileCommand = System.getProperty("user.dir") +
	// "/conf/Command.txt";
	// com.fss.dictionary.Dictionary mdic = new com.fss.dictionary.Dictionary(
	// fis);
	// int mPort = Integer.parseInt(!mdic.getString("PortListener").trim().
	// equals("") ?
	// mdic.getString("PortListener").trim() :
	// "1212");
	// int miMaxSession = Integer.parseInt(!mdic.getString("MaxSession").trim().
	// equals("") ?
	// mdic.getString("MaxSession") : "10");
	// ServerSocket msrvSocket = new ServerSocket(mPort);
	// System.out.println("Listen on port " + mPort);
	// while(msrvSocket != null)
	// {
	// Socket socket = msrvSocket.accept();
	// //DEPQ setSoLinger de neu server socket.close() thi client ko ghi du lieu
	// len OutputStream duoc nua
	// socket.setSoLinger(true,0);
	// SystemPrintStream sout = new SystemPrintStream(socket.getOutputStream());
	// TelnetInputStream sin = new TelnetInputStream(socket.getInputStream(),
	// sout);
	// TelnetServer server = new
	// TelnetServer(sin,sout,sout,socket,null,manager);
	// if(mvtSession.size() > miMaxSession)
	// {
	// //send command to client
	// socket.close();
	// return;
	// }
	// sin.setSession(server);
	// sout.setSession(server);
	// mvtSession.addElement(server);
	// Thread thread = new Thread(server);
	// thread.start();
	// server.setLastReceiveCommand(System.currentTimeMillis());
	// }
	// }
	// catch(IOException ex)
	// {
	// ex.printStackTrace();
	// System.out.print(ex.getMessage());
	// }
	// }
}
