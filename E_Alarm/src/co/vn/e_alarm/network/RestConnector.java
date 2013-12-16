package co.vn.e_alarm.network;

import org.apache.http.entity.StringEntity;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class RestConnector {
	public static String IP = "54.243.244.187:7777";
	private static String url = "http://"+IP+"/";

	private static int timeOut = NetworkUtility.DEFAULT_TIME_OUT;
    private static AsyncHttpClient client = new AsyncHttpClient();
    
    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.setTimeout(timeOut);
    	client.get(getAbsoluteUrl(url), params, responseHandler);
    }
    
    public static void getWithUrl(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.setTimeout(timeOut);
    	client.get(url, params, responseHandler);
    }


    public static void post(Activity ac,String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.setTimeout(timeOut);
    	client.post(getAbsoluteUrl(url), params, responseHandler);
    	Toast.makeText(ac, getAbsoluteUrl(url)+" param: "+params, Toast.LENGTH_SHORT).show();
    }
    public static void postEntiny(Activity ac,String url,StringEntity entity,String type,AsyncHttpResponseHandler response){
    	client.setTimeout(timeOut);
    	client.post(ac, getAbsoluteUrl(url), entity, null, response);
    }
    public static void cancelRequest(Context c){
    	client.cancelRequests(c, true);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return new String(url + relativeUrl);
    }
    
    public void setTimeOut(int timeOut){
    	NetworkUtility.DEFAULT_TIME_OUT = timeOut;
    }
    

}
