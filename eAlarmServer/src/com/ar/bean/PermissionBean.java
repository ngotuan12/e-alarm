package com.ar.bean;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;

import com.ar.util.AppProcessor;
import com.ar.util.Util;
import com.fss.sql.Database;

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
		case "queryMenuData":
			queryMenuData();
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
			open(false);

			// commit
			mcnMain.commit();
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

	public void queryMenuData() throws Exception
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
