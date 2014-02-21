package com.ar.bean;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.codehaus.jettison.json.JSONArray;
import com.ar.util.AppProcessor;
import com.ar.util.Util;
import com.fss.sql.Database;

public class UserManagementBean extends AppProcessor{
	
	public JSONArray ExcuteQuery(String Query, int TypeExcute) throws Exception {
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			// open connection
			open();
			// prepare
			pstm = mcnMain.prepareStatement(Query);
			if (TypeExcute == 0) {
				rs = pstm.executeQuery();
			} else {
				pstm.executeUpdate();
				rs = null;
			}
			JSONArray arr = new JSONArray();
			if (rs != null)
				arr = Util.convertToJSONArray(rs);
			return arr;
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		} finally {
			Database.closeObject(pstm);
			Database.closeObject(rs);
			close();
		}
	}
	
	private JSONArray GetAllUser() throws Exception {
		String strSQL = "select id as ID,dept_id as IdDept,owner_id as OwnerID,username as UserName,"
						+"password as Password,status as Status,sex as Sex,email as Email,phone as Phone,"
						+"id_no as IDNo,address as Address,birth_day as BirthDay,create_date as CreateDate,fullname as FullName"
						+" from user";
		return ExcuteQuery(strSQL, 0);
	}
	@Override
	public void doGet() throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void doPost() throws Exception {
		// TODO Auto-generated method stub
		JSONArray GetAllUser = GetAllUser();
		response.put("ListUser", GetAllUser);
		response.put("Mess", "Success");
	}

	@Override
	public void doDelete() throws Exception {
		// TODO Auto-generated method stub
		
	}
}
