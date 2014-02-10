package co.vn.e_alarm;


import java.util.ArrayList;

import org.holoeverywhere.widget.Spinner;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class CityFragment extends Fragment{
	TextView tvCity;
	Spinner spCity;
	static ArrayList<String> arrCity;
	int position=0;
	static boolean check=false;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view=inflater.inflate(R.layout.row_city, null);
		spCity=(Spinner) view.findViewById(R.id.spCity);
		if(check){
			position=getArguments().getInt("POSITION_CITY");
		}
		return view;
	}
	/**
	 * input position city fragment
	 * @param po: position city select
	 * @return fragment city
	 */
	public static Fragment newInstance(int po) {
		 check=true;
		CityFragment f = new CityFragment();
        Bundle b = new Bundle();
        b.putInt("POSITION_CITY", po);
        f.setArguments(b);
        return f;	}
	public static void SetListCity(ArrayList<String> listCity){
		arrCity=listCity;
	}

}
