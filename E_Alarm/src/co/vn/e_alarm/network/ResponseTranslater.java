package co.vn.e_alarm.network;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import co.vn.e_alarm.bean.ObjArea;
import co.vn.e_alarm.bean.ObjStation;

/**
 * @author HoaiLTT class parser data from server
 */
public class ResponseTranslater {
	public static ArrayList<ObjArea> getAllArea(String response) {
		ObjArea obj;
		ArrayList<ObjArea> arrArea = new ArrayList<ObjArea>();
		try {

			JSONObject jsonMain = new JSONObject(response);
			if (jsonMain.get(NetworkUtility.MESSAGE).equals(
					NetworkUtility.SUCCESS)) {
				JSONArray jsonArray = jsonMain.getJSONArray("ListArea");
				if (jsonArray.length() > 0) {
					for (int i = 0; i < jsonArray.length(); i++) {
						JSONObject j = jsonArray.getJSONObject(i);
						obj = new ObjArea();
						obj.setId(j.getInt("ID"));
						obj.setCode(j.getString("Code"));
						obj.setName(j.getString("Name"));
						obj.setParentID(j.getInt("ParentID"));
						obj.setLevel(j.getInt("Level"));
						obj.setStatus(j.getInt("Status"));
						obj.setWoodenleg(j.getString("Woodenleg"));
						obj.setType(j.getInt("Type"));
						obj.setLat(j.getDouble("Lat"));
						obj.setLng(j.getDouble("Lng"));
						arrArea.add(obj);
					}
				}
			}

		} catch (Exception e) {
			// TODO: handle exception
		}
		return arrArea;

	}
	public static boolean CheckLoginSession(String response,String userName){
		try {
			JSONObject jsonMain = new JSONObject(response);
			if (jsonMain.getString(NetworkUtility.HANDLE).equals(
					NetworkUtility.SUCCESS1)) {
				
				NetworkUtility.AUTHORIZATION = jsonMain
						.getString("Authorization");
				NetworkUtility.SESSIONKEY = jsonMain
						.getString("sessionKey");
				NetworkUtility.SESSION_USERNAME = userName;
				return true;
			}
			else{
				return false;
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
		
	}

	public static ArrayList<ObjStation> getAllStation(String response) {
		ObjStation obj;
		ArrayList<ObjStation> arrStation = new ArrayList<ObjStation>();
		try {

			JSONObject jsonMain = new JSONObject(response);
			//if (jsonMain.get(NetworkUtility.MESSAGE).equals(
				//	NetworkUtility.SUCCESS)) {
				JSONArray jsonArray = jsonMain
						.getJSONArray("all_devices_byarea_info");
				Log.e("RES: ",""+ jsonArray.length());
				if (jsonArray.length() > 0) {
					
					for (int i = 0; i < jsonArray.length(); i++) {
						JSONObject j = jsonArray.getJSONObject(i);
						obj = new ObjStation();
						Log.e("RES: ",""+ j.getDouble("lat"));
						obj.setId(j.getInt("id"));
						obj.setCode(j.getString("code"));
						obj.setArea_id(j.getInt("area_id"));
						obj.setArea_code(j.getString("area_code"));
						obj.setAddress(j.getString("address"));
						obj.setStatus(j.getInt("status"));
						obj.setLat(j.getDouble("lat"));
						obj.setLng(j.getDouble("lng"));
						
						JSONArray jsonList = j.getJSONArray("list");
						if(jsonList.length()>0){
							JSONObject jsonTemp = jsonList.getJSONObject(0);
							obj.setTemp(jsonTemp.getDouble("value"));
							JSONObject jsonHumi = jsonList.getJSONObject(1);
							obj.setHumidity(jsonHumi.getDouble("value"));
						}
						
						arrStation.add(obj);
					}
				}
			//}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return arrStation;

	}

}
