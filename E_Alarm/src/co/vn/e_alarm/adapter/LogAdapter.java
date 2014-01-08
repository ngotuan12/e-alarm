package co.vn.e_alarm.adapter;

import co.vn.e_alarm.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class LogAdapter extends BaseAdapter{
	Context context;
	LayoutInflater inflater;
	int status_station;
	String[] date={"26/12/2013","26/12/2013","26/12/2013","26/12/2013","26/12/2013","26/12/2013"};
	String[] listLog={"điện áp bình thường","độ rung ổn định","nhiệt độ Cao","Thiết bị trên bo mạch bính thường",
			"thiết bị trên Enthernet bình thường","thiết bị trên GSM bình thường"};
	public LogAdapter(Context ctx,int status){
		this.context=ctx;
		status_station=status;
		inflater=(LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return date.length;
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
	public View getView(int position, View arg1, ViewGroup arg2) {
		View v=arg1;
		ViewHolder holder;
		if(v==null){
			v=inflater.inflate(R.layout.row_log, null);
			holder=new ViewHolder();
			holder.tvDate=(TextView) v.findViewById(R.id.tvDate_log);
			holder.tvDescription=(TextView) v.findViewById(R.id.tvDate_des_log);
			v.setTag(holder);
			
		}
		else{
			holder=(ViewHolder) v.getTag();
		}
		holder.tvDate.setText(""+date[position]);
		holder.tvDescription.setText(""+listLog[position]);
		
			if(status_station==0){
				if(position==2){
					holder.tvDate.setTextColor(context.getResources().getColor(R.color.red2));
					holder.tvDescription.setTextColor(context.getResources().getColor(R.color.red2));
				}
				
			}
		
		return v;
	}
	class ViewHolder{
		TextView tvDate,tvDescription;
	}

}
