package com.ar.bean;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.codehaus.jettison.json.JSONArray;
import com.ar.Model.AreaModel;
import com.ar.util.AppProcessor;
import com.ar.util.Util;
import com.fss.sql.Database;

public class AreaBean extends AppProcessor
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
			
			JSONArray arr = Util.convertToJSONArray(rs);
			
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

	public JSONArray GetActiveByParent(int ID) throws Exception
	{
		String strSQL = "SELECT id as ID,code as Code,name as Name,parent_id as ParentID,level as Level,status as Status,woodenleg as Woodenleg,lat as Lat,lng as Lng,type as Type "
				+ "FROM area "
				+ "WHERE status=1 AND parent_id = "
				+ String.valueOf(ID);
		return ExcuteQuery(strSQL, 0);
	}

	private JSONArray GetByID(int ID) throws Exception
	{
		String strSQL = "SELECT id as ID,code as Code,name as Name,parent_id as ParentID,level as Level,status as Status,woodenleg as Woodenleg,lat as Lat,lng as Lng,type as Type "
				+ "FROM area WHERE id = " + String.valueOf(ID);
		return ExcuteQuery(strSQL, 0);
	}

	private void AddArea(AreaModel _Model) throws Exception
	{
		String strSQL = "INSERT INTO area(code,name,parent_id,status,lat,lng,type) "
				+ "VALUES('"
				+ _Model.Code
				+ "','"
				+ _Model.Name
				+ "',"
				+ _Model.ParentID
				+ ","
				+ _Model.Status
				+ ","
				+ _Model.Lat
				+ "," + _Model.Lang + "," + _Model.Type + ")";
		ExcuteQuery(strSQL, 1);
	}

	private void UpdateArea(AreaModel _Model) throws Exception
	{
		String strSQL = "UPDATE area SET code='" + _Model.Code + "',name='"
				+ _Model.Name + "'," + "parent_id=" + _Model.ParentID
				+ ",status=" + _Model.Status + ",lat=" + _Model.Lat + ",lng="
				+ _Model.Lang + ",type=" + _Model.Type + "" + " WHERE id="
				+ _Model.ID + "";
		ExcuteQuery(strSQL, 1);
	}

	private void DisbleArea(int ID) throws Exception
	{
		String strSQL = "UPDATE area SET status=0 WHERE id=" + ID + "";
		ExcuteQuery(strSQL, 1);
	}

	@Override
	public void doPost() throws Exception
	{
		String Method = (String) request.getString("Method");
		switch (Method)
		{
		case "GetByID":
			int ID = Integer.parseInt((String) request.getString("ID"));
			JSONArray GetByID = GetByID(ID);
			response.put("AreaInfor", GetByID);
			response.put("Mess", "Success");
			break;
		case "GetActiveByParent":
			int IDParent = Integer.parseInt((String) request.getString("ID"));
			JSONArray GetActiveByParent = GetActiveByParent(IDParent);
			response.put("ListArea", GetActiveByParent);
			response.put("Mess", "Success");
			break;
		case "AddArea":
			AreaModel _AreaModel = new AreaModel();
			_AreaModel.Code = (String) request.getString("Code");
			_AreaModel.Name = (String) request.getString("Name");
			_AreaModel.ParentID = Integer.parseInt((String) request
					.getString("ParentID"));
			_AreaModel.Status = (String) request.getString("Status");
			_AreaModel.Lang = Double.parseDouble((String) request
					.getString("Lang"));
			_AreaModel.Lat = Double.parseDouble((String) request
					.getString("Lat"));
			_AreaModel.Type = (String) request.getString("Type");
			AddArea(_AreaModel);
			response.put("Mess", "Success");
			break;
		case "UpdateArea":
			AreaModel _AreaModelUpdate = new AreaModel();
			_AreaModelUpdate.ID = Integer.parseInt((String) request
					.getString("ID"));
			_AreaModelUpdate.Code = (String) request.getString("Code");
			_AreaModelUpdate.Name = (String) request.getString("Name");
			_AreaModelUpdate.ParentID = Integer.parseInt((String) request
					.getString("ParentID"));
			_AreaModelUpdate.Status = (String) request.getString("Status");
			_AreaModelUpdate.Lang = Double.parseDouble((String) request
					.getString("Lang"));
			_AreaModelUpdate.Lat = Double.parseDouble((String) request
					.getString("Lat"));
			_AreaModelUpdate.Type = (String) request.getString("Type");
			UpdateArea(_AreaModelUpdate);
			response.put("Mess", "Success");
			break;
		case "DisbleArea":
			int IDAreaDis = Integer.parseInt((String) request.getString("ID"));
			DisbleArea(IDAreaDis);
			response.put("Mess", "Success");
			break;
		default:
			response.put("Mess", "API does not exist");
			break;
		}// TODO Auto-generated method stub
	}

	@Override
	public void doGet() throws Exception
	{

		// TODO Auto-generated method stub

	}

	@Override
	public void doDelete() throws Exception
	{
		// TODO Auto-generated method stub
	}
}
