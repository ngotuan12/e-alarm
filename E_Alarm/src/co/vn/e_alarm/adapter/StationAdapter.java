package co.vn.e_alarm.adapter;

import java.util.ArrayList;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import co.vn.e_alarm.R;
import co.vn.e_alarm.StationFragment;
import co.vn.e_alarm.bean.ObjStation;
import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;

public class StationAdapter extends FragmentPagerAdapter implements
		OnPageChangeListener {
	ArrayList<Marker> listMarket;
	GoogleMap mGooglemap;
	double[] lat_cg = { 21.045901, 21.045915, 21.034707, 21.022159, 21.046044,
			21.035540, 21.036472, 21.050162, 21.037018, 21.033613, 21.045970,
			21.013656, 21.041619, 21.024840, 21.045849, 21.036362, 21.033891,
			21.045724, 21.045990, 21.037117, 21.013683, 21.045839, 21.033933,
			21.033639, 21.036732, 21.045838, 21.030950, 21.049596, 21.011481,
			21.041118, 21.037117, 21.041408, 21.024850, 21.038099, 21.045369,
			21.015041, 21.046017, 21.036362, 21.015511, 21.013603, 21.038009,
			21.013244, 21.013603, 21.038108, 21.011497, 21.036482, 21.034344,
			21.034479, 21.034574, 21.010160 };
	double[] lng_cg = { 105.790189, 105.798842, 105.7923397, 105.799697,
			105.791110, 105.792766, 105.783427, 105.789843, 105.792745,
			105.798785, 105.792804, 105.799135, 105.788099, 105.789188,
			105.790259, 105.792332, 105.769901, 105.785785, 105.794338,
			105.801653, 105.794840, 105.790268, 105.788813, 105.769826,
			105.791854, 105.790269, 105.800560, 105.789901, 105.800705,
			105.778706, 105.801653, 105.777665, 105.797267, 105.793135,
			105.797535, 105.796871, 105.783261, 105.792332, 105.804015,
			105.806124, 105.790218, 105.806570, 105.806124, 105.772281,
			105.800723, 105.789671, 105.795780, 105.789043, 105.799820,
			105.810411 };
	double[] lat_2bt = { 21.012076, 21.010389, 21.015652, 20.997523, 21.019988,
			21.015797, 21.015992, 21.010308, 21.013478, 21.019442, 21.018085,
			21.010789, 21.015982, 21.011425, 21.012616, 21.016438, 21.017154,
			21.015441, 21.010594, 21.015191, 21.012121, 21.019047, 21.014036,
			21.019878, 21.013536, 21.026978, 21.011911, 21.012331, 21.015231,
			21.010263, 21.018486, 21.018606, 20.997025, 21.012963 };
	double[] lng_2bt = { 105.849817, 105.849098, 105.855766, 105.845874,
			105.843691, 105.856453, 105.858437, 105.849168, 105.850922,
			105.842387, 105.851474, 105.850412, 105.865078, 105.850643,
			105.850423, 105.849940, 105.855600, 105.856898, 105.850198,
			105.851179, 105.850418, 105.847864, 105.860108, 105.841926,
			105.851038, 105.843943, 105.848427, 105.850398, 105.853357,
			105.847837, 105.850697, 105.856866, 105.864324, 105.849801 };
	LatLng latlng;
	ArrayList<ObjStation> arrStation=new ArrayList<ObjStation>();
	FragmentManager fmMa;
	public StationAdapter(FragmentManager fm,ArrayList<ObjStation> listStation,GoogleMap googleMap,
			ArrayList<Marker> arrMarket) {
		super(fm);
		fmMa=fm;
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
		latlng=new LatLng(arrStation.get(arg0).getLat(), arrStation.get(arg0).getLng());
		mGooglemap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 14.0f));
		Log.e("TEST: ", ""+arg0);
		listMarket.get(arg0).showInfoWindow();

	}

}
