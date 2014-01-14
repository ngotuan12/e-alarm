package com.sc.robot.util;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.fss.dictionary.Dictionary;

public class Util
{
	public static Dictionary mDic;
	static
	{

	}

	public static JSONArray concatArray(JSONArray... arrs) throws JSONException
	{
		JSONArray result = new JSONArray();
		for (JSONArray arr : arrs)
		{
			for (int i = 0; i < arr.length(); i++)
			{
				result.put(arr.get(i));
			}
		}
		return result;
	}

	/**
	 * @param rs
	 * @return
	 * @throws SQLException
	 * @throws JSONException
	 */
	public static JSONArray convertToJSONArray(ResultSet rs)
			throws SQLException, JSONException
	{
		JSONArray json = new JSONArray();
		ResultSetMetaData rsmd = rs.getMetaData();

		while (rs.next())
		{
			int numColumns = rsmd.getColumnCount();
			JSONObject obj = new JSONObject();
			for (int i = 1; i < numColumns + 1; i++)
			{
				String column_name = rsmd.getColumnLabel(i);

				if (rsmd.getColumnType(i) == java.sql.Types.ARRAY)
				{
					obj.put(column_name, rs.getArray(column_name));
				}
				else if (rsmd.getColumnType(i) == java.sql.Types.BIGINT)
				{
					obj.put(column_name, rs.getInt(column_name));
				}
				else if (rsmd.getColumnType(i) == java.sql.Types.BOOLEAN)
				{
					obj.put(column_name, rs.getBoolean(column_name));
				}
				else if (rsmd.getColumnType(i) == java.sql.Types.BLOB)
				{
					obj.put(column_name, rs.getBlob(column_name));
				}
				else if (rsmd.getColumnType(i) == java.sql.Types.DOUBLE)
				{
					obj.put(column_name, rs.getDouble(column_name));
				}
				else if (rsmd.getColumnType(i) == java.sql.Types.FLOAT)
				{
					obj.put(column_name, rs.getFloat(column_name));
				}
				else if (rsmd.getColumnType(i) == java.sql.Types.INTEGER)
				{
					obj.put(column_name, rs.getInt(column_name));
				}
				else if (rsmd.getColumnType(i) == java.sql.Types.NVARCHAR)
				{
					obj.put(column_name, rs.getNString(column_name));
				}
				else if (rsmd.getColumnType(i) == java.sql.Types.VARCHAR)
				{
					obj.put(column_name, rs.getString(column_name));
				}
				else if (rsmd.getColumnType(i) == java.sql.Types.TINYINT)
				{
					obj.put(column_name, rs.getInt(column_name));
				}
				else if (rsmd.getColumnType(i) == java.sql.Types.SMALLINT)
				{
					obj.put(column_name, rs.getInt(column_name));
				}
				else if (rsmd.getColumnType(i) == java.sql.Types.DATE)
				{
					obj.put(column_name, rs.getDate(column_name));
				}
				else if (rsmd.getColumnType(i) == java.sql.Types.TIMESTAMP)
				{
					obj.put(column_name, rs.getTimestamp(column_name));
				}
				else
				{
					obj.put(column_name, rs.getObject(column_name));
				}
			}
			json.put(obj);
		}

		return json;
	}
}
