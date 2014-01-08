package com.ar.bean;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;

import com.ar.util.AppProcessor;
import com.ar.util.Encrypt;
import com.ar.util.Util;
import com.fss.sql.Database;
import com.fss.util.AppException;
import com.fss.util.StringUtil;

public class PermissionBean extends AppProcessor
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
		case "login":
			login();
			break;
		case "loadSystemData":
			loadSystemData();
			break;
		default:
			throw new Exception("Unknown request");
		}
	}

	@Override
	public void doDelete() throws Exception
	{

	}

	public void login() throws Exception
	{
		try
		{
			// Parameter
			String strUserName = request.getString("UserName");
			String strPassword = request.getString("PassWord");
			open(false);
			// commit
			mcnMain.commit();
			//password
			verifyPassword(mcnMain, strUserName, strPassword);
			//session
			String strDate =StringUtil.format(new Date( System.currentTimeMillis() + 300000L), "dd/MM/yyyy HH:mm:ss");
			JSONObject json = new JSONObject();
			json.put("username", strUserName);
			json.put("date", strDate);
			//secret key with DES
			SecretKey desKey       = KeyGenerator.getInstance("DES").generateKey();
			Encrypt desEncrypter = new Encrypt(desKey, desKey.getAlgorithm());
			String sessionKey = desEncrypter.encrypt(json.toString());
			Util.session.put(sessionKey, desKey);
			//response
			response.put("sessionKey", sessionKey);
			response.put("Authorization", sessionKey);
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			throw ex;
		}
		finally
		{
			close();
			Database.closeObject(mcnMain);
		}
	}

	private String verifyPassword(Connection cn, String strUserName,
			String strPassword) throws Exception
	{
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try
		{
			String strSQL = "SELECT id, password FROM `user` WHERE UPPER(username)=? AND STATUS='1'";

			pstm = cn.prepareStatement(strSQL);
			pstm.setString(1, strUserName.toUpperCase());
			rs = pstm.executeQuery();
			if (!(rs.next()))
			{
				throw new AppException("EAS-SYS-003",
						"DBTableAuthenticator.verifyPassword");
			}
			String strReturn = rs.getString(1);
			String strDBPassword = rs.getString(2);
			rs.close();
			pstm.close();

			if (strDBPassword == null)
			{
				strDBPassword = "";
			}
			if (!(strDBPassword.equals(strPassword)))
			{
				throw new AppException("EAS-SYS-003",
						"DBTableAuthenticator.verifyPassword");
			}

			return strReturn;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			throw ex;
		}
		finally
		{
			Database.closeObject(pstm);
			pstm = null;
			Database.closeObject(rs);
			rs = null;
		}
	}

	public void loadSystemData() throws Exception
	{
		PreparedStatement pstm = null;
		ResultSet rs = null;
		JSONArray vt = new JSONArray();
		JSONArray vtReturn = new JSONArray();
		try
		{
			open();

			String strSQL = "SELECT id, module_name, module_type, "
					+ "module_action,module_icon, " + "description,level "
					+ "FROM module WHERE status='1' "
					+ "ORDER BY woodenleg,`order` ";
			// prepare
			pstm = mcnMain.prepareStatement(strSQL);
			rs = pstm.executeQuery();
			vt = Util.convertToJSONArray(rs);
			// organize
			vtReturn = organizeTree(vt, 1, "G");
			// response
			response.put("menu_data", vtReturn);
			pstm.close();
			rs.close();
			// language
			strSQL = "SELECT id, `code`, `name`,`status` "
					+ "FROM language WHERE status='1' "
					+ "ORDER BY `order`,`name` ";
			// prepare
			pstm = mcnMain.prepareStatement(strSQL);
			rs = pstm.executeQuery();
			// put
			response.put("language", Util.convertToJSONArray(rs));
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			throw ex;
		}
		finally
		{
			Database.closeObject(pstm);
			pstm = null;
			Database.closeObject(rs);
			rs = null;
			close();
		}
	}

	public static JSONArray organizeTree(JSONArray vtData, int iLevel,
			String strTypeGroup) throws Exception
	{
		JSONArray vtReturn = new JSONArray();
		while (vtData.length() > 0)
		{
			JSONObject vtDataRow = (JSONObject) vtData.get(0);
			int iNewLevel = vtDataRow.getInt("level");
			if (iNewLevel == iLevel)
			{
				// Add new node
				vtDataRow.remove("level");
				vtReturn.put(vtDataRow);
				vtData.remove(vtDataRow);
				// Add child node
				String strType = vtDataRow.getString("module_type");
				if (strType.equals(strTypeGroup))
				{
					vtDataRow.put("children",
							(organizeTree(vtData, iLevel + 1, strTypeGroup)));
				}
			}
			else if (iNewLevel > iLevel)
			{
				vtData.remove(vtDataRow);
			}
			else
			{
				break;
			}
		}
		return vtReturn;
	}
}
