package co.vn.e_alarm.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * 
 * @author HoaiLTT
 * class declare param communication with server
 */
public class NetworkUtility {
	public static int DEFAULT_TIME_OUT = 5*1000;
	public static String METHOD="Method";
	public static String ID="ID";
	public static String MESSAGE="Mess";
	public static String DATA="data";
	public static String SUCCESS="Success";
	public static String FAIL="error";
	//area
	public static String AREA_SERVICE="AreaService";
	public static String LIST_AREA="ListArea";
	public static String GET_BY_ID="GetByID";
	public static String GET_ALL_AREA="GetAllArea";
	//device
	public static String DEVICE_SERVICES="DeviceServices";
	public static String GET_ALL_DEVICES_BY_AREA="onGetAllDevicesByAreaID";
	public static String AREA_ID="area_id";
   
	/**
	 * Check the network state.
	 *
	 * @param context the context to check network on
	 * @return true, if has usable network
	 */
	public static boolean checkNetworkState(Context context){
		ConnectivityManager conMgr =  (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo i = conMgr.getActiveNetworkInfo();
		  if (i == null)
		    return false;
		  if (!i.isConnected())
		    return false;
		  if (!i.isAvailable())
		    return false;
		return true;
	}
}
