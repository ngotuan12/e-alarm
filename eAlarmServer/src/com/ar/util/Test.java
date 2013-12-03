package com.ar.util;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;

public class Test
{

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		try
		{
			JSONObject obj = new JSONObject();
			
			JSONArray arr = new JSONArray();
			arr.put(new JSONObject());
			arr.put(new JSONObject());
			arr.put(new JSONObject());
			obj.put("data", arr);
			System.out.println(obj.toString());
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

}
