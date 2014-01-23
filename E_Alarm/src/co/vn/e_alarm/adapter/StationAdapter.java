package co.vn.e_alarm.adapter;

import java.util.ArrayList;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import co.vn.e_alarm.StationFragment;
import co.vn.e_alarm.bean.ObjStation;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;

public class StationAdapter extends FragmentPagerAdapter implements
		OnPageChangeListener {
	ArrayList<Marker> listMarket;
	GoogleMap mGooglemap;
	
	LatLng latlng;
	ArrayList<ObjStation> arrStation=new ArrayList<ObjStation>();
	FragmentManager fmMa;
	public StationAdapter(FragmentManager fm,ArrayList<ObjStation> listStation,GoogleMap googleMap,
			ArrayList<Marker> arrMarket) {
		super(fm);
		listMarket = arrMarket;
		mGooglemap = googleMap;
		arrStation=listStation;
		latlng=new LatLng(arrStation.get(0).getLat(), arrStation.get(0).getLng());
		mGooglemap.animateCamera(CameraUpdateFactory.zoomTo(13.0f));
		mGooglemap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 14.0f));
		listMarket.get(0).showInfoWindow();
		StationFragment.setStationFragment(arrStation);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Fragment getItem(int arg0) {
		
		return StationFragment.newInstance(arg0);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return arrStation.size();
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		// TODO Auto-generated method stub

	}
	

	@Override
	public void onPageSelected(int arg0) {
		listMarket.get(arg0).showInfoWindow();
		mGooglemap.animateCamera(CameraUpdateFactory.zoomTo(14.0f));
		if(arrStation.size()>arg0){
		latlng=new LatLng(arrStation.get(arg0).getLat(), arrStation.get(arg0).getLng());
		mGooglemap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 14.0f));
		listMarket.get(arg0).showInfoWindow();
		}

	}

}
