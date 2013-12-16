package co.vn.e_alarm.network;

import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;
import com.loopj.android.http.RequestParams;

/**
 * 
 * @author HoaiLTT
 *	class include method RequestParams with server
 */
public class ParamBuilder {
public ParamBuilder() {
		
	}

	
	
	public static String BuildAreaData(){
		JSONObject data = new JSONObject();
		try {
			data.put(NetworkUtility.METHOD, NetworkUtility.GET_ALL_AREA);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return data.toString();
	}
	public static String BuildDeviceData(int id_area){
		JSONObject data = new JSONObject();
		try {
			data.put(NetworkUtility.METHOD, NetworkUtility.GET_ALL_DEVICES_BY_AREA);
			data.put(NetworkUtility.AREA_ID,""+id_area);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return data.toString();
	}
	public static StringEntity GetInfo(String data){
		StringEntity entity = null;
		try {
		entity = new StringEntity(data);
		entity.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
		} catch(Exception e) {
		//Exception
		
	}
		return entity;
	}
}
