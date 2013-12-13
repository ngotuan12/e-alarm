package com.sc.robot.telnet;

import java.util.Timer;
import java.util.Vector;

import com.fss.thread.ParameterType;
import com.fss.thread.ThreadConstant;
import com.fss.util.AppException;
import com.sc.robot.dealgrabber.muachungTask;
import com.sc.robot.server.TelnetServer;


/**
 * DEPQ 2011
 * 
 * @version 1.0
 */
public class ThreadManagerTelnetSession extends com.fss.thread.ManageableThread {
	public ThreadManagerTelnetSession() {
	}

	private int miPort = 23;
	private String mstrUserName = "";
	private String mstrPassword = "";
	private int miMaxSession = 10;
	private String mstrPrompt = "$FPT:";
	private int miExpireDuration = 5 * 60 * 1000;
	private int miMessageTimeout = 5 * 60 * 1000;
	Vector<TelnetServer> mvtSession = new Vector<TelnetServer>();
	private boolean isDiff = true;

	public Vector getParameterDefinition() {
		Vector vtReturn = new Vector();
		
		vtReturn.addElement(createParameterDefinition("User", "",
				ParameterType.PARAM_TEXTBOX_MAX, "100"));
		vtReturn.addElement(createParameterDefinition("Password", "",
				ParameterType.PARAM_TEXTBOX_MAX, "100"));
		vtReturn.addElement(createParameterDefinition("MaxSession", "",
				ParameterType.PARAM_TEXTBOX_MAX, "100"));
		vtReturn.addElement(createParameterDefinition("Prompt", "",
				ParameterType.PARAM_TEXTBOX_MASK, ""));
		vtReturn.addElement(createParameterDefinition("ExpireDuration(ms)", "",
				ParameterType.PARAM_TEXTBOX_MASK, "999999"));

		Vector vtDiffTimeout = new Vector();
		vtDiffTimeout.addElement("No");
		vtDiffTimeout.addElement("Yes");
		vtReturn.addElement(createParameterDefinition("Use different timeout for Queue",
				"", ParameterType.PARAM_COMBOBOX, vtDiffTimeout,
				"Timeout for command"));
		vtReturn.addElement(createParameterDefinition("MessageTimeout(ms)", "",
				ParameterType.PARAM_TEXTBOX_MASK, "999999"));

		vtReturn.addAll(super.getParameterDefinition());
		return vtReturn;
	}

	public void fillParameter() throws AppException {
		miPort = loadInteger("TelnetPort");
		mstrUserName = loadMandatory("User");
		mstrPassword = loadMandatory("Password");
		miMaxSession = loadInteger("MaxSession");
		mstrPrompt = loadMandatory("Prompt");
		miExpireDuration = loadInteger("ExpireDuration(ms)");
		miMessageTimeout = loadInteger("MessageTimeout(ms)");
		String strDailyMode = loadMandatory("Use different timeout for Queue");
		isDiff = "Yes".equals(strDailyMode);
		super.fillParameter();
	}

	protected void beforeSession() throws Exception {
		log("Bf SS Done!");

	}

	protected void processSession() throws Exception {
		log("start run");
		new Timer();	
		muachungTask mc =  new muachungTask();
		while (miThreadCommand != ThreadConstant.THREAD_STOP) {
			if (mvtSession == null || mvtSession.size() == 0) {
				try {
					Thread.sleep(1);
					mc.run();
				} catch (InterruptedException ex) {
				
				continue;
				}
			}
		}
		};
	
	public void endSession(TelnetServer ts){
		if(mvtSession.remove(ts)){
			ts.forceEndSession();
		}
	}

	protected void afterSession() throws Exception {
		// remove all session
		Vector<TelnetServer> vtRemoveSession = new Vector<TelnetServer>();
		for (int iIndex = 0; iIndex < mvtSession.size(); iIndex++) {
			TelnetServer server = mvtSession.elementAt(iIndex);
			//
			server.printend("Your session is interupt because telnet server shutdown");
			server.forceEndSession();
			vtRemoveSession.addElement(server);
		}
		for (int i = 0; i < vtRemoveSession.size(); i++) {
			mvtSession.remove(vtRemoveSession.elementAt(i));
		}
	}
}
