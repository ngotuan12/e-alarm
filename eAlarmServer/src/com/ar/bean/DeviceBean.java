package com.ar.bean;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;

import com.ar.util.AppProcessor;
import com.ar.util.Util;
import com.fss.sql.Database;

public class DeviceBean extends AppProcessor
{

	public JSONArray onGetDevicesInfoByDeviceID(int deviceID) throws Exception
	{
		String strSQL = "";
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try
		{
			// open connection
			open();
			strSQL = "SELECT id,device_id,infor_id,value"
					+ "FROM device_infor " + "WHERE device_id = ?";
			// prepare
			pstm = mcnMain.prepareStatement(strSQL);
			pstm.setInt(1, deviceID);
			rs = pstm.executeQuery();
			// get JSON data
			JSONArray arr = Util.convertToJSONArray(rs);
			// if account not exists
			if (arr.length() == 0)
			{
				// close statement
				Database.closeObject(pstm);
				Database.closeObject(rs);
				// response
				return null;
			}
			else
			{
				// response
				return arr;
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

	public void onGetDevicesandDeviceinfobyID() throws Exception
	{
		String strSQL = "";
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try
		{
			String strDeviceID = (String) request.getString("deviceID");
			// open connection
			open();
			strSQL = "SELECT id,code,area_id,area_code,address,lat,lng,status"
					+ "FROM device " + "WHERE id = ? and status = 1";
			// prepare
			pstm = mcnMain.prepareStatement(strSQL);
			pstm.setString(1, strDeviceID.toUpperCase());
			rs = pstm.executeQuery();
			// get JSON data
			JSONArray arr = Util.convertToJSONArray(rs);
			// if account not exists
			if (arr.length() == 0)
			{
				// close statement
				Database.closeObject(pstm);
				Database.closeObject(rs);
				// response
				response.put("error", "no device found");
			}
			else
			{
				String a = onGetDevicesInfoByDeviceID(
						Integer.parseInt(strDeviceID)).toString();
				arr.put(arr.length() + 1, a);
				// response
				response.put("device_info", arr);
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

	public void onEditDevicesinfobyID() throws Exception
	{
		String strSQL = "";
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try
		{
			String strdevice_id = (String) request.getString("device_id");
			String strinfor_id = (String) request.getString("infor_id");
			String strvalue = (String) request.getString("value");

			// open connection
			open();
			strSQL = "UPDATE student SET device_id = ?, " + " infor_id = ? "
					+ " value = ? " + " WHERE device_id = ? ";
			// prepare
			pstm = mcnMain.prepareStatement(strSQL);
			pstm.setString(1, strdevice_id);
			pstm.setInt(2, Integer.parseInt(strinfor_id));
			pstm.setString(3, strvalue);

			int done = pstm.executeUpdate(strSQL);

			if (done == 1)
			{
				// close statement
				Database.closeObject(pstm);
				Database.closeObject(rs);
				// response
				response.put("done", "edit sucess");
			}
			else
			{
				// response
				response.put("error", "have error with execute(validate data)");
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

	public void onGetDevicesByID() throws Exception
	{
		String strSQL = "";
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try
		{
			String strDeviceID = (String) request.getString("deviceID");
			// open connection
			open();
			strSQL = "SELECT id,code,area_id,area_code,address,lat,lng,status"
					+ "FROM device " + "WHERE id = ? and status = 1";
			// prepare
			pstm = mcnMain.prepareStatement(strSQL);
			pstm.setString(1, strDeviceID.toUpperCase());
			rs = pstm.executeQuery();
			// get JSON data
			JSONArray arr = Util.convertToJSONArray(rs);
			// if account not exists
			if (arr.length() == 0)
			{
				// close statement
				Database.closeObject(pstm);
				Database.closeObject(rs);
				// response
				response.put("error", "no device found");
			}
			else
			{
				JSONObject deviceInfo = (JSONObject) arr.get(0);
				// response
				response.put("device_info", deviceInfo);
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

	public void onGetAllDevices() throws Exception
	{
		String strSQL = "";
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try
		{
			// open connection
			open();
			strSQL = "SELECT id,code,area_id,area_code,address,lat,lng,status"
					+ "FROM device ";
			// prepare
			pstm = mcnMain.prepareStatement(strSQL);
			rs = pstm.executeQuery();
			// get JSON data
			JSONArray arr = Util.convertToJSONArray(rs);
			// if account not exists
			if (arr.length() == 0)
			{
				// close statement
				Database.closeObject(pstm);
				Database.closeObject(rs);
				// response
				response.put("error", "no device found");
			}
			else
			{
				// response
				response.put("all_devices_info", arr);
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

	public void onDisableDevices() throws Exception
	{
		String strSQL = "";
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try
		{
			String strDeviceID = (String) request.getString("deviceID");
			String status = (String) request.getString("status");
			// open connection
			open();
			strSQL = "UPDATE device SET status = ? " + " WHERE id = ? ";
			// prepare
			pstm = mcnMain.prepareStatement(strSQL);
			pstm.setInt(1, Integer.parseInt(status));
			pstm.setInt(2, Integer.parseInt(strDeviceID));

			int done = pstm.executeUpdate(strSQL);

			if (done == 1)
			{
				// close statement
				Database.closeObject(pstm);
				Database.closeObject(rs);
				// response
				response.put("done", "edit sucess");
			}
			else
			{
				// response
				response.put("error", "have error with execute(validate data)");
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

	public void onEditDevices() throws Exception
	{
		String strSQL = "";
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try
		{
			String strCode = (String) request.getString("code");
			String strarea_id = (String) request.getString("area_id");
			String strarea_code = (String) request.getString("area_code");
			String straddress = (String) request.getString("address");
			String strlat = (String) request.getString("lat");
			String strlng = (String) request.getString("lng");
			String strstatus = (String) request.getString("status");
			String strid = (String) request.getString("deviceID");

			// open connection
			open();
			strSQL = "UPDATE student SET code = ?, " + " area_id = ? "
					+ " area_code = ? " + " address = ? " + " lat = ? "
					+ " lng = ? " + " status = ? " + " WHERE id = ? ";
			// prepare
			pstm = mcnMain.prepareStatement(strSQL);
			pstm.setString(1, strCode);
			pstm.setInt(2, Integer.parseInt(strarea_id));
			pstm.setString(3, strarea_code);
			pstm.setString(4, straddress);
			pstm.setDouble(5, Double.parseDouble(strlat));
			pstm.setDouble(6, Double.parseDouble(strlng));
			pstm.setDouble(7, Integer.parseInt(strstatus));
			pstm.setDouble(8, Integer.parseInt(strid));

			int done = pstm.executeUpdate(strSQL);

			if (done == 1)
			{
				// close statement
				Database.closeObject(pstm);
				Database.closeObject(rs);
				// response
				response.put("done", "edit sucess");
			}
			else
			{
				// response
				response.put("error", "have error with execute(validate data)");
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

	public void onCreateNewDevices() throws Exception
	{
		String strSQL = "";
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try
		{
			String strCode = (String) request.getString("code");
			String strarea_id = (String) request.getString("area_id");
			String strarea_code = (String) request.getString("area_code");
			String straddress = (String) request.getString("address");
			String strlat = (String) request.getString("lat");
			String strlng = (String) request.getString("lng");
			String strstatus = (String) request.getString("status");

			// open connection
			open();
			strSQL = "INSERT INTO device " + "VALUES (?,?,?,?,?,?,?)";
			// prepare
			pstm = mcnMain.prepareStatement(strSQL);
			pstm.setString(1, strCode);
			pstm.setInt(2, Integer.parseInt(strarea_id));
			pstm.setString(3, strarea_code);
			pstm.setString(4, straddress);
			pstm.setDouble(5, Double.parseDouble(strlat));
			pstm.setDouble(6, Double.parseDouble(strlng));
			pstm.setDouble(7, Integer.parseInt(strstatus));

			int done = pstm.executeUpdate(strSQL);

			// if account not exists
			if (done == 1)
			{
				// close statement
				Database.closeObject(pstm);
				Database.closeObject(rs);
				// response
				response.put("done", "insert sucess");
			}
			else
			{
				// response
				response.put("error", "have error with execute(validate data)");
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

	public void onViewDevicesOnMap() throws Exception
	{
		String strSQL = "";
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try
		{
			// open connection
			open();
			strSQL = "SELECT id,code,area_id,area_code,address,lat,lng,status"
					+ "FROM device ";
			// prepare
			pstm = mcnMain.prepareStatement(strSQL);
			rs = pstm.executeQuery();
			// get JSON data
			JSONArray arr = Util.convertToJSONArray(rs);
			// if account not exists
			if (arr.length() == 0)
			{
				// close statement
				Database.closeObject(pstm);
				Database.closeObject(rs);
				// response
				response.put("error", "no device found");
			}
			else
			{
				// response
				response.put("all_devices_info", arr);
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

	public void doGet() throws Exception
	{
		// TODO Auto-generated method stub
	}

	@Override
	public void doPost() throws Exception
	{

		String request_type = request.getString("data");
		switch (request_type)
		{
		case "onGetDevicesByID":
			onGetDevicesByID();
			break;
		case "onGetAllDevices":
			onGetAllDevices();
			break;
		default:
			response.put("error", "you must enter the correct API name");
			break;
		}

	}

	public void doDelete() throws Exception
	{

	}

}
