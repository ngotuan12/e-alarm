package co.vn.e_alarm.bussiness;

import org.apache.http.entity.StringEntity;
import org.holoeverywhere.widget.Toast;

import android.content.Context;
import co.vn.e_alarm.network.RestConnector;

import com.loopj.android.http.AsyncHttpResponseHandler;

public class AccountTask {
	public static void Login(String url,StringEntity entity,AsyncHttpResponseHandler response){
		RestConnector.postEntiny(null, url, entity, null, response);
	}
	public static void showNoNetworkToast(Context c,String message){
		Toast.makeText(c,message , Toast.LENGTH_SHORT).show();
	}
	public static void showFailToast(Context c,String message){
		Toast.makeText(c,message , Toast.LENGTH_SHORT).show();
	}
}
