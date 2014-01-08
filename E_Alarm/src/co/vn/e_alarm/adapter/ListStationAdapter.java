package co.vn.e_alarm.adapter;

import java.util.ArrayList;

import org.achartengine.GraphicalView;
import co.vn.e_alarm.R;
import co.vn.e_alarm.bean.ObjStation;
import co.vn.e_alarm.utils.Utils;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ListStationAdapter extends BaseAdapter {
	Context context;
	static ArrayList<ObjStation> arrStation;
	LayoutInflater inflater;
	 GraphicalView mChart;
	  int[] temp = {48,48,48,48,48,48,48,43,48,48,48,48,48,48,52,48,48,48,48,48,46,54,58,48,48,48,48,48,48,48};
      int[] humi = {80, 80, 90, 90, 80,80, 80, 70, 80, 80,80, 80, 80, 80, 70,80, 60, 80, 80, 80,80, 80, 80, 80, 80,80, 80, 80, 80, 80};
	@SuppressWarnings("static-access")
	public ListStationAdapter(Context ctx, ArrayList<ObjStation> listStation) {
		this.context = ctx;
		this.arrStation = listStation;
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return arrStation.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(int position, View v, ViewGroup arg2) {
		View view = v;
		ViewHolder holder;
		if (view == null) {
			view = inflater.inflate(R.layout.row_station, null);
			holder=new ViewHolder();
			holder.layout_container=(LinearLayout) view.findViewById(R.id.layout_container);
			holder.tvNameStation=(TextView) view.findViewById(R.id.tvNameStation);
			holder.layout_main=(LinearLayout) view.findViewById(R.id.layout_main);
			view.setTag(holder);
		}
		else{
			holder=(ViewHolder) view.getTag();
		}
		holder.tvNameStation.setText(""+arrStation.get(position).getCode());
		if(arrStation.get(position).getStatus()==0){
			holder.layout_main.setBackgroundResource(R.color.red2);
		}
		else{
			holder.layout_main.setBackgroundResource(R.color.green);
		}
		Utils.DrawChart(context, 0, temp, humi, holder.layout_container, false);
		return view;
	}
	public static void SetStation(ArrayList<ObjStation> list_Station){
		arrStation=list_Station;
		
	}
	class ViewHolder {
			LinearLayout layout_container,layout_main;
			TextView tvNameStation;
	}
}
