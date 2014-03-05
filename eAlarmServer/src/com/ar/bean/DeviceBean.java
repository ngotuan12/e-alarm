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

public class DeviceBean extends AppProcessor
{

	public JSONArray onGetDevicesInfoByDeviceID(String deviceID)
			throws Exception
	{
		String strSQL = "";
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try
		{
			// open connection
			open();
			strSQL = "SELECT a.id,a.status,b.name,b.code,b.symbol,b.type,a.value, "
					+ "b.min_alarm,b.max_alarm,b.`min`,b.`max`,  "
					+ "(CASE WHEN a.value<= b.min_alarm or a.value>=b.max_alarm THEN '0' "
					+ "ELSE '1' end) alarm_status "
					+ "FROM device_infor a,device_properties b "
					+ "WHERE a.device_pro_id = b.id "
					+ "AND a.device_id = ? "
					+ "AND a.status = 1 ";
			// prepare
			pstm = mcnMain.prepareStatement(strSQL);
			pstm.setString(1, deviceID);
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

	public JSONArray onGetDevicesLogByDeviceID(int deviceID) throws Exception
	{
		String strSQL = "";
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try
		{
			// open connection
			open();
			strSQL = "SELECT device_log.* , reason.* "
					+ "FROM device_log INNER JOIN reason "
					+ "ON device_log.reason_id = reason.id where device_log.device_id = ?";
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
			strSQL = "SELECT *" + " FROM device " + "WHERE id = ? ";
			// prepare
			pstm = mcnMain.prepareStatement(strSQL);
			pstm.setString(1, strDeviceID);
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
				response.put("Mess", "no device found");
			}
			else
			{
				arr.put(arr.length() + 1,
						onGetDevicesInfoByDeviceID(strDeviceID));
				// response
				response.put("device_info", arr);
				response.put("Mess", "Success");
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

	public void onGetDevicesandDeviceLogbyID() throws Exception
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
					+ " FROM device " + "WHERE id = ? and status = 1";
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
				response.put("Mess", "no device found");
			}
			else
			{
				arr.put(arr.length() + 1,
						onGetDevicesLogByDeviceID(Integer.parseInt(strDeviceID)));
				// response
				response.put("device_info", arr);
				response.put("Mess", "Success");
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
			String strdevice_pro_id = (String) request.getString("infor_id");
			String strvalue = (String) request.getString("value");

			// open connection
			open();
			strSQL = "UPDATE device_info SET value = ? "
					+ " WHERE device_id = ? and device_pro_id = ?";
			// prepare
			pstm = mcnMain.prepareStatement(strSQL);
			pstm.setString(1, strvalue);
			pstm.setInt(2, Integer.parseInt(strdevice_id));
			pstm.setInt(3, Integer.parseInt(strdevice_pro_id));

			int done = pstm.executeUpdate(strSQL);

			if (done == 1)
			{
				// close statement
				Database.closeObject(pstm);
				Database.closeObject(rs);
				// response
				response.put("Mess", "edit sucess");
			}
			else
			{
				// response
				response.put("Mess", "have error with execute(validate data)");
				response.put("Mess", "Success");
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
			strSQL = "SELECT *" + " FROM device "
					+ "WHERE id = ? and status = 1";
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
				response.put("Mess", "no device found");
			}
			else
			{
				JSONObject deviceInfo = (JSONObject) arr.get(0);
				// response
				response.put("device_info", deviceInfo);
				response.put("Mess", "Success");
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
			strSQL = "SELECT * "
					+ " FROM device ";
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
				response.put("Mess", "no device found");
			}
			else
			{
				// response
				response.put("all_devices_info", arr);
				response.put("Mess", "Success");
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

	public JSONArray onGetAllDevices1() throws Exception
	{
		String strSQL = "";
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try
		{
			// open connection
			open();
			strSQL = "SELECT * FROM device";
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
				return arr;
			}
			else
			{
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

	public void onGetAllDevicesByAreaIDViewOnMap() throws Exception
	{
		String strSQL = "";
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try
		{
			String strareaID = (String) request.getString("area_id");
			// open connection
			open();
			strSQL = "SELECT id,code,area_id,area_code,address,lat,lng,status"
					+ " FROM device " + "where area_id= ?";
			// prepare
			pstm = mcnMain.prepareStatement(strSQL);
			pstm.setInt(1, Integer.parseInt(strareaID));
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
				response.put("Mess", "no device found");
			}
			else
			{
				// response
				JSONArray Acooked = new JSONArray();
				JSONObject Ocooked = new JSONObject();
				for (int i = 0; i < arr.length(); i++)
				{

					Ocooked = arr.getJSONObject(i);
					Ocooked.put("list", (Object) onGetDevicesInfoByDeviceID(arr
							.getJSONObject(i).getString("id")));
					Acooked.put(Ocooked);

				}

				response.put("all_devices_byarea_info", Acooked);
				response.put("Mess", "Success");
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

	public void onGetAllDevicesWithPro() throws Exception
	{
		String strSQL = "";
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try
		{
			// open connection
			open();
			strSQL = "SELECT id,code,area_id,area_code,address,lat,lng,status"
					+ " FROM device ";
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
				response.put("Mess", "no device found");
			}
			else
			{
				// response
				JSONArray Acooked = new JSONArray();
				JSONObject Ocooked = new JSONObject();
				for (int i = 0; i < arr.length(); i++)
				{

					Ocooked = arr.getJSONObject(i);
					Ocooked.put("list", (Object) onGetDevicesInfoByDeviceID(arr
							.getJSONObject(i).getString("id")));
					Acooked.put(Ocooked);

				}

				response.put("all_devices_byarea_info", Acooked);
				response.put("Mess", "Success");
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
				response.put("Mess", "edit sucess");
				response.put("Mess", "Success");
			}
			else
			{
				// response
				response.put("Mess", "have error with execute(validate data)");
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
			strSQL = "UPDATE device SET code = ?, " + " area_id = ? "
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
				response.put("Mess", "edit sucess");
				response.put("Mess", "Success");
			}
			else
			{
				// response
				response.put("Mess", "have error with execute(validate data)");
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
			strSQL = "INSERT into device (code,area_id,area_code,address,lat,lng,status) VALUES"
					+ "(?,?,?,?,?,?,?)";
			// prepare
			pstm = mcnMain.prepareStatement(strSQL);
			pstm.setString(1, strCode);
			pstm.setInt(2, Integer.parseInt(strarea_id));
			pstm.setString(3, strarea_code);
			pstm.setString(4, straddress);
			pstm.setDouble(5, Double.parseDouble(strlat));
			pstm.setDouble(6, Double.parseDouble(strlng));
			pstm.setString(7, strstatus);

			int done = pstm.executeUpdate(strSQL);

			// if account not exists
			if (done == 1)
			{
				// close statement
				Database.closeObject(pstm);
				Database.closeObject(rs);
				// response
				response.put("Mess", "insert sucess");
				response.put("Mess", "Success");
			}
			else
			{
				// response
				response.put("Mess", "have error with execute(validate data)");
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
					+ " FROM device ";
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
				response.put("Mess", "no device found");

			}
			else
			{
				// response
				response.put("all_devices_info", arr);
				response.put("Mess", "Success");
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

	public void onGetDevicesInfoByDeviceID() throws Exception
	{
		String strSQL = "";
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try
		{
			String strDeviceID = (String) request.getString("deviceID");
			// open connection
			open();
			strSQL = "SELECT *" + " FROM device_infor " + "WHERE device_id = ?";
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
				response.put("Mess", "no device found");
			}
			else
			{
				// response
				response.put("device_info", arr);
				response.put("Mess", "Success");
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

	public void onGetDevicesByAreaCodeStatus() throws Exception
	{
		String strSQL = "";
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try
		{
			String strAreaCode = (String) request.getString("area_code");
			String strStatus = (String) request.getString("status");
			// open connection
			open();
			strSQL = "SELECT id,code,area_id,IF(connected_server IS NULL,'không kết nối',connected_server) Connected_Server,IF(mac_add IS NULL,'không kết nối',mac_add) Mac_Add,area_code,address,lat,lng,status"
					+ " FROM device "
					+ "where area_code LIKE "
					+ "'"
					+ strAreaCode + "%' ";
			if (strStatus.contentEquals("2") || strStatus.contentEquals("1")
					|| strStatus.contentEquals("0"))
			{
				strSQL += " AND status= " + strStatus;
			}
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
				response.put("Mess", "no device found");
			}
			else
			{
				// response
				JSONArray Acooked = new JSONArray();
				JSONObject Ocooked = new JSONObject();
				for (int i = 0; i < arr.length(); i++)
				{

					Ocooked = arr.getJSONObject(i);
					Ocooked.put("list", (Object) onGetDevicesInfoByDeviceID(arr
							.getJSONObject(i).getString("id")));
					Acooked.put(Ocooked);

				}

				response.put("all_devices_byarea_info", Acooked);
				response.put("Mess", "Success");
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

	}

	@Override
	public void doPost() throws Exception
	{
		String request_type = request.getString("Method");
		switch (request_type)
		{
		case "onFormMainLoad":
			onFormMainLoad();
			break;
		case "onGetDevicesInfoByDeviceID":
			onGetDevicesInfoByDeviceID();
			break;
		case "onGetDevicesByAreaCodeStatus":
			onGetDevicesByAreaCodeStatus();
			break;
		case "onGetDevicesByID":
			onGetDevicesByID();
			break;
		case "onGetAllDevices":
			onGetAllDevices();
			break;
		case "onCreateNewDevices":
			onCreateNewDevices();
			break;
		case "onEditDevices":
			onEditDevices();
			break;
		case "onDisableDevices":
			onDisableDevices();
			break;
		case "onGetDevicesandDeviceinfobyID":
			onGetDevicesandDeviceinfobyID();
			break;
		case "onEditDevicesinfobyID":
			onEditDevicesinfobyID();
			break;
		case "onViewDevicesOnMap":
			onViewDevicesOnMap();
			break;
		case "onGetAllDevicesByAreaID":
			onGetAllDevicesByAreaIDViewOnMap();
			break;
		case "onGetDevicesandDeviceLogbyID":
			onGetDevicesandDeviceLogbyID();
			break;
		case "onGetAllDevicesWithPro":
			onGetAllDevicesWithPro();
			break;
		case "onGetAllDevicePro":
			onGetAllDevicePro();
			break;
		case "onGetDeviceProByID":
			onGetDeviceProByID();
			break;
		case "onAddDevicePro":
			onAddDevicePro();
			break;
		case "onDelDevicePro":
			onDeleteDevicePro();
			break;
		case "onEditDevicePro":
			onEditDevicePro();
			break;
		case "onGetDevicePropertyByID":
			onGetDevicePropertyByID();
			break;
		case "get_device_detail_by_id":
			getDeviceDetailByDeviceId();
			break;
		default:
			response.put("error", "you must enter the correct API name");
			break;
		}

	}

	private void onDeleteDevicePro() throws Exception
	{
		String strSQL = "";
		PreparedStatement pstm = null;
		try
		{
			String strDeviceProID = request.getString("ID");
			// open connection
			open();
			strSQL = "DELETE FROM device_properties WHERE id='"
					+ strDeviceProID + "'";
			// prepare
			pstm = mcnMain.prepareStatement(strSQL);
			pstm.executeUpdate(strSQL);
			// Close statement
			pstm.close();
			// response
			strSQL = "DELETE FROM device_infor WHERE device_pro_id='"
					+ strDeviceProID + "'";
			// prepare
			pstm = mcnMain.prepareStatement(strSQL);
			pstm.executeUpdate(strSQL);

			response.put("Mess", "delete sucess");
			response.put("Mess", "Success");

		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			close();
		}

	}

	private void onEditDevicePro() throws Exception
	{
		String strSQL = "";
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try
		{
			String strdevice_pro_id = (String) request.getString("ID");
			String strname = (String) request.getString("name");
			String strcode = (String) request.getString("code");
			String strdescription = (String) request.getString("description");
			String strSymbol = (String) request.getString("symbol");
			String strmin = (String) request.getString("min");
			String strmax = (String) request.getString("max");
			String strminAlarm = (String) request.getString("min_Alarm");
			String strmaxAlarm = (String) request.getString("max_Alarm");
			String strType = (String) request.getString("type");
			// open connection
			open();
			strSQL = "UPDATE device_properties SET name=N'" + strname
					+ "',code='" + strcode + "',description=N'"
					+ strdescription + "',symbol='" + strSymbol + "',min='"
					+ strmin + "',max='" + strmax + "',min_alarm='"
					+ strminAlarm + "',type='" + strType + "',max_alarm='"
					+ strmaxAlarm + "'" + " WHERE id='" + strdevice_pro_id
					+ "'";

			pstm = mcnMain.prepareStatement(strSQL);
			int done = pstm.executeUpdate(strSQL);

			if (done == 1)
			{
				// close statement
				Database.closeObject(pstm);
				Database.closeObject(rs);
				// response
				response.put("Mess", "edit sucess");
			}
			else
			{
				// response
				response.put("Mess", "have error with execute(validate data)");
				response.put("Mess", "Success");
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

	private void onAddDevicePro() throws Exception
	{
		String strSQL = "";
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try
		{
			String strname = (String) request.getString("name");
			String strcode = (String) request.getString("code");
			String strdescription = (String) request.getString("description");
			String strSymbol = (String) request.getString("symbol");
			String strmin = (String) request.getString("min");
			String strmax = (String) request.getString("max");
			String strminAlarm = (String) request.getString("min_Alarm");
			String strmaxAlarm = (String) request.getString("max_Alarm");
			String strtype = (String) request.getString("type");

			// open connection
			open();
			strSQL = "INSERT INTO device_properties (name,code,description,symbol,min,max,min_alarm,max_alarm,type) VALUES ('"
					+ strname
					+ "','"
					+ strcode
					+ "','"
					+ strdescription
					+ "','"
					+ strSymbol
					+ "','"
					+ strmin
					+ "','"
					+ strmax
					+ "','"
					+ strminAlarm
					+ "','"
					+ strmaxAlarm
					+ "','"
					+ strtype + "')";
			// prepare
			pstm = mcnMain.prepareStatement(strSQL,
					Statement.RETURN_GENERATED_KEYS);
			pstm.executeUpdate();
			// get inserted key
			rs = pstm.getGeneratedKeys();
			int iPropertyID;
			if (rs.next()) iPropertyID = rs.getInt(1);
			else throw new AppException("EAS-DEVICCE-001",
					"Can't insert property!");
			// Close statement
			rs.close();
			pstm.close();
			// insert device infor
			strSQL = "INSERT INTO device_infor(device_id,device_pro_id) select id,"
					+ iPropertyID + " from device";
			// prepare
			pstm = mcnMain.prepareStatement(strSQL);
			pstm.executeUpdate(strSQL);
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

	private void onGetDeviceProByID() throws Exception
	{
		String strSQL = "";
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try
		{
			String strDeviceProID = (String) request.getString("ID");
			// open connection
			open();
			strSQL = "SELECT *" + " FROM device_properties "
					+ "WHERE id = ? and status = 1";
			// prepare
			pstm = mcnMain.prepareStatement(strSQL);
			pstm.setString(1, strDeviceProID.toUpperCase());
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
				response.put("Mess", "no device found");
			}
			else
			{
				JSONObject deviceInfo = (JSONObject) arr.get(0);
				// response
				response.put("device_pro", deviceInfo);
				response.put("Mess", "Success");
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

	private void onGetAllDevicePro() throws Exception
	{
		String strSQL = "";
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try
		{
			// open connection
			open();
			strSQL = "SELECT *,(case when type = '1' then 'Cảm Biến' else 'Công Tắc' end) Typeaa FROM device_properties";
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
				response.put("Mess", "no device-pro found");
			}
			else
			{
				// response
				response.put("all_devices_pro", arr);
				response.put("Mess", "Success");
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

	public void doDelete() throws Exception
	{

	}

	/**
	 * @author ducdienpt
	 * @since 25/02/2014
	 * @version 1.0
	 * @throws Exception
	 */
	public void onFormMainLoad() throws Exception
	{
		PreparedStatement pstm = null;
		ResultSet rs = null;
		String strSQL = "";
		try
		{
			// area list
			strSQL = "SELECT * FROM area WHERE status = '1' ORDER BY woodenleg";
			// open connection
			open();
			// prepare
			pstm = mcnMain.prepareStatement(strSQL);
			// exec
			rs = pstm.executeQuery();
			// response
			response.put("area_list", Util.convertToJSONArray(rs));
			// devices list
			strSQL = "SELECT * FROM device ORDER BY area_code,status";
			// prepare
			pstm = mcnMain.prepareStatement(strSQL);
			// exec
			rs = pstm.executeQuery();
			response.put("device_list", Util.convertToJSONArray(rs));
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
	 * @author ducdienpt
	 * @since 25/02/2014
	 * @version 1.0
	 * @throws Exception
	 */
	public void onGetDevicePropertyByID() throws Exception
	{
		PreparedStatement pstm = null;
		ResultSet rs = null;
		String strSQL = "";
		try
		{
			String StrDeviceId = (String) request.getString("device_id");
			strSQL = "SELECT a.id,a.device_pro_id,a.device_id,b.code,b.name,a.value "
					+ "FROM device_infor a,device_properties b "
					+ "where a.device_pro_id = b.id "
					+ "AND a.status = '1' "
					+ "AND a.device_id= "
					+ StrDeviceId
					+ " "
					+ "ORDER BY a.device_pro_id";
			// open connection
			open();
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
				response.put("Mess", "no device-pro found");
			}
			else
			{
				// response
				response.put("all_devices_pro", arr);
				response.put("Mess", "Success");
			}
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
	public void getDeviceDetailByDeviceId() throws Exception
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
}
