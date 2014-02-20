package com.ar.bean;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.codehaus.jettison.json.JSONArray;

import com.ar.util.AppProcessor;
import com.ar.util.Util;
import com.fss.sql.Database;

public class UserBean extends AppProcessor
{
	public JSONArray ExcuteQuery(String Query, int TypeExcute) throws Exception
	{
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try
		{
			// open connection
			open();
			// prepare
			pstm = mcnMain.prepareStatement(Query);
			if (TypeExcute == 0)
			{
				rs = pstm.executeQuery();
			}
			else
			{
				pstm.executeUpdate();
				rs = null;
			}
			JSONArray arr = new JSONArray();
			if (rs != null) arr = Util.convertToJSONArray(rs);
			return arr;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			throw ex;
		}
		finally
		{
			Database.closeObject(pstm);
			Database.closeObject(rs);
			close();
		}
	}

	public JSONArray UpdateNewPassword(String Password, String ID)
			throws Exception
	{
		String strSQL = "UPDATE user SET password='" + Password + "'"
				+ " WHERE id='" + ID + "'";
		return ExcuteQuery(strSQL, 1);
	}

	private void UpdateUserInfo(String Fullname, String gender, String ID)
			throws Exception
	{
		String strSQL = "UPDATE user SET fullname=N'" + Fullname + "',sex='"
				+ gender + "'" + " WHERE id=" + ID + "";
		ExcuteQuery(strSQL, 1);
	}

	@Override
	public void doPost() throws Exception
	{
		String Method = (String) request.getString("Method");
		switch (Method)
		{
		case "UpdatePass":
			String pass = request.getString("newpassword");
			UpdateNewPassword(pass, request.getString("id").toString());
			response.put("Mess", "Success");
			break;
		case "UpdateDetail":
			String fullname = request.getString("fullname");
			String gender = request.getString("gender");
			UpdateUserInfo(fullname, gender, request.getString("id").toString());
			response.put("Mess", "Success");
			break;
		default:
			response.put("Mess", "API does not exist");
			break;
		}// TODO Auto-generated method stub
	}

	@Override
	public void doGet() throws Exception
	{

		// TODO Auto-generated method stub

	}

	@Override
	public void doDelete() throws Exception
	{
		// TODO Auto-generated method stub
	}
}
