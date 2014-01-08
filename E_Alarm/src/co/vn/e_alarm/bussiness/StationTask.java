package co.vn.e_alarm.bussiness;

import org.apache.http.entity.StringEntity;

import co.vn.e_alarm.network.RestConnector;

import com.loopj.android.http.AsyncHttpResponseHandler;

public class StationTask {
	public static void GetAllStationByIDArea(String url,StringEntity entity,AsyncHttpResponseHandler response){
		RestConnector.postEntiny(null, url, entity, null, response);
	}
	public static void ViewLogStation(String url,StringEntity entity,AsyncHttpResponseHandler handler){
		RestConnector.postEntiny(null, url, entity, null, handler);
	}
}
