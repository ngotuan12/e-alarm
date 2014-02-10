package co.vn.e_alarm;

import java.util.ArrayList;
import org.achartengine.GraphicalView;
import org.holoeverywhere.widget.AdapterView;
import org.holoeverywhere.widget.AdapterView.OnItemSelectedListener;
import org.holoeverywhere.widget.Spinner;
import org.holoeverywhere.widget.Toast;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphViewSeries.GraphViewSeriesStyle;
import com.loopj.android.http.AsyncHttpResponseHandler;
import co.vn.e_alarm.adapter.CustomGridViewAdapter;
import co.vn.e_alarm.bean.ObjStation;
import co.vn.e_alarm.bean.ObjProperties;
import co.vn.e_alarm.bussiness.StationTask;
import co.vn.e_alarm.customwiget.CustomGridView;
import co.vn.e_alarm.network.NetworkUtility;
import co.vn.e_alarm.network.ParamBuilder;
import co.vn.e_alarm.network.ResponseTranslater;
import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

@SuppressLint("ValidFragment")
public class StationFragment extends Fragment {
	ImageView imgAlarm;
	String TAG = "StationFragment";
	TextView tvAlarm, tvName, tvAddress;
	static ArrayList<ObjStation> listStation = new ArrayList<ObjStation>();
	int pos = 0, idDevice = 0, idDeviceProperties = 0;
	View mView;
	boolean check = false;
	GraphicalView mChart;
	LinearLayout layoutGraph, layout_header, chartContainer, layout_properties,
			layout_statusDevices;
	CustomGridView layoutStatus;
	static boolean isCheck = false;
	private GraphViewSeries exampleSeries2;
	int status = 0, type = 0;
	ArrayList<ObjProperties> listProperties;
	ArrayList<ObjProperties> listType1Properties;
	ArrayList<ObjProperties> listType2Properties;
	ArrayList<String> listNameProperties;
	CustomGridViewAdapter customGridAdapter;
	double graph2LastXValue = 0d, random = 0d, value = 0d;
	private Runnable mTime1, mTimer2;
	private final static Handler mHandler = new Handler();
	private final static Handler mHandlerLog = new Handler();
	Spinner spGraph;
	LineGraphView graphView;

