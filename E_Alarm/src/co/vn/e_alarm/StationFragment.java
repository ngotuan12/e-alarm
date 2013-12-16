package co.vn.e_alarm;
import java.util.ArrayList;

import org.achartengine.GraphicalView;
import co.vn.e_alarm.bean.ObjStation;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphViewSeries.GraphViewSeriesStyle;
import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

@SuppressLint("ValidFragment")
public class StationFragment extends Fragment  {
	ImageView imgAlarm;
	TextView tvAlarm,tvName,tvAddress,tvTemp,tvHumi;
	static ArrayList<ObjStation> listStation;
	int pos=0;
	View mView;
	 GraphicalView mChart;
	LinearLayout layoutGraph;
	static boolean isCheck=false;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view=inflater.inflate(R.layout.activity_detail_station, null);
		mView=view;
		initStation();
		Show();
		return view;
	}
	public static void setStationFragment(ArrayList<ObjStation> objStation){
		listStation=objStation;
	}
	public void initStation(){
		imgAlarm=(ImageView) mView.findViewById(R.id.imgAlarm);
		tvAlarm=(TextView) mView.findViewById(R.id.tvAlarm);
		tvName=(TextView) mView.findViewById(R.id.tvNameStation);
		tvAddress=(TextView) mView.findViewById(R.id.tvAddress);
		tvTemp=(TextView) mView.findViewById(R.id.tvTemp);
		tvHumi=(TextView) mView.findViewById(R.id.tvHumi);
		layoutGraph = (LinearLayout)mView.findViewById(R.id.chart_container);
		if(isCheck){
			pos=getArguments().getInt("POSITION");
		}
		//obj=(ObjStation) getArguments().getSerializable("OBJECT");
		tvName.setText(listStation.get(pos).getCode());
		tvTemp.setText(""+listStation.get(pos).getTemp());
		tvHumi.setText(""+listStation.get(pos).getHumidity());
		tvAddress.setText(listStation.get(pos).getAddress());
	}

	public static Fragment newInstance(int po) {
		StationFragment f = new StationFragment();
        Bundle b = new Bundle();
        b.putInt("POSITION", po);
        isCheck=true;
        
       // b.putSerializable("OBJECT", objStation);
        f.setArguments(b);
        return f;	}
	
	 public void Show(){
		 GraphView graphView;
		 graphView = new LineGraphView(
					getActivity() // context
					, "Tháng 1" // heading
			);
		 graphView.setHorizontalLabels(new String[] {"2 ngày trước","hôm qua", "hôm nay"});
			graphView.setVerticalLabels(new String[] {"cao", "thường", "thấp"});
			GraphViewSeries exampleSeries2 = new GraphViewSeries("nhiệt độ", new GraphViewSeriesStyle(Color.rgb(200, 50, 00), 3), new GraphViewData[] {
				new GraphViewData(1, 0.20d)
				, new GraphViewData(2, 0.15d)
				, new GraphViewData(3, 0.25d)});
			GraphViewSeries exampleSeries3 = new GraphViewSeries("độ ẩm", new GraphViewSeriesStyle(Color.GREEN, 3), new GraphViewData[] {
				new GraphViewData(1, 1.0d)
				, new GraphViewData(2, 0.5d)
				, new GraphViewData(3, 0.35d)});
			
			graphView.setManualYAxisBounds(1.0d, 0d);
			graphView.addSeries(exampleSeries2); // data
			graphView.addSeries(exampleSeries3);
			layoutGraph = (LinearLayout) mView.findViewById(R.id.chart_container);
			layoutGraph.addView(graphView);

	 }
	
}
