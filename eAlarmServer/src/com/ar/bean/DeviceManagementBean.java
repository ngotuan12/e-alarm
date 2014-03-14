package com.ar.bean;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;

import com.ar.util.AppProcessor;
import com.ar.util.Util;
import com.fss.sql.Database;
import com.fss.util.AppException;
import com.mysql.jdbc.Statement;

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
		case "form_device_none_load":
			formDeviceNoneLoad();
			break;
		case "form_device_none_delete":
			deleteDevice();
			break;
		case "get_device_detail":
			getDeviceDetail();
			break;
		case "add_device":
			addDevice();
			break;
		case "update_device":
			updateDevice();
			break;
		default:
			throw new AppException("EAS-DV-MN-001", "API does not exists!");
		}
	}
	/**
	 * 
	 */
	public void deleteDevice() throws Exception
	{
		PreparedStatement pstm = null;
		ResultSet rs = null;
		String strSQL = "";
		try
		{
			int iDeviceID = request.getInt("device_id");
			//open
			open();
			//delete device issue
			strSQL = "DELETE FROM device_command_log WHERE device_id = ? ";
			pstm = mcnMain.prepareStatement(strSQL);
			pstm.setInt(1, iDeviceID);
			pstm.executeUpdate();
			pstm.close();
			//delete device issue
			strSQL = "DELETE FROM device_issue WHERE device_id = ? ";
			pstm = mcnMain.prepareStatement(strSQL);
			pstm.setInt(1, iDeviceID);
			pstm.executeUpdate();
			pstm.close();
			//delete device infor
			strSQL = "DELETE FROM device_infor WHERE device_id = ? ";
			pstm = mcnMain.prepareStatement(strSQL);
			pstm.setInt(1, iDeviceID);
			pstm.executeUpdate();
			pstm.close();
			//delete device
			strSQL = "DELETE FROM device WHERE id = ? "; 
			pstm = mcnMain.prepareStatement(strSQL);
			pstm.setInt(1, iDeviceID);
			pstm.executeUpdate();
			pstm.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw e;
		}
		finally
		{
			Database.closeObject(rs);
			Database.closeObject(pstm);
			close();
		}
	}
	/**
	 * @author TuanNA
	 * @throws Exception
	 */
	public void getDeviceDetail() throws Exception
	{
		PreparedStatement pstm = null;
		ResultSet rs = null;
		String strSQL = "";
		try
		{
			int iDeviceID = request.getInt("device_id");
			// device properties
			strSQL = "SELECT a.id,a.status,b.name,b.code,b.symbol,a.value, "
					+ "b.min_alarm,b.max_alarm, "
					+ "(CASE WHEN a.value<= b.min_alarm or a.value>=b.max_alarm THEN '0' "
					+ "ELSE '1' end) alarm_status "
					+ "FROM device_infor a,device_properties b "
					+ "WHERE a.device_pro_id = b.id "
					+ "AND a.device_id = ? AND status = '1' ";
			// open connection
			open();
			// Prepare
			pstm = mcnMain.prepareStatement(strSQL);
			// set parameter
			pstm.setInt(1, iDeviceID);
			// execute
			rs = pstm.executeQuery();
			// response
			response.put("properties", Util.convertToJSONArray(rs));
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw e;
		}
		finally
		{
			Database.closeObject(rs);
			Database.closeObject(pstm);
			close();
		}
	}

	/**
	 * @author: TuanNA
	 * 
	 * @since: 27/02/2014
	 * 
	 * @version: 1.0
	 */
	public void updateDevice() throws Exception
	{
		PreparedStatement pstm = null;
		ResultSet rs = null;
		String strSQL = "";
		try
		{
			// get parameter
			String strCode = request.getString("code");
			String strName = request.getString("name");
			String strAddress = request.getString("address");
			String strShortAddress = request.getString("short_address");
			String strMacAddress = request.getString("mac_add");
			Double lat = request.getDouble("lat");
			Double lng = request.getDouble("lng");
			int iAreaID = request.getInt("area_id");
			int deviceID = request.getInt("device_id");
			// SQL
			strSQL = "UPDATE device " + "SET code = ? , name = ?, address= ?, "
					+ "short_address = ?, lat = ?, lng=?, "
					+ "mac_add =?, area_id = ? " + "WHERE id = ? ";
			// open connection
			open();
			// prepare
			pstm = mcnMain.prepareStatement(strSQL);
			// set parameter
			pstm.setString(1, strCode);
			pstm.setString(2, strName);
			pstm.setString(3, strAddress);
			pstm.setString(4, strShortAddress);
			pstm.setDouble(5, lat);
			pstm.setDouble(6, lng);
			pstm.setString(7, strMacAddress);
			pstm.setInt(8, iAreaID);
			pstm.setInt(9, deviceID);
			// execute query
			pstm.executeUpdate();
			// close statement
			pstm.close();
			// Insert properties
			JSONArray properties = request.getJSONArray("properties");
			// SQL
			strSQL = "UPDATE device_infor " + "SET status = ? "
					+ "WHERE device_id = ? " + "AND device_pro_id = ? ";
			// prepare
			pstm = mcnMain.prepareStatement(strSQL);
			for (int i = 0; i < properties.length(); i++)
			{
				JSONObject property = properties.getJSONObject(i);
				int propertyID = property.getInt("id");
				String strStatus = property.getString("status");
				// set parameter

				pstm.setString(1, strStatus);
				pstm.setInt(2, deviceID);
				pstm.setInt(3, propertyID);
				// execute
				pstm.executeUpdate();
			}
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

	/**
	 * @author: TuanNA
	 * 
	 * @since: 27/02/2014
	 * 
	 * @version: 1.0
	 */
	public void addDevice() throws Exception
	{
		PreparedStatement pstm = null;
		ResultSet rs = null;
		String strSQL = "";
		try
		{
			// get parameter
			String strCode = request.getString("code");
			String strName = request.getString("name");
			String strAddress = request.getString("address");
			String strShortAddress = request.getString("short_address");
			String strMacAddress = request.getString("mac_add");
			Double lat = request.getDouble("lat");
			Double lng = request.getDouble("lng");
			int iAreaID = request.getInt("area_id");
			String strStatus = "0";
			int deviceID = -1;
			// SQL
			strSQL = "INSERT INTO device(`code`,`name`,address,short_address,lat,lng,mac_add,status,area_id)"
					+ "VALUES(?,?,?,?,?,?,?,?,?)";
			// open connection
			open();
			// prepare
			pstm = mcnMain.prepareStatement(strSQL,
					Statement.RETURN_GENERATED_KEYS);
			// set parameter
			pstm.setString(1, strCode);
			pstm.setString(2, strName);
			pstm.setString(3, strAddress);
			pstm.setString(4, strShortAddress);
			pstm.setDouble(5, lat);
			pstm.setDouble(6, lng);
			pstm.setString(7, strMacAddress);
			pstm.setString(8, strStatus);
			pstm.setInt(9, iAreaID);
			// execute query
			pstm.executeUpdate();
			// get insert id
			rs = pstm.getGeneratedKeys();
			if (rs.next()) deviceID = rs.getInt(1);
			else throw new AppException("EAS-DV-MN-002", "Can't insert device!");
			// close statement
			rs.close();
			pstm.close();
			// Insert properties
			JSONArray properties = request.getJSONArray("properties");
			// SQL
			strSQL = "INSERT INTO device_infor(device_id,device_pro_id,status)"
					+ "VALUES(?,?,?)";
			// prepare
			pstm = mcnMain.prepareStatement(strSQL);
			for (int i = 0; i < properties.length(); i++)
			{
				JSONObject property = properties.getJSONObject(i);
				int propertyID = property.getInt("id");
				strStatus = property.getString("status");
				// set parameter
				pstm.setInt(1, deviceID);
				pstm.setInt(2, propertyID);
				pstm.setString(3, strStatus);
				// execute
				pstm.executeUpdate();
			}
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

	/**
	 * @throws Exception
	 */
	public void formDeviceNoneLoad() throws Exception
	{
		PreparedStatement pstm = null;
		ResultSet rs = null;
		String strSQL = "";
		try
		{
			strSQL = "SELECT * FROM device ORDER BY area_code ";
			// open connection
			open();
			// Prepare
			pstm = mcnMain.prepareStatement(strSQL);
			// ResultSet
			rs = pstm.executeQuery();
			// response
			response.put("device_list", Util.convertToJSONArray(rs));
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

	/**
	 * @throws Exception
	 */
	public void formDeviceAddLoad() throws Exception
	{
		PreparedStatement pstm = null;
		ResultSet rs = null;
		String strSQL = "";
		try
		{
			String strAction = request.getString("action");
			// SQL
			strSQL = "SELECT * FROM area WHERE level = 2 AND status = '1' ORDER BY woodenleg ";
			// open connection
			open();
			// Prepare
			pstm = mcnMain.prepareStatement(strSQL);
			// ResultSet
			rs = pstm.executeQuery();
			// response
			response.put("area_list", Util.convertToJSONArray(rs));
			// close
			pstm.close();
			rs.close();
			if (strAction.equals("ADD"))
			{
				// device properties
				strSQL = "SELECT *,'0' status FROM device_properties";
				// Prepare
				pstm = mcnMain.prepareStatement(strSQL);

			}
			else if (strAction.equals("EDIT"))
			{
				int iDeviceID = request.getInt("device_id");
				// check device exists
				strSQL = "SELECT 1 FROM device WHERE id = ? ";
				pstm = mcnMain.prepareStatement(strSQL);
				// set parameter
				pstm.setInt(1, iDeviceID);
				// ResultSet
				rs = pstm.executeQuery();
				if (!rs.next()) throw new AppException("EAS-DV-MN-003",
						"Can't find device!");
				// close statement
				rs.close();
				pstm.close();
				// device properties
				strSQL = "SELECT b.*,a.status "
						+ "FROM device_infor a,device_properties b "
						+ "WHERE a.device_pro_id = b.id "
						+ "AND a.device_id = ? ";
				// Prepare
				pstm = mcnMain.prepareStatement(strSQL);
				// set parameter
				pstm.setInt(1, iDeviceID);
			}
			// ResultSet
			rs = pstm.executeQuery();
			// response
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