	/*
	 * int[] temperature = { 48, 48, 48, 48, 48, 48, 48, 43, 48, 48, 48, 48, 48,
	 * 48, 52, 48, 48, 48, 48, 48, 46, 54, 58, 48, 48, 48, 48, 48, 48, 48 };
	 * int[] humi = { 80, 80, 90, 90, 80, 80, 80, 70, 80, 80, 80, 80, 80, 80,
	 * 70, 80, 60, 80, 80, 80, 80, 80, 80, 80, 80, 80, 80, 80, 80, 80 };
	 */

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.new_layout, null);
		mView = view;
		initStation();
		spGraph.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					final int position, long id) {
				mHandler.removeCallbacks(mTimer2);
				if (graphView != null) {
					chartContainer.post(new Runnable() {
						public void run() {

							graph2LastXValue = 0d;
							chartContainer.removeView(graphView);
							// chartContainer.removeAllViews();
							drawGraph(position);
						}
					});
				}

			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub

			}
		});

		return view;
	}

	/**
	 * set data station
	 * 
	 * @param objStation
	 */
	public static void setStationFragment(ArrayList<ObjStation> objStation) {

		listStation = objStation;
	}

	/**
	 * declare variable StationFragment
	 */
	public void initStation() {

		imgAlarm = (ImageView) mView.findViewById(R.id.imgAlarm);
		layout_header = (LinearLayout) mView.findViewById(R.id.layout_header);
		tvName = (TextView) mView.findViewById(R.id.tvNameStation);
		tvAddress = (TextView) mView.findViewById(R.id.tvAddress);
		chartContainer = (LinearLayout) mView.findViewById(R.id.container);
		layoutGraph = (LinearLayout) mView.findViewById(R.id.chart_container);
		layout_statusDevices = (LinearLayout) mView
				.findViewById(R.id.layout_statusDevices);
		layout_properties = (LinearLayout) mView
				.findViewById(R.id.layout_properties);
		spGraph = (Spinner) mView.findViewById(R.id.spGraph);
		layoutStatus = (CustomGridView) mView.findViewById(R.id.layoutStatus);
		listProperties = new ArrayList<ObjProperties>();
		listType1Properties = new ArrayList<ObjProperties>();
		listType2Properties = new ArrayList<ObjProperties>();
		listNameProperties = new ArrayList<String>();
		if (isCheck) {
			pos = getArguments().getInt("POSITION");
		}
		if (listStation.size() > 0) {
			status = listStation.get(pos).getStatus();
			if (status == 0) {
				imgAlarm.setBackgroundResource(R.drawable.ic_action_cancel);
				layout_header.setBackgroundResource(R.color.red2);
			} else {
				imgAlarm.setBackgroundResource(R.drawable.ic_action_accept);
				layout_header.setBackgroundResource(R.color.green);
			}
			listProperties = listStation.get(pos).getListPropertiesStation();
			if (listProperties.size() > 0) {

				TextView[] tvProperties = new TextView[listProperties.size()];
				for (int i = 0; i < listProperties.size(); i++) {

					type = listProperties.get(i).getTypeProperties();
					Log.e(TAG, "log: "
							+ listProperties.get(i).getNameProperties());
					if (type == 1) {
						listNameProperties.add(listProperties.get(i)
								.getNameProperties());
						listType1Properties.add(listProperties.get(i));
						String value = listProperties.get(i)
								.getNameProperties()
								+ ": "
								+ listProperties.get(i).getValueProperties();
						tvProperties[i] = new TextView(getActivity());
						tvProperties[i].setText("" + value);
						tvProperties[i].setTextColor(Color.WHITE);
						tvProperties[i].setPadding(10, 0, 0, 0);
						layout_properties.addView(tvProperties[i]);
					} else {
						listType2Properties.add(listProperties.get(i));
					}
				}
				ArrayAdapter<String> adapterNameProperties = new ArrayAdapter<String>(
						getActivity(), R.layout.row_name_properties,
						listNameProperties);

				spGraph.setAdapter(adapterNameProperties);
				adapterNameProperties
						.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);

				/*
				 * Utils.DrawChart(getActivity(), 0, temperature, humi,
				 * chartContainer, false, listType1Properties);
				 */
				if (listType2Properties.size() > 0) {
					customGridAdapter = new CustomGridViewAdapter(
							getActivity(), R.layout.row_status_station,
							listType2Properties);
					customGridAdapter.notifyDataSetChanged();
					layoutStatus.invalidate();
					layoutStatus.setAdapter(customGridAdapter);
					layout_statusDevices.setVisibility(View.VISIBLE);
				}

			}
			tvName.setText(listStation.get(pos).getCode());
			tvAddress.setText(listStation.get(pos).getAddress());
			if (graphView == null) {
				drawGraph(0);

			}

		}

	}

	/**
	 * Called when the fragment is first created.
	 * 
	 * @param po
	 *            : position current
	 * @return
	 */
	public static Fragment newInstance(int po) {
		StationFragment f = new StationFragment();
		Bundle b = new Bundle();
		b.putInt("POSITION", po);
		isCheck = true;

		f.setArguments(b);
		return f;
	}

	/**
	 * draw graph
	 * 
	 * @param position
	 *            : postion properties device
	 */
	public void drawGraph(int position) {
		idDevice = listProperties.get(0).getIdDevice();
		idDeviceProperties = listProperties.get(position)
				.getIdDeviceProperties();

		String nameProperties = "Độ Ẩm & Nhiệt Độ";
		exampleSeries2 = new GraphViewSeries("", new GraphViewSeriesStyle(
				Color.rgb(200, 50, 00), 3), new GraphViewData[] {
				new GraphViewData(1, 0), new GraphViewData(2, 0)

		});
		graphView = new LineGraphView(getActivity(), nameProperties);
		((LineGraphView) graphView).setDrawBackground(true);
		graphView.addSeries(exampleSeries2); // data
		graphView.setViewPort(1, 10);
		graphView.setScalable(true);
		chartContainer.addView(graphView);
		try {
			mTimer2 = new Runnable() {
				@Override
				public void run() {
					graph2LastXValue += 1d;

					exampleSeries2.appendData(new GraphViewData(
							graph2LastXValue, ValueLog()), true, 20);
					mHandler.postDelayed(this, 1000);
				}
			};
			mHandler.postDelayed(mTimer2, 1000);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * update value log
	 */
	public double ValueLog() {
		try {
			mTime1 = new Runnable() {

				@Override
				public void run() {

					random = GetDataLog();
					mHandlerLog.postDelayed(this, 5000);
				}
			};
			mHandlerLog.postDelayed(mTime1, 1000);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return random;

	}

	/**
	 * 
	 * @return value log
	 */
	public double GetDataLog() {

		if (NetworkUtility.checkNetworkState(getActivity())) {
			StationTask.ViewLogStation(NetworkUtility.DEVICE_SERVICES,
					ParamBuilder.GetInfo(ParamBuilder.getLog(idDevice,
							idDeviceProperties)),
					new AsyncHttpResponseHandler() {
						@Override
						public void onSuccess(int arg0, String arg1) {
							// TODO Auto-generated method stub
							super.onSuccess(arg0, arg1);
							if (arg1 == null) {
								return;
							}
							value = ResponseTranslater.GetLogDevice(arg1);

						}

						@Override
						public void onFailure(Throwable arg0, String arg1) {
							// TODO Auto-generated method stub
							if (arg1 == null) {
								Log.e(TAG, "error: "+arg1);
								mHandler.removeCallbacks(mTimer2);
								mHandlerLog.removeCallbacks(mTime1);
								mHandler.sendEmptyMessage(0);
								mHandlerLog.sendEmptyMessage(0);
								return;
							}
							super.onFailure(arg0, arg1);

							
						}
					});
		} else {
			Toast.makeText(getActivity(), "Không có mạng!", Toast.LENGTH_SHORT)
					.show();
		}
		return value;
	}

	@Override
	public void onPause() {

		Log.e(TAG, "onpause");

		super.onPause();
		mHandler.removeCallbacks(mTimer2);
		mHandlerLog.removeCallbacks(mTime1);
		mHandler.sendEmptyMessage(0);
		mHandlerLog.sendEmptyMessage(0);

	}

	public static void removeAllCallback() {
		mHandler.removeCallbacksAndMessages(null);
		mHandlerLog.removeCallbacksAndMessages(null);

	}

	@Override
	public void onResume() {
		super.onResume();
		Log.e(TAG, "onresume");

	}

	@Override
	public void onDetach() {
		// TODO Auto-generated method stub
		super.onDetach();

		Log.e(TAG, "onDetach");
	}

	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();

	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();

	}

}
