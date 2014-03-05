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
import com.loopj.android.http.AsyncHttpResponseHandler;
import co.vn.e_alarm.adapter.CustomGridViewAdapter;
import co.vn.e_alarm.bean.ObjAccount;
import co.vn.e_alarm.bean.ObjStation;
import co.vn.e_alarm.bean.ObjProperties;
import co.vn.e_alarm.bussiness.StationTask;
import co.vn.e_alarm.customwiget.CustomGridView;
import co.vn.e_alarm.db.DBStation;
import co.vn.e_alarm.network.NetworkUtility;
import co.vn.e_alarm.network.ParamBuilder;
import co.vn.e_alarm.network.ResponseTranslater;
import co.vn.e_alarm.utils.Utils;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

@SuppressLint("ValidFragment")
public class StationFragment extends Fragment implements
		OnItemSelectedListener, OnClickListener {
	ImageView imgAlarm;
	String TAG = "StationFragment";
	TextView tvAlarm, tvName, tvAddress,tvNameManager,tvMacAddress,tvServerConnect;
	static ArrayList<ObjStation> listStation = new ArrayList<ObjStation>();
	ObjStation obj;
	int pos = 0, idDevice = 0;
	int mPostion = 0;
	View mView;
	boolean check = false;
	static boolean isCheckMove = false;
	GraphicalView mChart;
	LinearLayout layoutGraph, layout_header, chartContainer, layout_properties,
			layout_statusDevices, layout_main, layout_child_properties,
			layout_call,layout_call_main,layout_email,layout_account;
	CustomGridView layoutStatus;
	static boolean isCheck = false;
	private GraphViewSeries series;
	int status = 0, type = 0;
	ImageButton imgFullChart;
	ArrayList<ObjProperties> listProperties;
	ArrayList<ObjProperties> listType1Properties;
	ArrayList<ObjProperties> listType2Properties;
	ArrayList<ObjProperties> listValueUpdate, listValueProperties;
	ArrayList<String> listNameProperties;
	ArrayList<Integer> listMinValue, listMaxValue;
	CustomGridViewAdapter customGridAdapter;
	double value = 0d, valueMin = 0d, valueMax = 0d;
	double lastValue = 0d;
	private Runnable mTime1, mTimer2, mTime3;
	private final static Handler mHandler = new Handler();
	private final static Handler mHandlerLog = new Handler();
	Spinner spGraph;
	LineGraphView graphView;
	TextView[] tvProperties;
	Activity ac;
	ObjAccount objAccount;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.e(TAG, "onCreateView");
		View view = inflater.inflate(R.layout.new_layout, null);
		mView = view;
		initStation();
		spGraph.setOnItemSelectedListener(this);
		layout_call.setOnClickListener(this);
		imgFullChart.setOnClickListener(this);
		layout_call_main.setOnClickListener(this);
		layout_email.setOnClickListener(this);
		layout_account.setOnClickListener(this);
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
		tvMacAddress=(TextView) mView.findViewById(R.id.tvMacAddress);
		tvServerConnect=(TextView) mView.findViewById(R.id.tvServerConnect);
		chartContainer = (LinearLayout) mView.findViewById(R.id.container);
		layoutGraph = (LinearLayout) mView.findViewById(R.id.chart_container);
		layout_main = (LinearLayout) mView.findViewById(R.id.layout_main);
		layout_call_main=(LinearLayout) mView.findViewById(R.id.layout_call_main);
		layout_email=(LinearLayout) mView.findViewById(R.id.layout_email);
		layout_account=(LinearLayout) mView.findViewById(R.id.layout_account);
		tvNameManager=(TextView) mView.findViewById(R.id.tvNameManager);
		layout_child_properties = (LinearLayout) mView
				.findViewById(R.id.layout_child_properties);
		layout_statusDevices = (LinearLayout) mView
				.findViewById(R.id.layout_statusDevices);
		layout_properties = (LinearLayout) mView
				.findViewById(R.id.layout_properties);
		spGraph = (Spinner) mView.findViewById(R.id.spGraph);
		imgFullChart = (ImageButton) mView.findViewById(R.id.imgFullChart);
		layout_call = (LinearLayout) mView.findViewById(R.id.layout_direct);
		layoutStatus = (CustomGridView) mView.findViewById(R.id.layoutStatus);
		listProperties = new ArrayList<ObjProperties>();
		listType1Properties = new ArrayList<ObjProperties>();
		listType2Properties = new ArrayList<ObjProperties>();
		listValueProperties = new ArrayList<ObjProperties>();
		listNameProperties = new ArrayList<String>();
		listMinValue = new ArrayList<Integer>();
		listMaxValue = new ArrayList<Integer>();
		if (isCheck) {
			pos = getArguments().getInt("POSITION");
		}
		if (listStation.size() > 0) {
			obj = listStation.get(pos);

		}

		SetData();
	}

	/**
	 * Set data to layout
	 */
	public void SetData() {
		DBStation mdb=new DBStation(ac);
		 objAccount=mdb.getInfoAccount();
		 if(objAccount!=null){
			 //tvNameManager.setText(objAccount.getFullName());
		 }
		 
		if (obj != null) {
			status = obj.getStatus();
			tvMacAddress.setText("Địa Chỉ Mac: "+obj.getMacAddress());
			tvServerConnect.setText("Server Connect: "+obj.getServerConnect());
			if (status == 1) {
				layout_header.setBackgroundResource(R.color.green);
				layout_main.setVisibility(View.VISIBLE);
			} else if(status==2) {
				layout_header.setBackgroundResource(R.color.red2);
				layout_main.setVisibility(View.VISIBLE);
			}
			else if(status==3){
				layout_header.setBackgroundResource(R.color.color_warning);
				layout_main.setVisibility(View.VISIBLE);
			}
			else{
				layout_header.setBackgroundResource(R.color.color_warning);
				layout_main.setVisibility(View.INVISIBLE);
			}
			listProperties = obj.getListPropertiesStation();

			if (listProperties.size() > 0) {

				for (int i = 0; i < listProperties.size(); i++) {

					type = listProperties.get(i).getTypeProperties();
					if (type == 1) {

						listNameProperties.add(listProperties.get(i)
								.getNameProperties());
						listMaxValue.add(listProperties.get(i)
								.getValueMaxProperties());
						listMinValue.add(listProperties.get(i)
								.getValueMinProperties());
						listType1Properties.add(listProperties.get(i));
						value = listType1Properties.get(0).getValueProperties();
						updateValue(i, listProperties);

					} else {
						listType2Properties.add(listProperties.get(i));
					}
				}
				ArrayAdapter<String> adapterNameProperties = new ArrayAdapter<String>(
						ac, R.layout.row_name_properties, listNameProperties);

				spGraph.setAdapter(adapterNameProperties);
				adapterNameProperties
						.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);

				if (listType2Properties.size() > 0) {
					customGridAdapter = new CustomGridViewAdapter(ac,
							R.layout.row_status_station, listType2Properties);
					customGridAdapter.notifyDataSetChanged();
					layoutStatus.invalidate();
					layoutStatus.setAdapter(customGridAdapter);
					layout_statusDevices.setVisibility(View.VISIBLE);
				}

			}
			if (graphView == null) {
				drawGraph();
			}
			tvName.setText(obj.getCode());
			tvAddress.setText(obj.getAddress());
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
	public void drawGraph() {
		ValueLog();

		idDevice = listProperties.get(0).getIdDeviceProperties();
		if (graphView != null) {
			graphView.removeSeries(series);
		}

		series = new GraphViewSeries(new GraphViewData[] {
				new GraphViewData(1, value), new GraphViewData(2, value),
				new GraphViewData(3, value), new GraphViewData(4, value) });

		graphView = new LineGraphView(ac, "Trạng thái thiết bị");
		((LineGraphView) graphView).setDrawBackground(true);
		graphView.setManualYAxisBounds(valueMax, valueMin);
		graphView.addSeries(series); // data
		graphView.setViewPort(1, 4);
		graphView.setScalable(true);
		chartContainer.addView(graphView);
		try {
			mTimer2 = new Runnable() {
				@Override
				public void run() {
					lastValue += 1d;

					series.appendData(new GraphViewData(lastValue, value),
							true, 10);
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
	public void ValueLog() {

		try {
			mTime1 = new Runnable() {

				@Override
				public void run() {

					GetDataLog();
					mHandlerLog.postDelayed(this, 5000);
				}
			};
			mHandlerLog.postDelayed(mTime1, 1000);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 
	 * @return value log
	 */
	public void GetDataLog() {
		if (listValueUpdate != null) {
			listValueUpdate.clear();
		}

		if (NetworkUtility.checkNetworkState(ac)) {
			StationTask.ViewLogStation(NetworkUtility.DEVICE_SERVICES,
					ParamBuilder.GetInfo(ParamBuilder
							.getValueProperties(idDevice)),
					new AsyncHttpResponseHandler() {
						@Override
						public void onSuccess(int arg0, String arg1) {
							// TODO Auto-generated method stub
							super.onSuccess(arg0, arg1);
							if (arg1 == null) {
								return;
							}
							listValueUpdate = ResponseTranslater
									.GetValueDevice(arg1);
							
							Log.e(TAG, "size: "+listValueUpdate.size());
							if (listValueUpdate.size() > 0) {
								listValueProperties.clear();

								for (int i = 0; i < listValueUpdate.size(); i++) {

									if (listValueUpdate.get(i)
											.getTypeProperties() == 1) {
										listValueProperties.add(listValueUpdate
												.get(i));
									}

								}

								if (listValueProperties.size() > 0) {
									if (((LinearLayout) layout_properties)
											.getChildCount() > 0) {
										((LinearLayout) layout_properties)
												.removeAllViews();
										layout_properties.invalidate();
										layout_properties.postInvalidate();
										layout_properties
												.refreshDrawableState();
										layout_properties
												.setVisibility(View.GONE);
									}

									value = listValueProperties.get(mPostion)
											.getValueProperties();

									tvProperties = new TextView[listValueProperties
											.size()];
									for (int j = 0; j < listValueProperties
											.size(); j++) {

										updateValue(j, listValueProperties);

									}

								}

							} else {
								if (NetworkUtility.FAIL
										.equals(NetworkUtility.ERROR)) {
									StationFragment.removeAllCallback();
									Utils.DiaLogAuthenticate(ac);

								}
							}

						}

						@Override
						public void onFailure(Throwable arg0, String arg1) {
							// TODO Auto-generated method stub
							if (arg1 == null) {
								/*
								 * mHandler.removeCallbacks(mTimer2);
								 * mHandlerLog.removeCallbacks(mTime1);
								 * mHandler.sendEmptyMessage(0);
								 * mHandlerLog.sendEmptyMessage(0);
								 */

								return;
							}
							super.onFailure(arg0, arg1);

						}
					});
		} else {
			Toast.makeText(ac, "Không có mạng!", Toast.LENGTH_SHORT).show();
		}
	}

	private void removeThread() {
		lastValue = 0d;
		mHandler.removeCallbacks(mTimer2);
		mHandlerLog.removeCallbacks(mTime1);
	}

	@Override
	public void onPause() {
		Log.d(TAG, "onpause");
		if (!isCheckMove) {
			removeThread();
		}

		super.onPause();

	}

	public static void removeAllCallback() {
		mHandler.removeCallbacksAndMessages(null);
		mHandlerLog.removeCallbacksAndMessages(null);

	}

	@Override
	public void onResume() {
		isCheckMove=false;
		super.onResume();

	}

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		ac = activity;
	}

	@Override
	public void onDetach() {
		// TODO Auto-generated method stub
		lastValue = 0;
		Log.d(TAG, "onDetach");

		super.onDetach();
	}

	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		Log.d(TAG, "Destroyview");
		// removeThread();
		super.onDestroyView();

	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		lastValue = 0d;
		Log.d(TAG, "Destroy");
		super.onDestroy();

	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		value = listType1Properties.get(position).getValueProperties();
		mHandler.removeCallbacks(mTimer2);
		mHandlerLog.removeCallbacks(mTime1);
		chartContainer.removeCallbacks(mTime3);
		mPostion = position;
		valueMin = listMinValue.get(position);
		valueMax = listMaxValue.get(position);
		if (graphView != null) {
			mTime3 = new Runnable() {

				@Override
				public void run() {
					lastValue = 0d;
					chartContainer.removeView(graphView);
					drawGraph();
				}
			};
			chartContainer.post(mTime3);
		}

	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		// TODO Auto-generated method stub

	}

	/**
	 * update value properties
	 */
	public void updateValue(int i, ArrayList<ObjProperties> listP) {
		tvProperties = new TextView[listP.size()];
		String value = listP.get(i).getNameProperties() + ": "
				+ listP.get(i).getValueProperties()+" "+listP.get(i).getSymbolProperties();
		tvProperties[i] = new TextView(ac);
		tvProperties[i].setText("" + value);
		if(listP.get(i).getValueProperties()>listP.get(i).getValueMaxProperties() || listP.get(i).getValueProperties()<listP.get(i).getValueMinProperties()){
			tvProperties[i].setTextColor(Color.RED);
		}
		else{
			tvProperties[i].setTextColor(Color.WHITE);
		}
	
		tvProperties[i].setPadding(10, 0, 0, 0);
		layout_properties.addView(tvProperties[i]);
		layout_properties.setVisibility(View.VISIBLE);

	}

	@Override
	public void onClick(View arg0) {

		MainActivity.isCheckHomePress = true;
		isCheckMove = true;
		switch (arg0.getId()) {
		case R.id.layout_call_main:
		case R.id.layout_call:
			Utils.CallManager(ac, "");
			break;
		case R.id.layout_email:
			Utils.ShowSMS(ac);
			
			break;
			
		case R.id.imgFullChart:
			if(listProperties!=null && listProperties.size()>0){
				Intent i=new Intent(ac,DetailGraphActivity.class);
				i.putExtra("ID_DEVICE", idDevice);
				Bundle b=new Bundle();
				b.putSerializable("LIST_PROPERTIES", listProperties);
				i.putExtras(b);
				startActivity(i);
			}
			
			break;
		case R.id.layout_account:
			 if(objAccount!=null){
				 Utils.ShowInfo(ac, objAccount);
			 }
			
			break;

		default:
			break;
		}
		

	}
	
}
