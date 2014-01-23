package co.vn.e_alarm;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import org.holoeverywhere.widget.Toast;
import com.loopj.android.http.AsyncHttpResponseHandler;
import co.vn.e_alarm.bean.ObjArea;
import co.vn.e_alarm.bussiness.AccountTask;
import co.vn.e_alarm.bussiness.AreaTask;
import co.vn.e_alarm.db.DBStation;
import co.vn.e_alarm.db.MyPreference;
import co.vn.e_alarm.network.NetworkUtility;
import co.vn.e_alarm.network.ParamBuilder;
import co.vn.e_alarm.network.ResponseTranslater;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class PopupLoginActitvity extends Activity {
	EditText eUserName, ePassword;
	String userName, passWord, firstTime;
	ArrayList<ObjArea> arrArea;
	DBStation mdb;
	boolean isCancel = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		init();
	}

	/**
	 * init variable popuplogin
	 */

	public void init() {
		eUserName = (EditText) findViewById(R.id.eUsername);
		ePassword = (EditText) findViewById(R.id.ePassword);
		MyPreference.getInstance().Initialize(this);
		mdb = new DBStation(getBaseContext());
	}

	/**
	 * event click button login
	 * 
	 * @param v
	 */
	public void OnclickLogin(View v) {
		// userName=
		userName = eUserName.getText().toString();
		passWord = ePassword.getText().toString();
		if (NetworkUtility.checkNetworkState(getBaseContext())) {
			if (CheckValidAccount(0, userName)
					&& CheckValidAccount(0, passWord)) {
				CheckLogin();
				
			} else {
				AccountTask.showFailToast(getBaseContext(),
						"Thiếu Thông Tin Đăng Nhập!");
			}

		} else {
			AccountTask.showNoNetworkToast(getBaseContext(),
					"Không có kết nối mạng!");
		}

	}

	/**
	 * Check valid login
	 * 
	 */
	public boolean CheckValidAccount(int index, String data) {
		switch (index) {
		case 0:
			if (data.equals(""))
				return false;
			else
				return true;
		case 1:
			if (data.length() >= 6)
				return true;
			else
				return false;
		default:
			return false;
		}

	}

	/**
	 * Process login account
	 */
	public void CheckLogin() {

		passWord = ConverPassToMD5(passWord);
		if (NetworkUtility.checkNetworkState(getBaseContext())) {
			NetworkUtility.showProgressDialog(PopupLoginActitvity.this, "", "Đang đăng nhập ...",
					true, new OnCancelListener() {

						@Override
						public void onCancel(DialogInterface dialog) {
							isCancel = true;
						}
					});
			AccountTask.Login(NetworkUtility.PERMISSION_SERVER, ParamBuilder
					.GetInfo(ParamBuilder.BuildLoginData(userName, passWord)),
					new AsyncHttpResponseHandler() {
						@Override
						public void onSuccess(int arg0, String response) {
							super.onSuccess(arg0, response);
							if (response == null || isCancel == true) {
								isCancel = false;
								return;
							}

							if (ResponseTranslater.CheckLoginSession(response,
									userName)) {
								firstTime = MyPreference.getInstance()
										.getString("FIRST_TIME");
								if ((firstTime.equals(""))) {
									getArea();
								} else {
									NetworkUtility.dismissProgressDialog();
									Intent intent = new Intent(
											PopupLoginActitvity.this,
											MainActivity.class);
									startActivity(intent);
									finish();
								}
							}

							else {
								AccountTask.showFailToast(getBaseContext(),
										"Sai Thông Tin Đăng Nhập");
								NetworkUtility.dismissProgressDialog();
							}
						}

						@Override
						public void onFailure(Throwable arg0, String arg1) {
							// TODO Auto-generated method stub
							super.onFailure(arg0, arg1);
							if (arg1 == null || isCancel == true) {
								isCancel = false;
								return;
							}
							NetworkUtility.dismissProgressDialog();
							AccountTask.showFailToast(getBaseContext(),
									"Đăng nhập không thành công");
						}
					});
		} else {
			AccountTask.showNoNetworkToast(getBaseContext(),
					"Không có kết nối mạng!");
		}

	}

	/**
	 * convert String to MD5
	 * 
	 * @param s
	 *            : password input
	 * @return String decoded MD5
	 */
	public String ConverPassToMD5(String s) {
		try {
			// Create MD5 Hash
			MessageDigest digest = java.security.MessageDigest
					.getInstance("MD5");
			digest.update(s.getBytes());
			byte messageDigest[] = digest.digest();

			// Create Hex String
			StringBuffer hexString = new StringBuffer();
			for (int i = 0; i < messageDigest.length; i++)
				hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
			return hexString.toString();

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * Get area from server To DB
	 */
	public void getArea() {
		if (NetworkUtility.checkNetworkState(this)) {
			AreaTask.GetAllArea(NetworkUtility.AREA_SERVICE,
					ParamBuilder.GetInfo(ParamBuilder.BuildAreaData()),
					new AsyncHttpResponseHandler() {
						@Override
						public void onSuccess(int arg0, String response) {
							// TODO Auto-generated method stub
							super.onSuccess(arg0, response);
							if (response == null) {
								return;
							}
							arrArea = ResponseTranslater.getAllArea(response);
							if (arrArea.size() > 0) {
								MyPreference.getInstance().writeString(
										"FIRST_TIME", "second");
								for (int i = 0; i < arrArea.size(); i++) {
									mdb.AddArea(arrArea.get(i));
								}
								Intent intent = new Intent(
										PopupLoginActitvity.this,
										MainActivity.class);
								startActivity(intent);
								finish();
							}
							NetworkUtility.dismissProgressDialog();
						}

						@Override
						public void onFailure(Throwable arg0, String arg1) {
							// TODO Auto-generated method stub
							super.onFailure(arg0, arg1);
							Toast.makeText(getBaseContext(),
									"không kết nối được với server: ",
									Toast.LENGTH_SHORT).show();
						}
					});

		}
	}
}
