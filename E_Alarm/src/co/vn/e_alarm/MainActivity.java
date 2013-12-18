package co.vn.e_alarm;

import java.util.ArrayList;

import co.vn.e_alarm.adapter.StationAdapter;
import co.vn.e_alarm.bean.ObjArea;
import co.vn.e_alarm.bean.ObjStation;
import co.vn.e_alarm.bussiness.StationTask;
import co.vn.e_alarm.customwiget.SlidingLayer;
import co.vn.e_alarm.db.DBStation;
import co.vn.e_alarm.db.MyPreference;
import co.vn.e_alarm.network.NetworkUtility;
import co.vn.e_alarm.network.ParamBuilder;
import co.vn.e_alarm.network.ResponseTranslater;
import co.vn.e_alarm.utils.Utils;
import com.actionbarsherlock.app.ActionBar.OnNavigationListener;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.loopj.android.http.AsyncHttpResponseHandler;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Spinner;

public class MainActivity extends SherlockFragmentActivity implements
		OnNavigationListener, OnItemSelectedListener {
	GoogleMap mGooglemap;
	SlidingLayer mSlidingLayer;
	ArrayList<Marker> arrMarker;
	ArrayList<ObjStation> arrStation;
	StationAdapter adapter;
	Spinner spDistric;
	ViewPager mPager;
	String strCity, strWood;
	RelativeLayout layout_main;
	DBStation mdb;
	Marker market;
	int index=0,count=0,id=0;
	private String TAG = "MainActivity";
	static Activity myActivity;
	ArrayList<ObjArea> listArea;
	ArrayList<String> listCity;
	Marker marker ;
	Fragment fragment;
	ProgressBar proMain;
	Button btnRetry;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initMapFragment();
		ShowCity();
		bindViews();
		initState();
		setWindowPopupMap();
		spDistric.setOnItemSelectedListener(this);
		
	}
	
	

	/**
	 * insert map
	 * 
	 */
	public void initMapFragment() {
		try {
			MapsInitializer.initialize(MainActivity.this);
			MyPreference.getInstance().Initialize(getBaseContext());
			mdb = new DBStation(getBaseContext());
			listCity = new ArrayList<String>();
			 fragment = getSupportFragmentManager().findFragmentById(
					R.id.mapFragment);
			mPager = (ViewPager) findViewById(R.id.myfivepanelpager);
			spDistric = (Spinner) findViewById(R.id.spDistric);
			proMain=(ProgressBar) findViewById(R.id.prMain);
			btnRetry=(Button) findViewById(R.id.btnRetry);
			layout_main=(RelativeLayout) findViewById(R.id.layout_main);
			SupportMapFragment supportMap = (SupportMapFragment) fragment;
			mGooglemap = supportMap.getMap();
			// mGooglemap.setMyLocationEnabled(true);
			mGooglemap.getUiSettings().setMyLocationButtonEnabled(false);
			mGooglemap.getUiSettings().setZoomControlsEnabled(false);
			strCity = MyPreference.getInstance().getString("CITY");
			myActivity=this;
			arrStation=new ArrayList<ObjStation>();
			arrMarker=new ArrayList<Marker>();

		} catch (GooglePlayServicesNotAvailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		mGooglemap.setInfoWindowAdapter(new InfoWindowAdapter() {

			@Override
			public View getInfoWindow(Marker arg0) {
				String[] arr = arg0.getId().split("m");
				int po = Integer.parseInt(arr[1].toString());
				View v = null;
				v = getLayoutInflater().inflate(R.layout.balloon_overlay, null);

				return v;
			}

			@Override
			public View getInfoContents(Marker arg0) {
				return null;

			}
		});
	}
	/**
	 * close sliding when back
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			if (arrStation.size() > 0) {
				if (mSlidingLayer.isOpened()) {
					mSlidingLayer.closeLayer(true);
					return true;
				}
			}

		default:
			return super.onKeyDown(keyCode, event);
		}
	}

	/**
	 * method get value city from db
	 */
	public void ShowCity() {
		strWood = MyPreference.getInstance().getString("WOODENLEG");
		strCity = MyPreference.getInstance().getString("CITY");
		if (!(strWood.equalsIgnoreCase(""))) {
			listArea = mdb.getListAreabyWoodenleg(strWood);
			if (listArea.size() > 0) {
				for (int i = 0; i < listArea.size(); i++) {
					if (i > 0) {
						listCity.add(listArea.get(i).getName());
					}

				}

			}
		}
		ArrayAdapter<String> adapterCity = new ArrayAdapter<String>(
				getBaseContext(), android.R.layout.simple_spinner_item,
				listCity);
		adapterCity
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spDistric.setAdapter(adapterCity);
		if(!(strCity.equalsIgnoreCase(""))){
			index = listCity.indexOf(strCity);
			if (index != -1)
				spDistric.setSelection(index);
		}
	}

	public void setWindowPopupMap() {
		mGooglemap.setOnMarkerClickListener(new OnMarkerClickListener() {

			@Override
			public boolean onMarkerClick(Marker marker) {
				String[] arrStr = marker.getId().split("m");
				int index2 = arrMarker.indexOf(marker);
				int posMarker = Integer.parseInt(arrStr[1].toString());
				mPager.setCurrentItem(index2);
				adapter.notifyDataSetChanged();
				mPager.invalidate();

				return false;
			}
		});
	}


	@Override
	public boolean onNavigationItemSelected(int itemPosition, long itemId) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * display navatige bar under
	 */
	private void bindViews() {
		mSlidingLayer = (SlidingLayer) findViewById(R.id.slidingLayer1);
		mSlidingLayer.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return false;
			}
		});

	}

	/**
	 * Initializes the origin state of the layer
	 */
	private void initState() {
		// Sticks container to right or left
		LayoutParams rlp = (LayoutParams) mSlidingLayer.getLayoutParams();
		mSlidingLayer.setStickTo(SlidingLayer.STICK_TO_BOTTOM);
		rlp.addRule(RelativeLayout.ALIGN_BOTTOM);
		rlp.width = LayoutParams.MATCH_PARENT;
		rlp.height = getResources().getDimensionPixelSize(R.dimen.layer_width);
		rlp.height = LayoutParams.MATCH_PARENT;
		mSlidingLayer.setLayoutParams(rlp);
		mSlidingLayer.setShadowWidthRes(R.dimen.shadow_width);
		mSlidingLayer.setOffsetWidth(getResources().getDimensionPixelOffset(
				R.dimen.offset_width));
	}

	/**
	 * method onclick Setting
	 */
	public void onClickSetting(View v) {
		Intent intent = new Intent(this, SettingActivity.class);
		intent.putExtra("CHECK",true);
		startActivity(intent);
	}
	
	/**
	 * method get station from server by city
	 * @param id: id area
	 */

	public void getStation(int id) {
		if (NetworkUtility.checkNetworkState(this)) {
			proMain.setVisibility(View.VISIBLE);
			layout_main.setVisibility(View.GONE);
			StationTask.GetAllStationByIDArea(NetworkUtility.DEVICE_SERVICES,
					ParamBuilder.GetInfo(ParamBuilder.BuildDeviceData(id)),
					new AsyncHttpResponseHandler() {
						@Override
						public void onSuccess(int arg0, String response) {
							// TODO Auto-generated method stub
							super.onSuccess(arg0, response);
							if (response == null) {
								return;
							}
							arrStation = ResponseTranslater
									.getAllStation(response);
							if (arrStation.size() > 0) {
								proMain.setVisibility(View.GONE);
								layout_main.setVisibility(View.VISIBLE);
								ShowStation();
							}
							else{
								Toast.makeText(getBaseContext(), "Không có dữ liệu cho địa điểm này",
										Toast.LENGTH_SHORT).show();
							}

						}

						@Override
						public void onFailure(Throwable arg0, String arg1) {
							// TODO Auto-generated method stub
							super.onFailure(arg0, arg1);
							btnRetry.setVisibility(View.VISIBLE);
							proMain.setVisibility(View.INVISIBLE);
							Toast.makeText(getBaseContext(), "fail: " + arg1,
									Toast.LENGTH_SHORT).show();
						}
					});
		}
	}
