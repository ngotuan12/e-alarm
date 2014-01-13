package com.sc.robot.thread;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;

public class test
{

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
//		String encodedInput = "";
		try
		{
			
			byte[] header = {0x01, 0x31};
//			byte[] byteHeader = new byte[header.length];
//			for(int i=0; i<byteHeader.length; i++) {
//				byteHeader[i] = (byte)header[i];
//			    
//			}
			for (byte b : header) {
				   System.out.format("0x%x ", b);
				}
			String strRequest = "msg to show";
//			byte[] bytes = java.nio.ByteBuffer.allocate(4).putInt(1695609641).array();
			byte[] bytes = strRequest.getBytes();
			for (byte b : bytes) {
			   System.out.format("0x%x ", b);
			}
			byte footer = 0x04;
			System.out.println(String.valueOf((char)footer));
//			System.out.println(String.valueOf(0x01));
		    // Prints [2, 5, 1, 5, m, s, g,  , t, o,  , s, h, o, w]
//		    System.out.println(Arrays.toString(concat(command, msg)));
//			byte[] by = stringToBytesUTFCustom("aaa");
//			System.out.println(by);
//			byte byteHeader = 0x00;
//			char cVersion = '1';
//			byte byteEnd = 0x04;
//			System.out.println(byteHeader);
//			System.out.println(cVersion);
//			System.out.println(byteEnd);
//			System.out.println(byteHeader+cVersion+byteEnd);
			Socket client = new Socket("192.168.1.110", 80);
//			Socket client = new Socket("54.243.244.187", 8888);
			System.out.println("Just connected to "
					+ client.getRemoteSocketAddress());
			OutputStream outToServer = client.getOutputStream();
			DataOutputStream out = new DataOutputStream(outToServer);
			JSONObject request = new JSONObject();
			request.put("cmd", "server_cmd");
			JSONObject content = new JSONObject();
			content.put("ver", 1);
			content.put("type", "request");
			//get connection list
//			content.put("cmd", "get_connection_list");
			//set_report_interval
			content.put("cmd", "remove_devices");
			//body
			JSONObject body = new JSONObject();
//			body.put("devices_id",12);
//			body.put("cmd","get_current_reading");
//			body.put("device_id", "11");
			body.put("devices",new JSONArray("[{ \"device_id\": 12}]"));
//			body.put("interval_minutes",5);
//			body.put("servers",new JSONArray("[{\"host\": \"192.168.1.110\", \"port\": 80,\"keepalive_secs\": 60},{\"host\": \"192.168.1.1\", \"port\": 80, \"keepalive_secs\": 60},{\"host\": \"54.243.244.187\",\"port\": 8086,\"keepalive_secs\": 30}]"));
			content.put("body", body);
			request.put("content", content);
			System.out.println(0x01+request.toString()+0x0A+0x0D );
			String strheader = String.valueOf((char)0x01);
			out.writeUTF(strheader+ request.toString()+String.valueOf((char)0x0A)+String.valueOf((char)0x0D));
			InputStream inFromServer = client.getInputStream();
//			DataInputStream in = new DataInputStream(inFromServer);
			BufferedReader buffer = new BufferedReader(new InputStreamReader(inFromServer));
			BufferedReader stdIn =
			        new BufferedReader(
			            new InputStreamReader(System.in));
			String str;
			while (true)
			{
				int curr = -1;
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				if ( (curr = inFromServer.read()) != -1)
				{
					 baos.write(curr);
					 System.out.println(baos.toString("UTF-8"));
				}
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}
	private static char[] concat(final byte[] bytes, final String str) {
	    final StringBuilder sb = new StringBuilder();
	    for (byte b : bytes) {
	        sb.append(b);
	    }
	    sb.append(str);
	    return sb.toString().toCharArray();
	}
	public static byte[] stringToBytesUTFCustom(String str) {
		 char[] buffer = str.toCharArray();
		 byte[] b = new byte[buffer.length << 1];
		 for(int i = 0; i < buffer.length; i++) {
		  int bpos = i << 1;
		  b[bpos] = (byte) ((buffer[i]&0xFF00)>>8);
		  b[bpos + 1] = (byte) (buffer[i]&0x00FF);
		 }
		 return b;
		}
	/*
	 * public class GreetingClient { public static void main(String [] args) {
	 * String serverName = args[0]; int port = Integer.parseInt(args[1]); try {
	 * System.out.println("Connecting to " + serverName + " on port " + port);
	 * Socket client = new Socket(serverName, port);
	 * System.out.println("Just connected to " +
	 * client.getRemoteSocketAddress()); OutputStream outToServer =
	 * client.getOutputStream(); DataOutputStream out = new
	 * DataOutputStream(outToServer);
	 * 
	 * out.writeUTF("Hello from " + client.getLocalSocketAddress()); InputStream
	 * inFromServer = client.getInputStream(); DataInputStream in = new
	 * DataInputStream(inFromServer); System.out.println("Server says " +
	 * in.readUTF()); client.close(); }catch(IOException e) {
	 * e.printStackTrace(); } } }
	 */
}
