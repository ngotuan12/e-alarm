package co.vn.e_alarm.db;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;



public class MyPreference {
	
	private static MyPreference instance;
	private Context mContext;
	private SharedPreferences mMyPreferences;
	
	public String customVar;

	public static MyPreference getInstance() {
		if (instance == null) {
			// Create the instance
			instance = new MyPreference();
		}
		// Return the instance
		return instance;
	}
	
	private MyPreference() {
		// Constructor hidden because this is a singleton
	}
	public void Initialize(Context ctxt){
	       mContext = ctxt;
	       mMyPreferences=PreferenceManager.getDefaultSharedPreferences(mContext);
	   }
	public void writeString(String key, String value){
	     Editor e = mMyPreferences.edit();
	     e.putString(key, value);
	     e.commit();
	}
	public String getString(String key){
		String str = mMyPreferences.getString(key, "");
		return str;
	}
	public void writeInteger(String key, int value){
	     Editor e = mMyPreferences.edit();
	     e.putInt(key, value);
	     e.commit();
	}
	public int getInteger(String key){
		int i = mMyPreferences.getInt(key,0);
		return i;
	}
	public void CheckValid(String key,boolean value){
		Editor e=mMyPreferences.edit();
		e.putBoolean(key, value);
		e.commit();
	}
	public boolean getValid(String key){
		boolean check = mMyPreferences.getBoolean(key,true);
		return check;
	}
	public String getError(String strCode){
		if(strCode.equalsIgnoreCase("EAS-SYS-001")){
			strCode="Session user name not correct!";
		}
		else if(strCode.equalsIgnoreCase("EAS-SYS-002")){
			strCode="Session expire!";
		}
		else if(strCode.equalsIgnoreCase("EAS-SYS-002")){
			strCode="DBTableAuthenticator.verifyPassword";
		}
		else{
			strCode="User not correct, Pls try again!";

		}
		return strCode;
		
	}
}
