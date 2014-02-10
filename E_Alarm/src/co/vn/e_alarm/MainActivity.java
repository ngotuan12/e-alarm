package co.vn.e_alarm;

import java.util.ArrayList;
import org.holoeverywhere.widget.AdapterView;
import org.holoeverywhere.widget.AdapterView.OnItemSelectedListener;
import org.holoeverywhere.widget.Button;
import org.holoeverywhere.widget.ProgressBar;
import org.holoeverywhere.widget.Spinner;
import org.holoeverywhere.widget.Toast;
import org.holoeverywhere.widget.ViewPager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import co.vn.e_alarm.adapter.DistrictAdapter;
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
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.loopj.android.http.AsyncHttpResponseHandler;

public class MainActivity extends FragmentActivity implements
		OnItemSelectedListener {
	GoogleMap mGooglemap;
	SlidingLayer mSlidingLayer;
	ArrayList<Marker> arrMarker;
	ArrayList<ObjStation> arrStation;
	StationAdapter adapter;
	ViewPager mPager, mPagerDistric;
	Spinner spCity;
	int city;
	RelativeLayout layout_main;
	DBStation mdb;
	Marker market;
	int index = 0, count = 0, idArea = 0;
	String TAG = "MainActivity", nameDistrict = "";
	ArrayList<ObjArea> listArea, listObjDistrict;
	ArrayList<Integer> ListIDArea;
	ArrayList<String> listCity, listDistrict;
	Marker marker;
	Fragment fragment;
	ProgressBar proMain;
	public static boolean isCheckHomePress = false;
	Button btnRetry;
	String[] arrDistrict;
	OnPageChangeListener districtListener;
	DistrictAdapter districtAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		initMapFragment();
		ShowCity();
		bindViews();
		initState();
		setWindowPopupMap();
		spCity.setOnItemSelectedListener(this);
	}

	/**
	 * insert map
	 * 
	 */
	public void initMapFragment() {
		try {
			MapsInitializer.initialize(this);
			MyPreference.getInstance().Initialize(this);
			mdb = new DBStation(this);
			listCity = new ArrayList<String>();
			ListIDArea = new ArrayList<Integer>();
			fragment = getSupportFragmentManager().findFragmentById(
					R.id.mapFragment);

			mPager = (ViewPager) findViewById(R.id.myfivepanelpager);
			mPagerDistric = (ViewPager) findViewById(R.id.pagerDistric);
			spCity = (Spinner) findViewById(R.id.spCity);
			proMain = (ProgressBar) findViewById(R.id.prMain);
			btnRetry = (Button) findViewById(R.id.btnRetry);
			layout_main = (RelativeLayout) findViewById(R.id.layout_main);
			SupportMapFragment supportMap = (SupportMapFragment) fragment;
			mGooglemap = supportMap.getMap();
			mGooglemap.getUiSettings().setMyLocationButtonEnabled(false);
			mGooglemap.getUiSettings().setZoomControlsEnabled(false);

			arrStation = new ArrayList<ObjStation>();
			arrMarker = new ArrayList<Marker>();
			listDistrict = new ArrayList<String>();
			listObjDistrict = new ArrayList<ObjArea>();
			city = MyPreference.getInstance().getInteger("CITY");

		} catch (GooglePlayServicesNotAvailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		mGooglemap.setInfoWindowAdapter(new InfoWindowAdapter() {

			@Override
			public View getInfoWindow(Marker arg0) {
				View v = getLayoutInflater().inflate(R.layout.balloon_overlay,
						null);

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
				else{
					android.os.Process.killProcess(android.os.Process
							.myPid());
					finish();
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

		listArea = mdb.getListAreabyWoodenleg("001", 2);
		if (listArea.size() > 0) {

			for (int i = 0; i < listArea.size(); i++) {
				listCity.add(listArea.get(i).getName());
				ListIDArea.add(listArea.get(i).getId());

			}
			if (city == 0) {
				city = ListIDArea.get(0);
				Utils.SaveCitySelect(getBaseContext(), city);
			}

			ShowLocation();
		}

	}

	/**
	 * manager event click marker google map
	 */

	public void setWindowPopupMap() {
		mGooglemap.setOnMarkerClickListener(new OnMarkerClickListener() {

			@Override
			public boolean onMarkerClick(Marker marker) {
				// String[] arrStr = marker.getId().split("m");
				int index2 = arrMarker.indexOf(marker);
				mPager.setCurrentItem(index2);
				adapter.notifyDataSetChanged();
				mPager.invalidate();

				return false;
			}
		});
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

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

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
	 * method get station from server by city
	 * 
	 * @param id
	 *            : id area
	 */

	public void getStation(int id) {
		if (NetworkUtility.checkNetworkState(this)) {
			layout_main.setVisibility(View.GONE);
			Log.e(TAG, "" + id);
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
								layout_main.setVisibility(View.VISIBLE);
								btnRetry.setVisibility(View.INVISIBLE);
								ShowStation();
							} else {
								arrMarker.clear();
								mGooglemap.clear();
								layout_main.setVisibility(View.INVISIBLE);

								if (NetworkUtility.FAIL
										.equals(NetworkUtility.ERROR)) {
									StationFragment.removeAllCallback();
									Utils.DiaLogAuthenticate(MainActivity.this);
									
								} else {
									Toast.makeText(
											getBaseContext(),
											"Không có dữ liệu cho địa điểm này",
											Toast.LENGTH_SHORT).show();
								}
							}

							proMain.setVisibility(View.INVISIBLE);
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
	 * 
	 * @param listStation
	 *            : arraylist ObjStation
	 */
	public void ShowStation() {
		mSlidingLayer.setVisibility(View.VISIBLE);
		for (int i = 0; i < arrStation.size(); i++) {
			LatLng lat_long_place = new LatLng(arrStation.get(i).getLat(),
					arrStation.get(i).getLng());
			if (arrStation.get(i).getStatus() == 1) {
				marker = mGooglemap.addMarker(new MarkerOptions()
						.position(lat_long_place)
						.title("")
						.snippet("")
						.icon(BitmapDescriptorFactory
								.fromResource(R.drawable.marker2)));
			} else {
				marker = mGooglemap.addMarker(new MarkerOptions()
						.position(lat_long_place)
						.title("")
						.snippet("")
						.icon(BitmapDescriptorFactory
								.fromResource(R.drawable.marker_dot)));
			}

			mGooglemap.animateCamera(CameraUpdateFactory.zoomTo(14.0f), 2000,
					null);

			arrMarker.add(marker);

		}
		adapter = new StationAdapter(getSupportFragmentManager(), arrStation,
				mGooglemap, arrMarker);

		mPager.setAdapter(adapter);

		mPager.setCurrentItem(1, true);
		mPager.setCurrentItem(2, false);
		mPager.setCurrentItem(0);
		adapter.notifyDataSetChanged();
		mPager.setOnPageChangeListener(adapter);

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	/**
	 * Show location city
	 */
	public void ShowLocation() {

		ArrayAdapter<String> adapterArea = new ArrayAdapter<String>(
				getBaseContext(), R.layout.row_city, listCity);
		adapterArea
				.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
		spCity.setAdapter(adapterArea);

		String name_area = mdb.getNameArea(city);
		int index = listCity.indexOf(name_area);
		if (index != -1) {
			spCity.setSelection(index);
		}

	}

	/**
	 * Show district by id City
	 */
	public void ShowDistrict(int idCity) {

		listObjDistrict = mdb.getListDistrict(idCity);

		if (listObjDistrict.size() > 0) {
			ShowLocationDistrict(listObjDistrict);
		} else {

			listDistrict.clear();
			mPagerDistric.setAdapter(null);
			LatLng latLng = mdb.getLatLngCity(idCity);
			mGooglemap.animateCamera(CameraUpdateFactory.zoomTo(13.0f));
			mGooglemap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,
					10.0f));
			arrMarker.clear();
			mGooglemap.clear();
			layout_main.setVisibility(View.GONE);
			proMain.setVisibility(View.INVISIBLE);
		}
	}

	/**
	 * Show device by area district param id district
	 */
	public void ShowDevice(int idDistrict) {
		btnRetry.setVisibility(View.INVISIBLE);
		proMain.setVisibility(View.VISIBLE);
		idArea = mdb.getIdAreaByName(listDistrict.get(idDistrict));
		getStation(idArea);
	}

	/**
	 * Show location district
	 */
	public void ShowLocationDistrict(ArrayList<ObjArea> arrDistrict) {
		for (int i = 0; i < arrDistrict.size(); i++) {
			listDistrict.add(arrDistrict.get(i).getName());
		}
		districtAdapter = new DistrictAdapter(getSupportFragmentManager(),
				listDistrict);
		mPagerDistric.setAdapter(districtAdapter);
		districtAdapter.notifyDataSetChanged();
		ShowDevice(0);
		nameDistrict = listDistrict.get(0);
		mPagerDistric.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				mGooglemap.clear();
				arrMarker.clear();
				ShowDevice(arg0);
				nameDistrict = listDistrict.get(arg0);

			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub

			}
		});

	}

	/**
	 * click to load data again
	 */
	public void OnclickRetry(View v) {
		btnRetry.setVisibility(View.INVISIBLE);
		proMain.setVisibility(View.VISIBLE);
		getStation(idArea);

	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		arrMarker.clear();
		mGooglemap.clear();
		listObjDistrict.clear();
		listDistrict.clear();
		btnRetry.setVisibility(View.INVISIBLE);
		Utils.SaveCitySelect(getBaseContext(), ListIDArea.get(position));
		ShowDistrict(ListIDArea.get(position));

	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		// TODO Auto-generated method stub

	}

	/**
	 * method transfer atm map->list
	 */
	public void OnClickConvertList(View v) {
		if (!nameDistrict.equals("")) {
			// int id=mdb.getIdAreaByName(nameDistrict);
			Utils.SaveDistrictSelect(getBaseContext(), nameDistrict);
		}
		if (arrStation.size() > 0) {

			isCheckHomePress = true;
			Intent intent = new Intent(this, ListStationActivity.class);
			Bundle b = new Bundle();
			b.putSerializable("ARRAY_OBJECT", arrStation);
			intent.putExtras(b);
			startActivity(intent);
		} else {
			Toast.makeText(getBaseContext(), "Không có dữ liệu !",
					Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * event when user click home button
	 */
	@Override
	protected void onUserLeaveHint() {

		/*
		 * if (!isCheckHomePress) { finish(); } isCheckHomePress = false;
		 */
		super.onUserLeaveHint();

	}

}
