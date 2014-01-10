package co.vn.e_alarm.adapter;

import java.util.ArrayList;
import co.vn.e_alarm.CityFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class CityAdapter extends FragmentPagerAdapter {
	ArrayList<String> arrCity;

	public CityAdapter(FragmentManager fm,ArrayList<String> listCity) {
		super(fm);
		this.arrCity=listCity;
		CityFragment.SetListCity(arrCity);
	}

	

	@Override
	public Fragment getItem(int arg0) {
		// TODO Auto-generated method stub
		return CityFragment.newInstance(arg0);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return arrCity.size();
	}

	

}
