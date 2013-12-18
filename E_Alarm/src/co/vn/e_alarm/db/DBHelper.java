package co.vn.e_alarm.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {
	private String creationString;
	private String tableName;

	
	public DBHelper(Context context, String name, CursorFactory factory,
			int version, String tableName, String creationString) {
		super(context, name, factory, version);
		this.creationString = creationString;
		this.tableName = tableName;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(creationString);
	}

	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Log the version upgrade
		Log.w("TaskDBAdapter", "Upgrading from version " + oldVersion + " to "
				+ newVersion + ", which will destroy all old data");

		db.execSQL("DROP TABLE IF EXISTS " + tableName);
		onCreate(db);

	}

	
	@Override
	public void onOpen(SQLiteDatabase db) {
		db.execSQL(creationString);
	}

}
