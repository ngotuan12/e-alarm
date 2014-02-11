package com.ar.bean;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.codehaus.jettison.json.JSONArray;

import com.ar.Model.GatewayDetailModel;
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
			JSONArray arr = new JSONArray();
			if (rs != null)
				arr = Util.convertToJSONArray(rs);
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
		String strSQL = "SELECT response FROM gateway_request WHERE ID=" + ID
				+ "";
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			String _Respone = "";
			// open connection
			open();
			// prepare
			pstm = mcnMain.prepareStatement(strSQL);
			rs = pstm.executeQuery();
			if (rs.next()) {
				_Respone = rs.getString(1);
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
				+ _Model.Gateway_ID
				+ "')";
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

	private void AddGatewayDetail(GatewayDetailModel _Model) throws Exception {
		String strSQL = "INSERT INTO gateway(mac_add,connected_server,status,type,ip_add)"
				+ "VALUES('"
				+ _Model.mac_add
				+ "','"
				+ _Model.connected_server
				+ "','"
				+ _Model.status
				+ "','"
				+ _Model.type
				+ "','"
				+ _Model.ip_add + "')";
		ExcuteQuery(strSQL, 1);
	}

	private void UpdateGateway(GatewayDetailModel _Model) throws Exception {
		String strSQL = "UPDATE gateway SET connected_server='"
				+ _Model.connected_server + "',ip_add='" + _Model.ip_add + "',"
				+ "mac_add='" + _Model.mac_add + "',status=" + _Model.status
				+ ",type=" + _Model.type + " WHERE id=" + _Model.id + "";
		ExcuteQuery(strSQL, 1);
	}

	private void DeleteGateway(GatewayDetailModel _Model) throws Exception {
		String strSQL = "UPDATE gateway SET status='0' WHERE id=" + _Model.id
				+ "";
		ExcuteQuery(strSQL, 1);
	}

	private void RestoreGateway(GatewayDetailModel _Model) throws Exception {
		String strSQL = "UPDATE gateway SET status='1' WHERE id=" + _Model.id
				+ "";
		ExcuteQuery(strSQL, 1);
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
		case "AddGatewayDetail":
			GatewayDetailModel _GatewayDetailAdd = new GatewayDetailModel();
			_GatewayDetailAdd.mac_add = (String) request.getString("mac_add");
			_GatewayDetailAdd.type = (String) request.getString("type");
			_GatewayDetailAdd.status = (String) request.getString("status");
			_GatewayDetailAdd.ip_add = (String) request.getString("ip_add");
			_GatewayDetailAdd.connected_server = (String) request
					.getString("connected_server");
			AddGatewayDetail(_GatewayDetailAdd);
			response.put("Mess", "Success");
			break;
		case "UpdateGatewayDetail":
			GatewayDetailModel _GatewayDetail = new GatewayDetailModel();
			_GatewayDetail.id = Integer.parseInt((String) request
					.getString("Gateway_ID"));
			_GatewayDetail.mac_add = (String) request.getString("mac_add");
			_GatewayDetail.type = (String) request.getString("type");
			_GatewayDetail.status = (String) request.getString("status");
			_GatewayDetail.ip_add = (String) request.getString("ip_add");
			_GatewayDetail.connected_server = (String) request
					.getString("connected_server");
			UpdateGateway(_GatewayDetail);
			response.put("Mess", "Success");
			break;
		case "DeleteGatewayDetail":
			GatewayDetailModel _GatewayDetailDel = new GatewayDetailModel();
			_GatewayDetailDel.id = Integer.parseInt((String) request
					.getString("Gateway_ID"));
			DeleteGateway(_GatewayDetailDel);
			response.put("Mess", "Success");
			break;
		case "RestoreGatewayDetail":
			GatewayDetailModel _GatewayDetailRestore = new GatewayDetailModel();
			_GatewayDetailRestore.id = Integer.parseInt((String) request
					.getString("Gateway_ID"));
			RestoreGateway(_GatewayDetailRestore);
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
			if (_respone != null && _respone != "") {
				response.put("Respone", _respone);
			} else {
				response.put("Respone", "Request time out");
			}
			response.put("Mess", "Success");
			break;
		default:
			response.put("Mess", "API does not exist");
			break;
		}
	}

	@Override
	public void doGet() throws Exception {
	}

	@Override
	public void doDelete() throws Exception {
	}
}
