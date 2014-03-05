package co.vn.e_alarm;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class DistrictFragment extends Fragment {
	private static String[] arrDistrict;
	static boolean check = false;
	int position = 0;
	/** Called when the activity is first created. */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.row_district, null);
		TextView tvDistric = (TextView) view.findViewById(R.id.tvDistrict);
		if (check) {
			position = getArguments().getInt("POSITION_DISTRICT");
		}
		
		if(arrDistrict!=null){
			tvDistric.setText("" + arrDistrict[position]);
			
		}
		
		return view;
	}

	public static void SetDistrict(String[] listStatus) {
		
		arrDistrict = listStatus;
	}

	/**
	 * input position district of fragment
	 * 
	 * @param po
	 *            : position district fragment
	 * @return Fragment
	 */
	public static Fragment newInstance(int po) {
		check = true;
		DistrictFragment f = new DistrictFragment();
		Bundle b = new Bundle();
		b.putInt("POSITION_DISTRICT", po);
		f.setArguments(b);
		return f;
	}

}
