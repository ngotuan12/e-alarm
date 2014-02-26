package com.ar.bean;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.ar.util.AppProcessor;
import com.ar.util.Util;
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
			case "form_device_add_load":
				formDeviceAddLoad();
				break;
			default:
				throw new AppException("EAS-DV-MN-001","API does not exists!");
		}
	}
	public void formDeviceAddLoad() throws Exception
	{
		PreparedStatement pstm = null;
		ResultSet rs = null;
		String strSQL = "";
		try
		{
			//SQL
			strSQL = "SELECT * FROM area WHERE level = 2 AND status = '1' ORDER BY woodenleg ";
			//open connection
			open();
			//Prepare 
			pstm = mcnMain.prepareStatement(strSQL);
			//ResultSet
			rs = pstm.executeQuery();
			//response
			response.put("area_list", Util.convertToJSONArray(rs));
			//close
			pstm.close();
			rs.close();
			//device properties
			strSQL = "SELECT * FROM device_properties";
			//Prepare 
			pstm = mcnMain.prepareStatement(strSQL);
			//ResultSet
			rs = pstm.executeQuery();
			//response
			response.put("properties_list", Util.convertToJSONArray(rs));
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
