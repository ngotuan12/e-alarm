package com.ar.bean;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.codehaus.jettison.json.JSONArray;
import com.ar.Model.GatewayModel;
import com.ar.util.AppProcessor;
import com.ar.util.Util;
import com.fss.sql.Database;
import com.mysql.jdbc.Statement;

public class GatewayBean extends AppProcessor {
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
			JSONArray arr = Util.convertToJSONArray(rs);
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

	public JSONArray GetListGateway() throws Exception {
		String strSQL = "SELECT * FROM gateway WHERE Type=1";
		return ExcuteQuery(strSQL, 0);
	}

	public String GetGatewayByID(int ID) throws Exception {
		String strSQL = "SELECT response FROM gateway_request WHERE ID="
				+ ID + "";
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			String _Respone="";
			// open connection
			open();
			// prepare
			pstm = mcnMain.prepareStatement(strSQL);
			rs = pstm.executeQuery();
			if(rs.next())
			{
				_Respone=rs.getString(1);
			}
			return _Respone;
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		} finally {
			Database.closeObject(pstm);
			Database.closeObject(rs);
			close();
		}
	}

	private int AddGateway(GatewayModel _Model) throws Exception {
		String strSQL = "INSERT INTO gateway_request(request, STATUS , gateway_id)"
				+ "VALUES('"
				+ _Model.Request
				+ "',2,'"
				+ _Model.Gateway_ID+"')";
		PreparedStatement pstm = null;
		int ID = 0;
		ResultSet rs = null;
		try {
			// open connection
			open();
			// prepare
			pstm = mcnMain.prepareStatement(strSQL,
					Statement.RETURN_GENERATED_KEYS);
			pstm.executeUpdate();
			rs = pstm.getGeneratedKeys();
			if (rs.next())
				ID = rs.getInt(1);
			return ID;
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		} finally {
			Database.closeObject(pstm);
			Database.closeObject(rs);
			close();
		}
		// ExcuteQuery(strSQL, 1);
	}

	@Override
	public void doPost() throws Exception {
		String Method = (String) request.getString("Method");
		switch (Method) {
		case "GetListGateway":
			JSONArray GetListGateway = GetListGateway();
			response.put("ListGateway", GetListGateway);
			response.put("Mess", "Success");
			break;
		case "AddGateway":
			GatewayModel _GatewayModel = new GatewayModel();
			_GatewayModel.Gateway_ID = Integer.parseInt((String) request
					.getString("Gateway_ID"));
			_GatewayModel.Request = (String) request.getString("Request");
			int ID = AddGateway(_GatewayModel);
			Thread.sleep(5000);
			String _respone = GetGatewayByID(ID);
			if (_respone != null && _respone!="") {
				response.put("Respone", _respone);
			} else {
				response.put("Respone", "Request time out");
			}
			response.put("Mess", "Success");
			break;
		default:
			response.put("Mess", "API does not exist");
			break;
		}// TODO Auto-generated method stub
	}

	@Override
	public void doGet() throws Exception {

		// TODO Auto-generated method stub

	}

	@Override
	public void doDelete() throws Exception {
		// TODO Auto-generated method stub
	}
}
