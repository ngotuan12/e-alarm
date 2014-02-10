package co.vn.e_alarm;

import java.util.ArrayList;
import org.holoeverywhere.widget.AdapterView;
import org.holoeverywhere.widget.AdapterView.OnItemSelectedListener;
import org.holoeverywhere.widget.ListView;
import org.holoeverywhere.widget.ProgressBar;
import org.holoeverywhere.widget.Spinner;
import org.holoeverywhere.widget.Toast;
import org.holoeverywhere.widget.ViewPager;
import com.loopj.android.http.AsyncHttpResponseHandler;
import co.vn.e_alarm.adapter.DistrictAdapter;
import co.vn.e_alarm.adapter.ListStationAdapter;
import co.vn.e_alarm.bean.ObjArea;
import co.vn.e_alarm.bean.ObjProperties;
import co.vn.e_alarm.bean.ObjStation;
import co.vn.e_alarm.bussiness.StationTask;
import co.vn.e_alarm.db.DBStation;
import co.vn.e_alarm.db.MyPreference;
import co.vn.e_alarm.network.NetworkUtility;
import co.vn.e_alarm.network.ParamBuilder;
import co.vn.e_alarm.network.ResponseTranslater;
import co.vn.e_alarm.utils.Utils;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ListStationActivity extends FragmentActivity implements
		OnItemSelectedListener, OnItemClickListener {
	 String TAG="ListStationActivity";
	ListView lvStation;
	ListStationAdapter adapter;
	ArrayList<ObjStation> listStation;
	ArrayList<ObjArea> listArea, listObjDistrict;
	DBStation mdb;
	ArrayList<String> listCity, listDistrict;
	ArrayList<Integer> ListIDArea;
	Spinner spCity;
	ViewPager mPagerDistric;
	DistrictAdapter districtAdapter;
	ProgressBar prMain;
	ArrayList<ObjProperties> listProperties;
	TextView tvMess;
	int city;
	
	/** Called when the activity is first created. */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_list_station);
		init();
		spCity.setOnItemSelectedListener(this);
		lvStation.setOnItemClickListener(this);
		ShowCity();
	}
/**
 * init variable 
 */
	@SuppressWarnings("unchecked")
	public void init() {
		lvStation = (ListView) findViewById(R.id.lvStation);
		spCity = (Spinner) findViewById(R.id.spCity);
		prMain = (ProgressBar) findViewById(R.id.prMain);
		mPagerDistric = (ViewPager) findViewById(R.id.pagerDistric);
		tvMess=(TextView) findViewById(R.id.tvMess);
		listStation = (ArrayList<ObjStation>) getIntent().getExtras()
				.getSerializable("ARRAY_OBJECT");
		listCity = new ArrayList<String>();
		listDistrict = new ArrayList<String>();
		MyPreference.getInstance().Initialize(getBaseContext());
		city = MyPreference.getInstance().getInteger("CITY");
		ListIDArea = new ArrayList<Integer>();
		mdb = new DBStation(getBaseContext());
		listObjDistrict = new ArrayList<ObjArea>();
		listProperties=new ArrayList<ObjProperties>();

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
		if(index!=-1){
			spCity.setSelection(index);
		}

	}
/**
 * event onclick item spinner city
 */
	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		prMain.setVisibility(View.VISIBLE);
		listObjDistrict.clear();
		listDistrict.clear();
		tvMess.setVisibility(View.INVISIBLE);
		Utils.SaveCitySelect(getBaseContext(), ListIDArea.get(position));
		ShowDistrict(ListIDArea.get(position));
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {

	}

	/**
	 * Show district
	 * param: id city
	 */
	public void ShowDistrict(int idCity) {

		listObjDistrict = mdb.getListDistrict(idCity);

		if (listObjDistrict.size() > 0) {
			ShowLocationDistrict(listObjDistrict);
		} else {

			listDistrict.clear();
			mPagerDistric.setAdapter(null);
			lvStation.setAdapter(null);
			prMain.setVisibility(View.INVISIBLE);
			tvMess.setVisibility(View.VISIBLE);

		}
	}

	/**
	 * Show location district
	 * param: arraylist area district
	 */
	public void ShowLocationDistrict(ArrayList<ObjArea> arrDistrict) {
		for (int i = 0; i < arrDistrict.size(); i++) {
			listDistrict.add(arrDistrict.get(i).getName());
		}
		

		districtAdapter = new DistrictAdapter(getSupportFragmentManager(),
				listDistrict);
		mPagerDistric.setAdapter(districtAdapter);
		String nameDistrict=MyPreference.getInstance().getString("DISTRICT");
		int indexDistrict = listDistrict.indexOf(nameDistrict);
		if(indexDistrict!=-1){
			mPagerDistric.setCurrentItem(indexDistrict);
		}
		mPagerDistric.post(new Runnable() {
			public void run() {
				districtAdapter.notifyDataSetChanged();

			}
		});

		ShowDevice(nameDistrict);
		mPagerDistric.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				
				ShowDevice(listDistrict.get(arg0));

			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub

			}
		});

	}

	/**
	 * Show device by area district
	 * param
	 */
	public void ShowDevice(String nameDistrict) {
		tvMess.setVisibility(View.INVISIBLE);
		prMain.setVisibility(View.VISIBLE);
		int idArea = mdb.getIdAreaByName(nameDistrict);
		lvStation.setAdapter(null);
		getStation(idArea);
	}

	/**
	 * method get station from server by city
	 * 
	 * @param id
	 *            : id area
	 */

	public void getStation(int id) {
		if (NetworkUtility.checkNetworkState(this)) {
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
							listStation = ResponseTranslater
									.getAllStation(response);
							if (listStation.size() > 0) {
								prMain.setVisibility(View.INVISIBLE);
								adapter = new ListStationAdapter(ListStationActivity.this, listStation);
								lvStation.setAdapter(adapter);
								adapter.notifyDataSetChanged();
							} else {
								tvMess.setVisibility(View.VISIBLE);
								lvStation.setAdapter(null);
								prMain.setVisibility(View.INVISIBLE);
							}

						}

						@Override
						public void onFailure(Throwable arg0, String arg1) {
							// TODO Auto-generated method stub
							super.onFailure(arg0, arg1);
							Toast.makeText(getBaseContext(), "fail: " + arg1,
									Toast.LENGTH_SHORT).show();
							prMain.setVisibility(View.INVISIBLE);
						}
					});
		}
	}
	@Override
	public void onItemClick(android.widget.AdapterView<?> arg0, View arg1,
			int position, long arg3) {
		listProperties=listStation.get(position).getListPropertiesStation();
		Intent intent=new Intent(this,DetailGraphActivity.class);
		Bundle b=new Bundle();
		b.putSerializable("OBJ_PROPERTIES", listProperties);
		intent.putExtras(b);
		startActivity(intent);
		
	}

		


}

