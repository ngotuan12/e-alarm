package com.sc.robot.thread;

import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Date;

import com.fss.util.StringUtil;

public class test
{

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		String encodedInput = "";
		try
		{
			Date date = new Date(1378714235);
			System.out.println(StringUtil.format(date, "dd/MM/yyyy HH:mm:ss"));
//			URL urlServlet = new URL(
//					"http://10.0.1.46:8080/BenesseProductServer/ServletServer");
//			HttpURLConnection cn = (HttpURLConnection) urlServlet
//					.openConnection();
//			cn.setDoInput(true);
//			cn.setDoOutput(true);
//			cn.setUseCaches(false);
//			cn.setRequestProperty("Content-type",
//					"application/x-www-form-urlencoded");
////			OutputStream output = cn.getOutputStream();
//			//
//			// Send url-encoded data over as binary to servlet
//			DataOutputStream dos = new DataOutputStream(cn.getOutputStream());
//			encodedInput = URLEncoder.encode("device_name") + "="
//					+ URLEncoder.encode("fdsfdsf") + "&";
//			dos.writeBytes(encodedInput);
//			encodedInput = URLEncoder.encode("result_name") + "="
//					+ URLEncoder.encode("fdsfdsf");
//			dos.writeBytes(encodedInput);
//			dos.flush();
//			dos.close();
//			// Send request
////			output.flush();
////			output.close();
//			//
//			InputStream is = cn.getInputStream();
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

}
