package co.vn.e_alarm;

import java.util.ArrayList;
import co.vn.e_alarm.bean.ObjArea;
import co.vn.e_alarm.db.DBStation;
import co.vn.e_alarm.db.MyPreference;
import co.vn.e_alarm.utils.Utils;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;

public class SettingActivity extends Activity implements OnItemSelectedListener {
	DBStation mdb;
	Spinner spNational, spCity;
	LinearLayout layout_city;
	ArrayList<ObjArea> listNational, listCity;
	ArrayList<String> listNameArea, listNameCity, listWood;
	String strNational,strCity;
	int indexNational=0,indexCity=0;
	private String TAG="SettingActivity";
	boolean isCheck=false;
	Activity ac;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		init();
		getAreaFromDB();
		spCity.setOnItemSelectedListener(this);
		spNational.setOnItemSelectedListener(this);
		ac=this;

	}

	public void getAreaFromDB() {
		listNational = mdb.getListAreabyParentID();
		if (listNational.size() > 0) {
			for (int i = 0; i < listNational.size(); i++) {
				listNameArea.add(listNational.get(i).getName());
				listWood.add(listNational.get(i).getWoodenleg());

			}
			listCity = mdb.getListAreabyWoodenleg(listWood.get(0));

			
		}
	
		
		ArrayAdapter<String> adapterNational = new ArrayAdapter<String>(
				getBaseContext(), android.R.layout.simple_spinner_item,
				listNameArea);
		adapterNational
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spNational.setAdapter(adapterNational);
		strNational=MyPreference.getInstance().getString("NATIONAL");
		strCity=MyPreference.getInstance().getString("CITY");
		if(!(strNational.equalsIgnoreCase(""))){
			indexNational = listNameArea.indexOf(strNational);
			if (indexNational != -1)
				spNational.setSelection(indexNational);
		}
		if (listCity.size() > 0) {
			layout_city.setVisibility(View.VISIBLE);
			for (int i = 0; i < listCity.size(); i++) {
				if (i != 0) {
					listNameCity.add(listCity.get(i).getName());
				}

			}

		}
		ArrayAdapter<String> adapterCity = new ArrayAdapter<String>(
				getBaseContext(), android.R.layout.simple_spinner_item,
				listNameCity);
		adapterCity
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spCity.setAdapter(adapterCity);
		if(!(strCity.equalsIgnoreCase(""))){
			indexCity = listNameCity.indexOf(strCity);
			if (indexCity != -1)
				spCity.setSelection(indexCity);
		}
	}

	public void init() {
		spNational = (Spinner) findViewById(R.id.spNational);
		spCity = (Spinner) findViewById(R.id.spCity);
		layout_city = (LinearLayout) findViewById(R.id.layout_city);
		mdb = new DBStation(this);
		listNameArea = new ArrayList<String>();
		listNameCity = new ArrayList<String>();
		listCity=new ArrayList<ObjArea>();
		listWood = new ArrayList<String>();
		isCheck=getIntent().getExtras().getBoolean("CHECK");
		MyPreference.getInstance().Initialize(getBaseContext());

	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int position,
			long arg3) {
		switch (arg0.getId()) {
		case R.id.spNational:
			if(listWood.size()>0){
				Log.e(TAG, "pos: "+listWood.get(position));
				Utils.SaveWoodSelect(getBaseContext(), listWood.get(position));
			}
			break;
		case R.id.spCity:
				
		default:
			break;
		}
		
		

	}
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		if(isCheck){
			
			MainActivity.myActivity.finish();
			Intent intent=new Intent(SettingActivity.this,MainActivity.class);
			startActivity(intent);
			finish();
		}
		else{
			Intent intent=new Intent(SettingActivity.this,MainActivity.class);
			startActivity(intent);
			finish();
		}
		if(listCity.size()>0){
			Utils.SaveCitySelect(getBaseContext(), spCity.getSelectedItem().toString());
		}
		if(listNational.size()>0){
			Utils.SaveNationalSelect(getBaseContext(), spNational.getSelectedItem().toString());
		}
		
		
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub

	}

}
