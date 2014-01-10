package co.vn.e_alarm;


import java.util.ArrayList;

import com.loopj.android.http.AsyncHttpResponseHandler;

import co.vn.e_alarm.bean.ObjArea;
import co.vn.e_alarm.bussiness.AreaTask;
import co.vn.e_alarm.db.DBStation;
import co.vn.e_alarm.db.MyPreference;
import co.vn.e_alarm.network.NetworkUtility;
import co.vn.e_alarm.network.ParamBuilder;
import co.vn.e_alarm.network.ResponseTranslater;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;

public class SplashActivity extends Activity{
	String firstTime;
	ArrayList<ObjArea> arrArea;
	DBStation mdb;
	Intent intent;
	ProgressBar prSplash;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sflash);
		prSplash=(ProgressBar) findViewById(R.id.prSplash);
		mdb=new DBStation(this);
		MyPreference.getInstance().Initialize(this);
		firstTime = MyPreference.getInstance().getString("FIRST_TIME");
		if((firstTime.equals(""))){
			getArea();	
		}
		else{
			intent=new Intent(this,PopupLoginActitvity.class);
			startActivity(intent);
			finish();
		}
	}
	public void getArea(){
			if(NetworkUtility.checkNetworkState(this)){
				AreaTask.GetAllArea(NetworkUtility.AREA_SERVICE, ParamBuilder.GetInfo(ParamBuilder.BuildAreaData()), new AsyncHttpResponseHandler(){
					@Override
					public void onSuccess(int arg0, String response) {
						// TODO Auto-generated method stub
						super.onSuccess(arg0, response);
						if(response==null){
							return;
						}
						arrArea=ResponseTranslater.getAllArea(response);
						Log.e("TAG: ", "h: " +arrArea.size());
						if(arrArea.size()>0){
							MyPreference.getInstance().writeString("FIRST_TIME","second");
							for(int i=0;i<arrArea.size();i++){
								mdb.AddArea(arrArea.get(i));
							}
							
							
							intent=new Intent(SplashActivity.this,PopupLoginActitvity.class);
							startActivity(intent);
							finish();
						}
						
						
						
					}
					@Override
					public void onFailure(Throwable arg0, String arg1) {
						// TODO Auto-generated method stub
						super.onFailure(arg0, arg1);
						Toast.makeText(getBaseContext(), "không kết nối được với server: ", Toast.LENGTH_SHORT).show();
					}
				});
			
				
	}
	}

}