/**
 * Show station to map
 * @param listStation: arraylist ObjStation
 */
	public void ShowStation() {
		mSlidingLayer.setVisibility(View.VISIBLE);
		for (int i = 0; i < arrStation.size(); i++) {
			LatLng lat_long_place = new LatLng(arrStation.get(i).getLat(),
					arrStation.get(i).getLng());
			
			marker = mGooglemap.addMarker(new MarkerOptions()
					.position(lat_long_place)
					.title("")
					.snippet("")
					.icon(BitmapDescriptorFactory
							.fromResource(R.drawable.marker_dot)));

			mGooglemap.animateCamera(CameraUpdateFactory.zoomTo(14.0f), 2000,
					null);

			arrMarker.add(marker);

		}
		Log.e(TAG, "d: "+arrStation.get(0).getAddress());
/*StationFragment station=new StationFragment(arrStation.get(0));
FragmentManager fm = getSupportFragmentManager();
fm.beginTransaction().replace(R.id.myfivepanelpager, station)
		.commit();*/
		adapter = new StationAdapter(getSupportFragmentManager(), arrStation,
				mGooglemap, arrMarker);
		
		mPager.setAdapter(adapter);
		adapter.notifyDataSetChanged();
		mPager.setOnPageChangeListener(adapter);
		
	//	mPager.setCurrentItem(0);
		
	}


	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		mGooglemap.clear();
		arrStation.clear();
		arrMarker.clear();
		mPager.setAdapter(null);
		 id=mdb.getIdAreaByName(spDistric.getSelectedItem().toString());
		getStation(id);
		Utils.SaveCitySelect(getBaseContext(), spDistric.getSelectedItem().toString());
	}


	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub
		
	}
	/**
	 * click to load data again
	 */
	public void OnclickRetry(View v){
		btnRetry.setVisibility(View.INVISIBLE);
		getStation(id);
		
	}
}
