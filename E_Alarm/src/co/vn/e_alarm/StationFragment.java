package co.vn.e_alarm;
import java.util.ArrayList;
import org.achartengine.GraphicalView;
import co.vn.e_alarm.bean.ObjStation;
import co.vn.e_alarm.utils.Utils;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

@SuppressLint("ValidFragment")
public class StationFragment extends Fragment implements OnClickListener  {
	ImageView imgAlarm;
	TextView tvAlarm,tvName,tvAddress,tvTemp,tvHumi;
	static ArrayList<ObjStation> listStation=new ArrayList<ObjStation>();
	int pos=0;
	View mView;
	 GraphicalView mChart;
	LinearLayout layoutGraph,layout_header,chartContainer,layout_Log;
	static boolean isCheck=false;
	int status=0;
	double temp=0,humy=0;
	int[] temperature = {48,48,48,48,48,48,48,43,48,48,48,48,48,48,52,48,48,48,48,48,46,54,58,48,48,48,48,48,48,48};
    int[] humi = {80, 80, 90, 90, 80,80, 80, 70, 80, 80,80, 80, 80, 80, 70,80, 60, 80, 80, 80,80, 80, 80, 80, 80,80, 80, 80, 80, 80};
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view=inflater.inflate(R.layout.new_layout, null);
		mView=view;
		initStation();
		Utils.DrawChart(getActivity(), 0, temperature, humi, chartContainer, false);
		return view;
	}
	
	public static void setStationFragment(ArrayList<ObjStation> objStation){
		
		listStation=objStation;
	}
	public void initStation(){
		imgAlarm=(ImageView) mView.findViewById(R.id.imgAlarm);
		layout_header=(LinearLayout) mView.findViewById(R.id.layout_header);
		tvName=(TextView) mView.findViewById(R.id.tvNameStation);
		tvAddress=(TextView) mView.findViewById(R.id.tvAddress);
		tvTemp=(TextView) mView.findViewById(R.id.tvTemp);
		tvHumi=(TextView) mView.findViewById(R.id.tvHumi);
		layout_Log=(LinearLayout) mView.findViewById(R.id.layout_Log);
		 chartContainer = (LinearLayout) mView.findViewById(R.id.container);
		layoutGraph = (LinearLayout)mView.findViewById(R.id.chart_container);
		if(isCheck){
			pos=getArguments().getInt("POSITION");
		}
		if(listStation.size()>0){
			status=listStation.get(pos).getStatus();
			if(status==0){
				imgAlarm.setBackgroundResource(R.drawable.ic_action_cancel);
				tvTemp.setTextColor(getResources().getColor(R.color.red2));
				layout_header.setBackgroundResource(R.color.red2);
			}
			else{
				imgAlarm.setBackgroundResource(R.drawable.ic_action_accept);
				tvTemp.setTextColor(getResources().getColor(R.color.white));
				layout_header.setBackgroundResource(R.color.green);
			}
			temp=listStation.get(pos).getTemp()/100;
			humy=listStation.get(pos).getHumidity()/100;
			tvName.setText(listStation.get(pos).getCode());
			tvTemp.setText("Nhiệt độ hiện tại: "+listStation.get(pos).getTemp());
			tvHumi.setText(""+listStation.get(pos).getHumidity());
			tvAddress.setText(listStation.get(pos).getAddress());
			layout_Log.setOnClickListener(this);
		}
		
	}

	public static Fragment newInstance(int po) {
		StationFragment f = new StationFragment();
        Bundle b = new Bundle();
        b.putInt("POSITION", po);
        isCheck=true;
        
        f.setArguments(b);
        return f;	}
	@Override
	public void onClick(View arg0) {
		Intent intent=new Intent(getActivity(),ViewLogActivity.class);
		intent.putExtra("CODE_STATION", listStation.get(pos).getCode());
		intent.putExtra("STATUS_STATION", listStation.get(pos).getStatus());
		startActivity(intent);
		
	}
	
	
	
	
}
