package co.vn.e_alarm;

import org.holoeverywhere.widget.ListView;

import co.vn.e_alarm.adapter.LogAdapter;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
public class ViewLogActivity extends Activity {
	ListView lvLog;
	String codeStation="";
	int status=0;
	TextView tvCode;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_viewlog);
		init();
	}
	/**
	 * declare variables log
	 */
	public void init(){
		lvLog=(ListView) findViewById(R.id.lvLog);
		tvCode=(TextView) findViewById(R.id.tvNameStation);
		codeStation=getIntent().getExtras().getString("CODE_STATION");
		status=getIntent().getExtras().getInt("STATUS_STATION");
		LogAdapter adapter=new LogAdapter(getBaseContext(),status);
		lvLog.setAdapter(adapter);
		tvCode.setText(""+codeStation);
	}
}
