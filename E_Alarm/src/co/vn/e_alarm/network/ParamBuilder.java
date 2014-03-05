package co.vn.e_alarm.network;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * 
 * @author HoaiLTT class include method RequestParams with server
 */
public class ParamBuilder {
	public ParamBuilder() {

	}

	/**
	 * @return
	 */
	public static JSONObject BuildAreaData() {
		JSONObject data = new JSONObject();
		try {
			data.put(NetworkUtility.METHOD, NetworkUtility.GET_ALL_AREA);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return data;
	}

	/**
	 * @param userName
	 * @param passWord
	 * @return
	 */
	public static JSONObject BuildLoginData(String userName, String passWord) {
		JSONObject data = new JSONObject();
		try {
			data.put(NetworkUtility.METHOD, NetworkUtility.LOGIN);
			data.put(NetworkUtility.USERNAME, userName);
			data.put(NetworkUtility.PASSWORD, passWord);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return data;
	}

	/**
	 * @param id_area
	 * @return
	 */
	public static JSONObject BuildDeviceData(int id_area) {
		JSONObject data = new JSONObject();
		try {
			data.put(NetworkUtility.METHOD,
					NetworkUtility.GET_ALL_DEVICES_BY_AREA);
			data.put(NetworkUtility.AREA_ID, "" + id_area);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return data;
	}
	
	public static JSONObject getDeviceByCodeArea(String nameCode,int status) {
		JSONObject data = new JSONObject();
		try {
			data.put(NetworkUtility.METHOD,
					NetworkUtility.GET_ALL_DEVICES_BY_AREA_STATUS);
			data.put(NetworkUtility.AREA_CODE, "" + nameCode);
			data.put(NetworkUtility.STATUS, "" + status);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return data;
	}
	/**
	 * send param to get value log
	 */
	public static JSONObject getLog(int idDevice,int idProDevice){
		JSONObject data=new JSONObject();
		try {
			data.put(NetworkUtility.METHOD, NetworkUtility.DEVIECESINFOBYDEVICEID);
			data.put(NetworkUtility.DEVICEID,""+ idDevice);
			data.put(NetworkUtility.DEVICEPROID,""+idProDevice);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return data;
	}
	/**
	 * send param to get value log
	 */
	public static JSONObject getValueProperties(int idDevice){
		JSONObject data=new JSONObject();
		try {
			data.put(NetworkUtility.METHOD, NetworkUtility.DEVIECESBYDEVICEID);
			data.put(NetworkUtility.DEVICEID,""+ idDevice);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return data;
	}
	
	/**
	 * @author HoaiLTT
	 * @param data
	 * @return
	 */
	public static StringEntity GetInfo(JSONObject request) {
		StringEntity entity = null;
		try {
			request.put("sessionKey", NetworkUtility.SESSIONKEY);
			request.put("SessionUserName", NetworkUtility.SESSION_USERNAME);
			entity = new StringEntity(request.toString());
			entity.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE,
					"application/json"));
		} catch (Exception e) {
			// Exception

		}
		return entity;
	}
	
	
}
