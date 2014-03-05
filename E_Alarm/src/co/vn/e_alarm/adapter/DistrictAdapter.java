package co.vn.e_alarm.adapter;

import co.vn.e_alarm.DistrictFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;

public class DistrictAdapter extends FragmentPagerAdapter implements OnPageChangeListener {
	//private ArrayList<String> arrDistrict;
	private String[] listStatus={"Tất Cả","Tốt","Mất Kết Nối","Sự Cố"};
	public DistrictAdapter(FragmentManager fm) {
		super(fm);
		//arrDistrict=listDistrict;
		DistrictFragment.SetDistrict(listStatus);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Fragment getItem(int arg0) {
		// TODO Auto-generated method stub
		return DistrictFragment.newInstance(arg0);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return listStatus.length;
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
		
	}
	@Override
	public int getItemPosition(Object object) {
	    return POSITION_NONE;
	}
	

	

}
