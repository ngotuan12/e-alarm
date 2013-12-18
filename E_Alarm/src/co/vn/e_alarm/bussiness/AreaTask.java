package co.vn.e_alarm.bussiness;

import org.apache.http.entity.StringEntity;

import co.vn.e_alarm.network.RestConnector;

import com.loopj.android.http.AsyncHttpResponseHandler;

public class AreaTask {
	public static void GetAllArea(String url,StringEntity entity,AsyncHttpResponseHandler response){
		RestConnector.postEntiny(null, url, entity, null, response);
	}
}
