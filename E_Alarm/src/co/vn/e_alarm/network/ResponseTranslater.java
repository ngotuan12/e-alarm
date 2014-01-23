package co.vn.e_alarm.network;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;
import co.vn.e_alarm.bean.ObjArea;
import co.vn.e_alarm.bean.ObjStation;
import co.vn.e_alarm.bean.ObjProperties;

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

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return arrArea;

	}
	/**
	 * Check session Login
	 * @param response: response server 
	 * @param userName
	 * @return
	 */
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
/**
 * Get all station from server
 * @param response
 * @return
 */
	public static ArrayList<ObjStation> getAllStation(String response) {
		Log.e("Response: ", response);
		ObjStation obj;
		ArrayList<ObjStation> arrStation=new ArrayList<ObjStation>();
		try {

			JSONObject jsonMain = new JSONObject(response);
			if (jsonMain.get(NetworkUtility.HANDLE).equals(
					NetworkUtility.SUCCESS1)) {
				JSONArray jsonArray = jsonMain
						.getJSONArray("all_devices_byarea_info");
				if (jsonArray.length() > 0) {
					
					for (int i = 0; i < jsonArray.length(); i++) {
						JSONObject j = jsonArray.getJSONObject(i);
						obj = new ObjStation();
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
							
							
							ObjProperties objProperties=new ObjProperties();
							 ArrayList<ObjProperties> listProperties = new ArrayList<ObjProperties>();
								for(int k=0;k<jsonList.length();k++){
									objProperties=new ObjProperties();
									JSONObject jsonListProperties = jsonList.getJSONObject(k);
									objProperties.setIdDevice(jsonListProperties.getInt("device_id"));
									objProperties.setIdDeviceProperties(jsonListProperties.getInt("device_pro_id"));
									objProperties.setNameProperties(jsonListProperties.getString("name"));
									objProperties.setValueProperties(jsonListProperties.getInt("value"));
									objProperties.setValueMaxProperties(jsonListProperties.getInt("max"));
									objProperties.setValueMinProperties(jsonListProperties.getInt("min"));
									objProperties.setTypeProperties(jsonListProperties.getInt("type"));
									listProperties.add(objProperties);
								}
								
								
								
								obj.setListPropertiesStation(listProperties);	
						}
						
						arrStation.add(obj);
					}
				}
			}
			NetworkUtility.FAIL=jsonMain.getString(NetworkUtility.HANDLE);
			

		} catch (Exception e) {
			e.printStackTrace();
		}
		return arrStation;

	}
	public static double GetLogDevice(String response){
		double value=0;
		try {
			JSONObject jsonLog=new JSONObject(response);
			
			if (jsonLog.get(NetworkUtility.MESSAGE).equals(
					NetworkUtility.SUCCESS)) {
				JSONArray json=jsonLog.getJSONArray(("device_info"));
				
				if(json.length()>0){
					 value=json.getJSONObject(0).getDouble("value");
				}
				
			}
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		return value;
		
	}

}
