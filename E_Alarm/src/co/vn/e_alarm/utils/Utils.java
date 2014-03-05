package co.vn.e_alarm.utils;

import java.text.Format;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import org.holoeverywhere.app.AlertDialog;
import org.holoeverywhere.widget.Toast;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import co.vn.e_alarm.DetailGraphActivity;
import co.vn.e_alarm.MainActivity;
import co.vn.e_alarm.PopupLoginActitvity;
import co.vn.e_alarm.R;
import co.vn.e_alarm.bean.ObjAccount;
import co.vn.e_alarm.bean.ObjProperties;
import co.vn.e_alarm.db.MyPreference;

public class Utils {
	static Format formatter;
	 static GraphicalView mChart ;
	int[] temp = { 48, 48, 48, 48, 48, 48, 48, 43, 48, 48, 48, 48, 48, 48, 52,
			48, 48, 48, 48, 48, 46, 54, 58, 48, 48, 48, 48, 48, 48, 48 };
	int[] hum = { 80, 80, 90, 90, 80, 80, 80, 70, 80, 80, 80, 80, 80, 80, 70,
			80, 60, 80, 80, 80, 80, 80, 80, 80, 80, 80, 80, 80, 80, 80 };
	public static void SaveCitySelect(Context ctx,int city){
		MyPreference.getInstance().Initialize(ctx);
		MyPreference.getInstance().writeInteger("CITY", city);
	}
	public static void SaveDistrictSelect(Context ctx,String district){
		MyPreference.getInstance().Initialize(ctx);
		MyPreference.getInstance().writeString("DISTRICT",district);
	}
	public static void SaveNationalSelect(Context ctx,String national){
		MyPreference.getInstance().Initialize(ctx);
		MyPreference.getInstance().writeString("NATIONAL", national);
	}
	public static void SaveWoodSelect(Context ctx,String woodenleg){
		MyPreference.getInstance().Initialize(ctx);
		MyPreference.getInstance().writeString("WOODENLEG",woodenleg);
	}
	public static void SaveLanguageSelect(Context ctx,int language){
		MyPreference.getInstance().Initialize(ctx);
		MyPreference.getInstance().writeInteger("LANGUAGE",language);
	}
	public static void DrawChart(final Context ctx,int position,int[] arrTemp, int[] arrHumi,final LinearLayout chartContainer,boolean isCheck,final ArrayList<ObjProperties> listProperties ){
		
		int count = 30;
		Date[] dt = new Date[30];
		for (int i = 0; i < count; i++) {
			GregorianCalendar gc = new GregorianCalendar(2013, 12, i + 1);
			dt[i] = gc.getTime();
		}
		TimeSeries tempSeries = new TimeSeries("Temp");
		TimeSeries humilitySeries = new TimeSeries("Humility");
		TimeSeries maxSeries = new TimeSeries("Max");
		TimeSeries minSeries = new TimeSeries("Min");

		for (int i = 0; i < dt.length; i++) {
			tempSeries.add(dt[i], arrTemp[i]);
			humilitySeries.add(dt[i], arrHumi[i]);
			maxSeries.add(dt[i], 100);
			minSeries.add(dt[i], 40);
		}

		XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
		switch (position) {
		case 0:
			dataset.addSeries(humilitySeries);
			dataset.addSeries(tempSeries);
			break;
		case 1:
			dataset.addSeries(humilitySeries);
			break;
		default:
			dataset.addSeries(tempSeries);
			break;
		}
		dataset.addSeries(minSeries);
		dataset.addSeries(maxSeries);
		// custom temp
		XYSeriesRenderer tempRenderer = new XYSeriesRenderer();
		tempRenderer.setColor(ctx.getResources().getColor(R.color.orange));
		tempRenderer.setPointStyle(PointStyle.CIRCLE);//
		tempRenderer.setFillPoints(true);
		tempRenderer.setLineWidth(2);
		tempRenderer.setDisplayChartValues(true);
		// custom humility
		XYSeriesRenderer humRenderer = new XYSeriesRenderer();
		humRenderer.setColor(ctx.getResources().getColor(R.color.green));
		humRenderer.setPointStyle(PointStyle.CIRCLE);
		humRenderer.setFillPoints(true);
		humRenderer.setLineWidth(2);
		humRenderer.setDisplayChartValues(true);
		//custom min
		XYSeriesRenderer minRenderer=new XYSeriesRenderer();
		minRenderer.setColor(ctx.getResources().getColor(R.color.green3));
		minRenderer.setLineWidth(1);
		minRenderer.setDisplayChartValues(false);
		//custom max
				XYSeriesRenderer maxRenderer=new XYSeriesRenderer();
				maxRenderer.setColor(Color.RED);
				maxRenderer.setLineWidth(1);
				maxRenderer.setDisplayChartValues(false);
		XYMultipleSeriesRenderer multiRenderer = new XYMultipleSeriesRenderer();
		multiRenderer.setXTitle("Days");// Title trục X
		multiRenderer.setYAxisAlign(Align.RIGHT, 0);
		multiRenderer.setYLabelsAlign(Align.LEFT);//
		multiRenderer.setXLabelsColor(Color.WHITE);//
		multiRenderer.setYTitle("value");
		/*if(isCheck){
			multiRenderer.setZoomButtonsVisible(true);
		}
		else{*/
			multiRenderer.setZoomButtonsVisible(false);
		//}
		

		// add tempRenderer and humRenderer in multipleRenderer
		switch (position) {
		case 0:
			multiRenderer.setChartTitle("Biểu đồ Độ ẩm & Nhiệt độ");
			multiRenderer.addSeriesRenderer(humRenderer);
			multiRenderer.addSeriesRenderer(tempRenderer);

			break;
		case 1:
			multiRenderer.setChartTitle("Biểu đồ Độ ẩm");
			multiRenderer.addSeriesRenderer(humRenderer);

			break;

		default:
			multiRenderer.setChartTitle("Biểu đồ Nhiệt độ");
			multiRenderer.addSeriesRenderer(tempRenderer);

			break;
		}
		multiRenderer.addSeriesRenderer(minRenderer);
		multiRenderer.addSeriesRenderer(maxRenderer);
		 mChart = (GraphicalView) ChartFactory.getTimeChartView(ctx, dataset,
				multiRenderer, "dd-MMM-yyyy");

		multiRenderer.setClickEnabled(true);
		multiRenderer.setSelectableBuffer(30);
		mChart.setBackgroundColor(ctx.getResources().getColor(R.color.low_gray));
		if(!isCheck){
			chartContainer.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					MainActivity.isCheckHomePress=true;
					Intent intent=new Intent(ctx,DetailGraphActivity.class);
					Bundle b=new Bundle();
					b.putSerializable("OBJ_PROPERTIES", listProperties);
					intent.putExtras(b);
					ctx.startActivity(intent);
					
				}
			});
		}
	/*	if(isCheck){
			mChart.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					 formatter = new SimpleDateFormat("dd-MMM-yyyy");
					SeriesSelection seriesSelection = mChart
							.getCurrentSeriesAndPoint();
					if (seriesSelection != null) {
						long clickedDateSeconds = (long) seriesSelection
								.getXValue();
						Date clickedDate = new Date(clickedDateSeconds);
						String strDate = formatter.format(clickedDate);
						int amount = (int) seriesSelection.getValue();
						Toast.makeText(ctx, strDate + " :" + amount,
								Toast.LENGTH_SHORT).show();
					}
				}
			});
		}*/
		
		mChart.refreshDrawableState();
		chartContainer.addView(mChart);
		
	}
	/**
	 * Show sms
	 */
	public static void ShowSMS(Context ctx){
		try {
			/*Intent sendIntent = new Intent(Intent.ACTION_VIEW);
			sendIntent.setType("vnd.android-dir/mms-sms");
			ctx.startActivity(sendIntent);*/
			ctx.startActivity(new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", "0977955485", null)));
		} catch (Exception e) {
			Toast.makeText(ctx,
					"SMS faild, please try again later!",
					Toast.LENGTH_LONG).show();
			e.printStackTrace();
		}
	}
	/**
	 * call manager
	 */
	public static void CallManager(Context ctx,String phone){
		Intent intent = new Intent(Intent.ACTION_CALL,
				Uri.parse("tel:0977955485"));
		ctx.startActivity(intent);
	}
	/**
	 * Dialog authenticate again
	 */
	public static void DiaLogAuthenticate(final Activity ac){
		AlertDialog.Builder dialog=new AlertDialog.Builder(ac);
		dialog.setTitle("Thông Báo !");
		dialog.setMessage("Please login again !");
		dialog.setCancelable(false);
		dialog.setPositiveButton("OK",new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent intent=new Intent(ac,PopupLoginActitvity.class);
				ac.startActivity(intent);
				ac.finish();
				
			}
		});
		dialog.show();
	}
	/**
	 * Dialog Show info account
	 */
	public static void ShowInfo(Context ctx,ObjAccount objAccount){
		
		final Dialog dialog = new Dialog(ctx);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
		dialog.getWindow().setBackgroundDrawable(
				new ColorDrawable(Color.TRANSPARENT));
		dialog.setContentView(R.layout.popup_info);
		android.widget.Button btnOK=(android.widget.Button) dialog.findViewById(R.id.btnOk);
		TextView tvUserName=(TextView) dialog.findViewById(R.id.tvName);
		TextView tvFullName=(TextView) dialog.findViewById(R.id.tvFullName);
		TextView tvGender=(TextView) dialog.findViewById(R.id.tvGender);
		TextView tvCreateDate=(TextView) dialog.findViewById(R.id.tvCreate_Date);
		
		if(objAccount!=null){
			tvUserName.setText(""+objAccount.getUserName());
			tvFullName.setText(""+objAccount.getFullName());
			tvCreateDate.setText(""+objAccount.getCreateDate());
			if(objAccount.getGender()==1){
				tvGender.setText("Nam");
			}
			else{
				tvGender.setText("Nữ");
			}
		}
		btnOK.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				dialog.dismiss();
				
			}
		});
		
		dialog.show();
	}
}
