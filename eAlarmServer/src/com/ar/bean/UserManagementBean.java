package com.ar.bean;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.codehaus.jettison.json.JSONArray;

import com.ar.util.AppProcessor;
import com.ar.util.Util;
import com.fss.sql.Database;

public class UserManagementBean extends AppProcessor
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

	private JSONArray GetAllUser() throws Exception
	{
		String strSQL = "select id as ID,dept_id as IdDept,owner_id as OwnerID,username as UserName,"
				+ "password as Password,(case when status = '1' then 'Kích Hoạt' else 'Không Kích Hoạt' end) Status,sex as Sex,email as Email,phone as Phone,"
				+ "id_no as IDNo,address as Address,DATE_FORMAT(birth_day,'%Y-%m-%d') as BirthDay,create_date as CreateDate,fullname as FullName,email,phone"
				+ " from user";
		return ExcuteQuery(strSQL, 0);
	}

	private void AddUser(String username, String password, String email,
			String phone, String fullname, String address, String bithday,
			String status, String sex) throws Exception
	{
		String strSQL = "INSERT INTO user (username,password,email,phone,fullname,address,birth_day,status,sex) VALUES ('"
				+ username
				+ "','"
				+ password
				+ "','"
				+ email
				+ "','"
				+ phone
				+ "','"
				+ fullname
				+ "','"
				+ address
				+ "',str_to_date('"
				+ bithday + "','%Y-%m-%d'),'" + status + "','" + sex + "')";
		// prepare
		ExcuteQuery(strSQL, 1);
	}

	private void EditUser(String username, String email, String phone,
			String fullname, String address, String bithday, String status,
			String sex, String id) throws Exception
	{
		String strSQL = "UPDATE user SET username='" + username + "',email='"
				+ email + "',phone='" + phone + "',address='" + address
				+ "',birth_day='" + bithday + "',status='" + status
				+ "',fullname='" + fullname + "',sex='" + sex + "'"
				+ " WHERE id='" + id + "'";
		// prepare
		ExcuteQuery(strSQL, 1);
	}

	private void DeleteUser(String id) throws Exception
	{
		String strSQL = "DELETE FROM user WHERE id='" + id + "'";
		// prepare
		ExcuteQuery(strSQL, 1);
	}

	@Override
	public void doGet() throws Exception
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void doPost() throws Exception
	{
		String Method = (String) request.getString("Method");
		switch (Method)
		{
		case "onGetAllUser":
			JSONArray GetAllUser = GetAllUser();
			response.put("ListUser", GetAllUser);
			response.put("Mess", "Success");
			break;
		case "onAddUser":
			AddUser(request.getString("username"),
					request.getString("password"), request.getString("email"),
					request.getString("phone"), request.getString("fullname"),
					request.getString("address"),
					request.getString("birth_day"),
					request.getString("status"), request.getString("sex"));
			response.put("Mess", "Success");
			break;
		case "onEditUser":
			EditUser(request.getString("username"), request.getString("email"),
					request.getString("phone"), request.getString("fullname"),
					request.getString("address"),
					request.getString("birth_day"),
					request.getString("status"), request.getString("sex"),
					request.getString("id"));
			response.put("Mess", "Success");
			break;
		case "onDeleteUser":
			DeleteUser(request.getString("id"));
			response.put("Mess", "Success");
			break;
		default:
			response.put("Mess", "API does not exist");
			break;
		}// TODO Auto-generated method stub
	}

	@Override
	public void doDelete() throws Exception
	{
		// TODO Auto-generated method stub

	}
}
