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
			strSQL = "SELECT device_infor.*,device_properties.* "
					+ "FROM device_infor INNER JOIN device_properties "
					+ "ON device_infor.device_pro_id = device_properties.id where device_infor.device_id = ?";
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
				arr.put(arr.length() + 1, onGetDevicesInfoByDeviceID(Integer
						.parseInt(strDeviceID)));
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
					Ocooked.put("list",
							(Object) onGetDevicesInfoByDeviceID(Integer
									.parseInt(arr.getJSONObject(i).getString(
											"id"))));
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
					Ocooked.put("list",
							(Object) onGetDevicesInfoByDeviceID(Integer
									.parseInt(arr.getJSONObject(i).getString(
											"id"))));
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
			strSQL = "SELECT id,code,area_id,area_code,address,lat,lng,status"
					+ " FROM device " + "where area_code LIKE "+"'"+strAreaCode+"%' ";
			if(strStatus.contentEquals("2") ||strStatus.contentEquals("1"))
			{
				strSQL+=" AND status= "+strStatus;
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
					Ocooked.put("list",
							(Object) onGetDevicesInfoByDeviceID(Integer
									.parseInt(arr.getJSONObject(i).getString(
											"id"))));
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
			onDisableDevicePro();
			break;
		case "onEditDevicePro":
			onEditDevicePro();
			break;
		case "onGetDevicePropertyByID":
			onGetDevicePropertyByID();
			break;
		default:
			response.put("error", "you must enter the correct API name");
			break;
		}

	}

	private void onDisableDevicePro() throws Exception
	{
		String strSQL = "";
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try
		{
			String strDeviceProID = (String) request.getString("ID");
			String status = (String) request.getString("status");
			// open connection
			open();
			strSQL = "UPDATE device_properties SET status = ? "
					+ " WHERE id = ? ";
			// prepare
			pstm = mcnMain.prepareStatement(strSQL);
			pstm.setInt(1, Integer.parseInt(status));
			pstm.setInt(2, Integer.parseInt(strDeviceProID));

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
			String strtype = (String) request.getString("type");
			String strmin = (String) request.getString("min");
			String strmax = (String) request.getString("max");

			// open connection
			open();
			// strSQL =
			// "UPDATE device_properties SET name=?,code=?,description=?,type=?,min=?,max=?"
			// + " WHERE id = ?";
			strSQL = "UPDATE device_properties SET name='" + strname.toString()
					+ "',code='" + strcode.toString() + "'," + "description='"
					+ strdescription.toString() + "',type="
					+ strtype.toString() + ",min=" + Double.parseDouble(strmin)
					+ ",max=" + Double.parseDouble(strmax) + " WHERE id="
					+ strdevice_pro_id + "";
			// prepare
			// pstm = mcnMain.prepareStatement(strSQL);
			// pstm.setString(1, strname);
			// pstm.setString(2, strcode);
			// pstm.setString(3, strdescription);
			// pstm.setString(4, strtype);
			// pstm.setDouble(5, Double.parseDouble(strmin));
			// pstm.setDouble(6, Double.parseDouble(strmax));
			// pstm.setInt(7, Integer.parseInt(strdevice_pro_id));

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
			String strtype = (String) request.getString("type");
			String strmin = (String) request.getString("min");
			String strmax = (String) request.getString("max");

			// open connection
			open();
			strSQL = "INSERT INTO device_properties VALUES (?,?,?,?,?,?)";
			// prepare
			pstm = mcnMain.prepareStatement(strSQL);
			pstm.setString(1, strname);
			pstm.setString(2, strcode);
			pstm.setString(3, strdescription);
			pstm.setString(4, strtype);
			pstm.setDouble(5, Double.parseDouble(strmin));
			pstm.setDouble(6, Double.parseDouble(strmax));

			int done = pstm.executeUpdate(strSQL);

			if (done == 1)
			{
				// close statement
				Database.closeObject(pstm);
				Database.closeObject(rs);
				// response
				response.put("Mess", "add sucess");
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
			strSQL = "SELECT *" + " FROM device_properties";
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
		String strSQL	= "";	
		try 
		{
			//area list
			strSQL = "SELECT * FROM area WHERE status = '1' ORDER BY woodenleg";
			//open connection
			open();
			//prepare
			pstm = mcnMain.prepareStatement(strSQL);
			//exec
			rs = pstm.executeQuery();
			//response
			response.put("area_list", Util.convertToJSONArray(rs));
			//devices list
			strSQL = "SELECT * FROM device ORDER BY area_code,status";
			//prepare
			pstm = mcnMain.prepareStatement(strSQL);
			//exec
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
		String strSQL	= "";
		try 
		{
			String StrDeviceId = (String) request.getString("device_id");
			strSQL="SELECT a.id,a.device_pro_id,a.device_id,b.code,b.name,a.value "
					+"FROM device_infor a,device_properties b "
					+"where a.device_pro_id = b.id "
					+"AND a.status = '1' "
					+"AND a.device_id= "+StrDeviceId+" "
					+"ORDER BY a.device_pro_id";
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
}
