package com.ar.bean;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;

import com.ar.util.AppProcessor;
import com.ar.util.Util;
import com.fss.sql.Database;
import com.mysql.jdbc.Statement;

public class LoginBean extends AppProcessor
{
	public void onLogin() throws Exception
	{
		String strSQL = "";
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try
		{
			String strPhoneNumber = (String) request.getString("phone_number");
			// open connection
			open();
			strSQL = "SELECT name,phone_number,status,amount,free_times,buy_times "
					+ "FROM users " + "WHERE phone_number = ? ";
			// prepare
			pstm = mcnMain.prepareStatement(strSQL);
			pstm.setString(1, strPhoneNumber.toUpperCase());
			rs = pstm.executeQuery();
			// get JSON data
			JSONArray arr = Util.convertToJSONArray(rs);
			// if account not exists
			if (arr.length() == 0)
			{
				// close statement
				Database.closeObject(pstm);
				Database.closeObject(rs);
				// insert new account
				strSQL = "INSERT INTO users(phone_number,status,amount,free_times,buy_times) "
						+ "VALUES(?,'1',0,5,0)";
				// prepare
				pstm = mcnMain.prepareStatement(strSQL);
				pstm.setString(1, strPhoneNumber.toUpperCase());
				pstm.executeUpdate();
				// response
				response.put("free_times", 5);
				response.put("buy_times", 0);
				response.put("amount", 0);
			}
			// if account exists
			else
			{
				JSONObject userInfo = (JSONObject) arr.get(0);

				// prepare
				pstm = mcnMain.prepareStatement(strSQL);
				pstm.setString(1, strPhoneNumber.toUpperCase());
				pstm.executeUpdate();
				// response
				response.put("free_times", userInfo.get("free_times"));
				response.put("buy_times", userInfo.get("buy_times"));
				response.put("amount", userInfo.get("amount"));
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			throw ex;
		}
		finally
		{
			close();
		}
	}

	public void onCheckLogin() throws Exception
	{
		String strSQL = "";
		PreparedStatement pstm = null;
		ResultSet rs = null;
		JSONObject userInfo;
		try
		{
			String strFaceBookID = request.getString("facebook_id");
			String strName = request.getString("name");
			String strAvatar = request.getString("avatar");
			strSQL = "SELECT a.id,a.facebook_id,a.user_name, "
					+ "a.name,a.avatar,a.min_time, " 
					+ "a.status, "
					+ "(select count(*) from user_exam WHERE user_id = a.id) num_exam, "
					+ "(select sum(point) from user_exam WHERE user_id = a.id) total_point "
					+ "FROM users a " 
					+ "WHERE a.facebook_id = ? ";
			// open connection
			open();
			// prepare
			pstm = mcnMain.prepareStatement(strSQL);
			pstm.setString(1, strFaceBookID.toUpperCase());
			rs = pstm.executeQuery();
			// get JSON data
			JSONArray arr = Util.convertToJSONArray(rs);
			// if account not exists
			if (arr.length() == 0)
			{
				int userID = 0;
				// close statement
				Database.closeObject(pstm);
				Database.closeObject(rs);
				// insert new account
				strSQL = "INSERT INTO users(facebook_id,`name`,avatar,status,amount,free_times,buy_times) "
						+ "VALUES(?,?,?,'1',0,5,0)";
				// prepare
				pstm = mcnMain.prepareStatement(strSQL,
						Statement.RETURN_GENERATED_KEYS);
				pstm.setString(1, strFaceBookID.toUpperCase());
				pstm.setString(2, strName);
				pstm.setString(3, strAvatar);
				pstm.executeUpdate();
				// user id
				rs = pstm.getGeneratedKeys();
				if (rs.next()) userID = rs.getInt(1);
				// user info
				userInfo = new JSONObject();
				userInfo.put("id", userID);
				userInfo.put("facebook_id", strFaceBookID);
				userInfo.put("user_name", "");
				userInfo.put("name", strName);
				userInfo.put("avatar", strAvatar);
				userInfo.put("status", "1");
				userInfo.put("min_time", 0);
				userInfo.put("num_exam", 0);
				userInfo.put("total_point", 0);
			}
			// if account exists
			else
			{
				userInfo = (JSONObject) arr.get(0);
				// close statement
				Database.closeObject(pstm);
				Database.closeObject(rs);
				// update user
				strSQL = "UPDATE users " + "SET name = ?, " + " avatar = ? "
						+ "WHERE facebook_id = ? ";
				// prepare
				pstm = mcnMain.prepareStatement(strSQL);

				pstm.setString(1, strName);
				pstm.setString(2, strAvatar);
				pstm.setString(3, strFaceBookID.toUpperCase());
				pstm.executeUpdate();
			}
			// close statement
			Database.closeObject(pstm);
			Database.closeObject(rs);
			// top of subject
			strSQL = "SELECT DISTINCT a.user_id,a.subject_id,b.`name` "
					+ "FROM user_exam a,users b " + "WHERE a.user_id = b.id "
					+ "ORDER BY a.point desc,a.end_time-a.start_time desc " 
					+ "LIMIT 5 ";
			// prepare
			pstm = mcnMain.prepareStatement(strSQL);
			// execute
			rs = pstm.executeQuery();
			// get JSON data
			JSONArray arrTop = Util.convertToJSONArray(rs);
			// response
			response.put("user_infor", userInfo);
			response.put("tops", arrTop);
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			throw ex;
		}
		finally
		{
			Database.closeObject(rs);
			Database.closeObject(pstm);
			close();
		}
	}

	@Override
	public void doGet() throws Exception
	{
		
	}

	@Override
	public void doPost() throws Exception
	{
		
	}

	@Override
	public void doDelete() throws Exception
	{
		// TODO Auto-generated method stub
		
	}
}
