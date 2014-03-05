package co.vn.e_alarm.network;

import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;
import co.vn.e_alarm.MainActivity;
import co.vn.e_alarm.bean.ObjAccount;
import co.vn.e_alarm.bean.ObjArea;
import co.vn.e_alarm.bean.ObjStation;
import co.vn.e_alarm.bean.ObjProperties;
import co.vn.e_alarm.db.DBStation;

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
						obj.setId(j.getInt("id"));
						obj.setArea_code(j.getString("area_code"));
						obj.setCode(j.getString("code"));
						obj.setName(j.getString("name"));
						obj.setParentID(j.getInt("parent_id"));
						obj.setLevel(j.getInt("level"));
						obj.setStatus(j.getInt("status"));
						obj.setWoodenleg(j.getString("woodenleg"));
						obj.setType(j.getInt("type"));
						obj.setLat(j.getDouble("lat"));
						obj.setLng(j.getDouble("lng"));
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
	public static boolean CheckLoginSession(Context ctx,String response,String userName){
		ObjAccount obj;
		try {
			JSONObject jsonMain = new JSONObject(response);
			if (jsonMain.getString(NetworkUtility.HANDLE).equals(
					NetworkUtility.SUCCESS1)) {
				DBStation 	mDb=new DBStation(ctx);
				mDb.deleteAccount();
				NetworkUtility.AUTHORIZATION = jsonMain
						.getString("Authorization");
				NetworkUtility.SESSIONKEY = jsonMain
						.getString("sessionKey");
				NetworkUtility.SESSION_USERNAME = userName;
				JSONObject jsonInfo=jsonMain.getJSONObject("userInfor");
				obj=new ObjAccount();
				obj.setIdAccount(jsonInfo.getInt("id"));
				obj.setUserName(jsonInfo.getString("username"));
				obj.setFullName(jsonInfo.getString("fullname"));
				obj.setGender(jsonInfo.getInt("sex"));
				obj.setCreateDate(jsonInfo.getString("create_date"));
				 
				 mDb.AddAccount(obj);
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
						obj.setMacAddress(j.getString("Mac_Add"));
						obj.setServerConnect(j.getString("Connected_Server"));
						
						JSONArray jsonList = j.getJSONArray("list");
						if(jsonList.length()>0){
							
							
							ObjProperties objProperties=new ObjProperties();
							 ArrayList<ObjProperties> listProperties = new ArrayList<ObjProperties>();
								for(int k=0;k<jsonList.length();k++){
									objProperties=new ObjProperties();
									JSONObject jsonListProperties = jsonList.getJSONObject(k);
									//objProperties.setIdDevice(jsonListProperties.getInt("device_id"));
									objProperties.setIdDeviceProperties(jsonListProperties.getInt("id"));
									objProperties.setNameProperties(jsonListProperties.getString("name"));
									objProperties.setValueProperties(jsonListProperties.getDouble("value"));
									objProperties.setValueMaxProperties(jsonListProperties.getInt("max_alarm"));
									objProperties.setValueMinProperties(jsonListProperties.getInt("min_alarm"));
									objProperties.setTypeProperties(jsonListProperties.getInt("type"));
									objProperties.setSymbolProperties(jsonListProperties.getString("symbol"));
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
	public static ArrayList<ObjProperties> GetValueDevice(String response){
		Log.e("TAG: ", response);
		ArrayList<ObjProperties> listValue=new ArrayList<ObjProperties>();
		ObjProperties obj;
		try {
			JSONObject jsonLog=new JSONObject(response);
			
			if (jsonLog.get(NetworkUtility.HANDLE).equals(
					NetworkUtility.SUCCESS1)) {
				JSONArray json=jsonLog.getJSONArray(("device_info"));
				
				if(json.length()>2){
						JSONArray array=json.getJSONArray(2);
						
						if(array.length()>0){
							
							for(int j=0;j<array.length();j++){
								obj=new ObjProperties();
								JSONObject jsonListProperties = array.getJSONObject(j);
								obj.setIdDevice(jsonListProperties.getInt("device_id"));
								obj.setIdDeviceProperties(jsonListProperties.getInt("device_pro_id"));
								obj.setNameProperties(jsonListProperties.getString("name"));
								obj.setValueProperties(jsonListProperties.getInt("value"));
								obj.setValueMaxProperties(jsonListProperties.getInt("max"));
								obj.setValueMinProperties(jsonListProperties.getInt("min"));
								obj.setTypeProperties(jsonListProperties.getInt("type"));
								listValue.add(obj);
							}
						}
					}
					
				}
			NetworkUtility.FAIL=jsonLog.getString(NetworkUtility.HANDLE);
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return listValue;
		
	}

}
