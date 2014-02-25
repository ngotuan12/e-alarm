package com.ar.bean;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.ar.util.AppProcessor;
import com.fss.sql.Database;
import com.fss.util.AppException;

public class DeviceManagementBean extends AppProcessor
{

	@Override
	public void doGet() throws Exception
	{
		
	}

	@Override
	public void doPost() throws Exception
	{
		String request_type = request.getString("Method");
		switch (request_type)
		{
			case "form_load":
				break;
			default:
				throw new AppException("EAS-DV-MN-001","API does not exists!");
		}
	}
	public void formLoad() throws Exception
	{
		PreparedStatement pstm = null;
		ResultSet rs = null;
		String strSQL = "";
		try
		{
			//SQL
			
			//open connection
			open();
			//Prepare 
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
	public void doDelete() throws Exception
	{
		
	}

}
