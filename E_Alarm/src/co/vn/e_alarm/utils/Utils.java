package co.vn.e_alarm.utils;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.SeriesSelection;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.Toast;
import co.vn.e_alarm.GraphStationActivity;
import co.vn.e_alarm.R;
import co.vn.e_alarm.db.MyPreference;

public class Utils {
	 static GraphicalView mChart ;
	int[] temp = { 48, 48, 48, 48, 48, 48, 48, 43, 48, 48, 48, 48, 48, 48, 52,
			48, 48, 48, 48, 48, 46, 54, 58, 48, 48, 48, 48, 48, 48, 48 };
	int[] hum = { 80, 80, 90, 90, 80, 80, 80, 70, 80, 80, 80, 80, 80, 80, 70,
			80, 60, 80, 80, 80, 80, 80, 80, 80, 80, 80, 80, 80, 80, 80 };
	public static void SaveCitySelect(Context ctx,int city){
		MyPreference.getInstance().Initialize(ctx);
		MyPreference.getInstance().writeInteger("CITY", city);
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
	public static void DrawChart(final Context ctx,int position,int[] arrTemp, int[] arrHumi,final LinearLayout chartContainer,boolean isCheck ){
		
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
		//minRenderer.setPointStyle(PointStyle.CIRCLE);
		//minRenderer.setFillPoints(true);
		minRenderer.setLineWidth(1);
		minRenderer.setDisplayChartValues(false);
		//custom max
				XYSeriesRenderer maxRenderer=new XYSeriesRenderer();
				maxRenderer.setColor(Color.RED);
				//maxRenderer.setPointStyle(PointStyle.CIRCLE);
				//maxRenderer.setFillPoints(true);
				maxRenderer.setLineWidth(1);
				maxRenderer.setDisplayChartValues(false);
		XYMultipleSeriesRenderer multiRenderer = new XYMultipleSeriesRenderer();
		multiRenderer.setXTitle("Days");// Title trục X
		multiRenderer.setYAxisAlign(Align.RIGHT, 0);
		multiRenderer.setYLabelsAlign(Align.LEFT);//
		multiRenderer.setXLabelsColor(Color.WHITE);//
		multiRenderer.setYTitle("value");
		if(isCheck){
			multiRenderer.setZoomButtonsVisible(true);
		}
		else{
			multiRenderer.setZoomButtonsVisible(false);
		}
		

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
					Intent intent=new Intent(ctx,GraphStationActivity.class);
					ctx.startActivity(intent);
					
				}
			});
		}
		if(isCheck){
			mChart.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Format formatter = new SimpleDateFormat("dd-MMM-yyyy");
					SeriesSelection seriesSelection = mChart
							.getCurrentSeriesAndPoint();
					if (seriesSelection != null) {
						int seriesIndex = seriesSelection.getSeriesIndex();
						String selectedSeries = "";
						if (seriesIndex == 0)
							selectedSeries = "Độ ẩm";
						else
							selectedSeries = "Nhiệt độ";
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
		}
		
		mChart.refreshDrawableState();
		chartContainer.addView(mChart);
		
	}
}
