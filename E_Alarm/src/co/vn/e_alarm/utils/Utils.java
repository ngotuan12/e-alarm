package co.vn.e_alarm.utils;

import android.content.Context;
import co.vn.e_alarm.db.MyPreference;

public class Utils {
	public static void SaveCitySelect(Context ctx,String city){
		MyPreference.getInstance().Initialize(ctx);
		MyPreference.getInstance().writeString("CITY", city);
	}
	public static void SaveNationalSelect(Context ctx,String national){
		MyPreference.getInstance().Initialize(ctx);
		MyPreference.getInstance().writeString("NATIONAL", national);
	}
	public static void SaveWoodSelect(Context ctx,String woodenleg){
		MyPreference.getInstance().Initialize(ctx);
		MyPreference.getInstance().writeString("WOODENLEG",woodenleg);
	}
}
