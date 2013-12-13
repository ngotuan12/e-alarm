package com.sc.robot.thread;

import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.commons.io.IOUtils;
import org.codehaus.jettison.json.JSONObject;

import com.fss.sql.Database;
import com.fss.thread.ManageableThread;
import com.fss.thread.ThreadConstant;
import com.fss.util.AppException;
import com.sc.robot.util.AppServer;

public class ThreadProcessImage extends ManageableThread
{
	private Connection cnMySQL = null;
	private int iDelaytime = 30000;
	private PreparedStatement pstm = null;
	private PreparedStatement pstmUpdate = null;
	private ResultSet rs = null;
	private String strDeviceName;
	private String strResultName;
	private String strRequestID;

	@Override
	protected void beforeSession() throws Exception
	{
		log("..check connect database");
		try
		{
			//
			String strSQL = "SELECT id,device_name,result_name FROM request WHERE status = '1' ";
			// open connection
			cnMySQL = AppServer.getConnection("MySQL");
			// prepare statement
			pstm = cnMySQL.prepareStatement(strSQL);
			// prepare update
			strSQL = "UPDATE  request " + "SET status = ?,"
					+ "     description = ? " + "WHERE id = ? ";
			pstmUpdate = cnMySQL.prepareStatement(strSQL);
			// log monitor
			log("connected to database");
		}
		catch (Exception ex)
		{
			// log monitor
			log("can't connect to database");
			logError("Error: " + ex.toString());
		}
	}

	@Override
	protected void processSession() throws Exception
	{
		while (miThreadCommand != ThreadConstant.THREAD_STOP)
		{
			// get request
			rs = pstm.executeQuery();
			// if request found
			while (rs.next())
			{
				// get parameter
				strRequestID = rs.getString(1);
				strDeviceName = rs.getString(2);
				strResultName = rs.getString(3);
				logInfo("Processing request...");
				logInfo("id: " + strRequestID);
				logInfo("device: " + strDeviceName);
				logInfo("result: " + strResultName);
				// process
				processImage(strRequestID, strDeviceName, strResultName);
				// execute update
				pstmUpdate.executeUpdate();
			}
			// close result
			rs.close();
			// sleep
			Thread.sleep(iDelaytime * 1000);
		}
	}

	protected void afterSession() throws Exception
	{
		Database.closeObject(rs);
		Database.closeObject(pstm);
		Database.closeObject(cnMySQL);
	}

	public void fillParameter() throws AppException
	{
		iDelaytime = loadUnsignedInteger("DelayTime");
		super.fillParameter();
	}

	@SuppressWarnings({
			"deprecation"
	})
	public void processImage(String strID, String strDeviceName,
			String strResultName) throws Exception
	{
		String encodedInput = "";
		try
		{
			URL urlServlet = new URL(
					"http://10.0.1.46:8080/BenesseProductServer/ServletServer");
			HttpURLConnection cn = (HttpURLConnection) urlServlet
					.openConnection();
			cn.setDoInput(true);
			cn.setDoOutput(true);
			cn.setUseCaches(false);
			cn.setRequestProperty("Content-type",
					"application/x-www-form-urlencoded");
			// OutputStream output = cn.getOutputStream();
			//
			// Send url-encoded data over as binary to servlet
			DataOutputStream dos = new DataOutputStream(cn.getOutputStream());
			encodedInput = URLEncoder.encode("device_name") + "="
					+ URLEncoder.encode(strDeviceName) + "&";
			dos.writeBytes(encodedInput);
			encodedInput = URLEncoder.encode("result_name") + "="
					+ URLEncoder.encode(strResultName);
			dos.writeBytes(encodedInput);
			// Send request
			dos.flush();
			dos.close();
			// response
			InputStream is = cn.getInputStream();
			StringWriter writer = new StringWriter();
			IOUtils.copy(is, writer, "UTF-8");
			JSONObject response = new JSONObject(writer.toString());
			// if has Exception
			if (response.getString("Exception") != null
					&& !response.getString("Exception").equals(""))
			{
				throw new Exception(response.getString("Exception"));
			}
			// set parameter
			pstmUpdate.setString(1, "2");
			pstmUpdate.setString(2, "Request process successful ");
			pstmUpdate.setString(3, strID);
			//
			logInfo("Request has process succcessful!");
			logInfo("END.");
		}
		catch (Exception ex)
		{
			// set parameter
			pstmUpdate.setString(1, "3");
			pstmUpdate.setString(2, ex.getMessage());
			pstmUpdate.setString(3, strID);
			// print
			logInfo("Request has process false!");
			logInfo("END.");
			ex.printStackTrace();
		}
	}
}
