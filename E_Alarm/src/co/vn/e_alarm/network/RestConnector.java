package co.vn.e_alarm.network;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.ParseException;
import org.apache.http.entity.StringEntity;

import android.app.Activity;
import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class RestConnector {
	public static String IP = "54.243.244.187:7777";
	private static String url = "http://" + IP + "/";

	private static int timeOut = NetworkUtility.DEFAULT_TIME_OUT;
	private static AsyncHttpClient client = new AsyncHttpClient();

	/**
	 * @param url
	 * @param params
	 * @param responseHandler
	 */
	public static void get(String url, RequestParams params,
			AsyncHttpResponseHandler responseHandler) {
		client.setTimeout(timeOut);
		client.get(getAbsoluteUrl(url), params, responseHandler);
	}

	/**
	 * @param url
	 * @param params
	 * @param responseHandler
	 */
	public static void getWithUrl(String url, RequestParams params,
			AsyncHttpResponseHandler responseHandler) {
		client.setTimeout(timeOut);
		client.get(url, params, responseHandler);
	}

	/**
	 * @param ac
	 * @param url
	 * @param params
	 * @param responseHandler
	 */
	public static void post(Activity ac, String url, RequestParams params,
			AsyncHttpResponseHandler responseHandler) {
		client.setTimeout(timeOut);
		client.post(getAbsoluteUrl(url), params, responseHandler);
	}

	/**
	 * @param ac
	 * @param url
	 * @param entity
	 * @param type
	 * @param response
	 */
	public static void postEntiny(Activity ac, String url, StringEntity entity,
			String type, AsyncHttpResponseHandler response) {
		client.setTimeout(timeOut);
		Header[] headers = new Header[1];
		headers[0] = new Header() {

			@Override
			public String getValue() {
				// TODO Auto-generated method stub
				return NetworkUtility.AUTHORIZATION;
			}

			@Override
			public String getName() {
				// TODO Auto-generated method stub
				return "AUTHORIZATION";
			}

			@Override
			public HeaderElement[] getElements() throws ParseException {
				// TODO Auto-generated method stub
				return null;
			}
		};
		client.post(ac, getAbsoluteUrl(url), headers, entity, "", response);

	}

	/**
	 * @param c
	 */
	public static void cancelRequest(Context c) {
		client.cancelRequests(c, true);
	}

	/**
	 * @param relativeUrl
	 * @return
	 */
	private static String getAbsoluteUrl(String relativeUrl) {
		return new String(url + relativeUrl);
	}

	/**
	 * @param timeOut
	 */
	public void setTimeOut(int timeOut) {
		NetworkUtility.DEFAULT_TIME_OUT = timeOut;
	}

}
