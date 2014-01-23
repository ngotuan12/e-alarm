package co.vn.e_alarm.db;

import java.util.ArrayList;
import com.google.android.gms.maps.model.LatLng;
import co.vn.e_alarm.bean.ObjArea;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

public class DBStation extends SQLiteOpenHelper {
	private static final String DATABASE = "dbstation";
	// area
	public static final String TABLE_AREA = "area";
	public static final String COLUMN_ID_AREA = "idArea";
	public static final String COLUMN_ID_AREA_SERVER = "idAreaSever";
	public static final String COLUMN_CODE_AREA = "code";
	public static final String COLUMN_NAME_AREA = "nameArea";
	public static final String COLUMN_PARENT_ID = "parentID";
	public static final String COLUMN_LEVEL = "level";
	public static final String COLUMN_STATUS = "status";
	public static final String COLUMN_WOODENLEG = "woodenleg";
	public static final String COLUMN_TYPE = "type";
	public static final String COLUMN_LAT = "lat";
	public static final String COLUMN_LNG = "lng";
	//

	Context mContext = null;

	public DBStation(Context ctx) {
		super(ctx, DATABASE, null, 1);
	}

	public DBStation(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE " + TABLE_AREA + " (" + COLUMN_ID_AREA
				+ " INTEGER PRIMARY KEY AUTOINCREMENT , "
				+ COLUMN_ID_AREA_SERVER + " INTEGER, " + COLUMN_CODE_AREA
				+ " TEXT, " + COLUMN_NAME_AREA + " TEXT , " + COLUMN_PARENT_ID
				+ " INTEGER, " + COLUMN_LEVEL + " INTEGER, " + COLUMN_STATUS
				+ " INTEGER, " + COLUMN_WOODENLEG + " TEXT, " + COLUMN_TYPE
				+ " INTEGER, " + COLUMN_LAT + " TEXT, " + COLUMN_LNG
				+ " TEXT)");
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_AREA);
onCreate(db);
	
	}

	public void AddArea(ObjArea obj) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put(COLUMN_ID_AREA_SERVER, obj.getId());
		cv.put(COLUMN_CODE_AREA, obj.getCode());
		cv.put(COLUMN_NAME_AREA, obj.getName());
		cv.put(COLUMN_PARENT_ID, obj.getParentID());
		cv.put(COLUMN_LEVEL, obj.getLevel());
		cv.put(COLUMN_STATUS, obj.getStatus());
		cv.put(COLUMN_WOODENLEG, obj.getWoodenleg());
		cv.put(COLUMN_TYPE, obj.getType());
		cv.put(COLUMN_LAT, obj.getLat());
		cv.put(COLUMN_LNG, obj.getLng());
		db.insert(TABLE_AREA, null, cv);
		db.close();
	}
	@SuppressWarnings("null")
	public ArrayList<ObjArea> getListAreabyParentID(){
		ArrayList<ObjArea> listArea=new ArrayList<ObjArea>();
		SQLiteDatabase db=this.getWritableDatabase();
		String query="SELECT * FROM "+TABLE_AREA +" WHERE "+COLUMN_PARENT_ID +" = 0";
		Cursor cursor=db.rawQuery(query, null);
		if(cursor!=null || cursor.getCount()>0){
			if(cursor.moveToFirst()){
				do {
					ObjArea obj=new ObjArea();
					obj.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_ID_AREA_SERVER)));
					obj.setName(cursor.getString(cursor.getColumnIndex(COLUMN_NAME_AREA)));
					obj.setWoodenleg(cursor.getString(cursor.getColumnIndex(COLUMN_WOODENLEG)));
					listArea.add(obj);
					
					
				} while (cursor.moveToNext());
			}
		}
		return listArea;
		
	}
	@SuppressWarnings("null")
	public ArrayList<ObjArea> getListAreabyWoodenleg(String id,int level){
		ArrayList<ObjArea> listArea=new ArrayList<ObjArea>();
		SQLiteDatabase db=this.getWritableDatabase();
		String query="SELECT * FROM "+TABLE_AREA +" WHERE "+COLUMN_WOODENLEG +" like '%"+id+"%'" +" and level ="+level;
		Cursor cursor=db.rawQuery(query, null);
		if(cursor!=null || cursor.getCount()>0){
			if(cursor.moveToFirst()){
				do {
					ObjArea obj=new ObjArea();
					obj.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_ID_AREA_SERVER)));
					obj.setName(cursor.getString(cursor.getColumnIndex(COLUMN_NAME_AREA)));
					listArea.add(obj);
					
					
				} while (cursor.moveToNext());
			}
		}
		return listArea;
		
	}
	@SuppressWarnings("null")
	public ArrayList<ObjArea> getListDistrict(int id){
		ArrayList<ObjArea> listArea=new ArrayList<ObjArea>();
		SQLiteDatabase db=this.getWritableDatabase();
		String query="SELECT * FROM "+TABLE_AREA +" WHERE "+COLUMN_PARENT_ID +" ="+id;
		Cursor cursor=db.rawQuery(query, null);
		if(cursor!=null || cursor.getCount()>0){
			if(cursor.moveToFirst()){
				do {
					ObjArea obj=new ObjArea();
					obj.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_ID_AREA_SERVER)));
					obj.setName(cursor.getString(cursor.getColumnIndex(COLUMN_NAME_AREA)));
					
					listArea.add(obj);
					
					
				} while (cursor.moveToNext());
			}
		}
		return listArea;
		
	}
	public Integer getIdAreaByName(String name){
		int idUser=0;
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_AREA +" WHERE TRIM(nameArea) = '"+name.trim()+"'", null);
		if (cursor.getCount()> 0 && cursor!=null) {
			if (cursor.moveToFirst()) {
				do {
					 idUser = cursor.getInt(cursor
							.getColumnIndex(COLUMN_ID_AREA_SERVER));
				} while (cursor.moveToNext());
			}
		}
		cursor.close();
		db.close();
		return idUser;
	}
	
	/*public Integer getIdParentDistrict(String name){
		int idUser=0;
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_AREA +" WHERE TRIM(nameArea) = '"+name.trim()+"'", null);
		if (cursor.getCount()> 0 && cursor!=null) {
			if (cursor.moveToFirst()) {
				do {
					 idUser = cursor.getInt(cursor
							.getColumnIndex(COLUMN_ID_AREA_SERVER));
				} while (cursor.moveToNext());
			}
		}
		cursor.close();
		db.close();
		return idUser;
	}*/
	public String getNameArea(int id){
		String name="";
		SQLiteDatabase db=this.getWritableDatabase();
		Cursor cur = db.rawQuery("SELECT * FROM area WHERE idAreaSever = '"+id+"'", null);
			if(cur.getCount()>0){
				if(cur.moveToFirst()){
					name=cur.getString(cur.getColumnIndex(COLUMN_NAME_AREA));
				}
			}	
		return name;
		
				
	}
	
	public LatLng getLatLngCity(int id){
		LatLng latLng = null ;
		SQLiteDatabase db=this.getWritableDatabase();
		Cursor cur = db.rawQuery("SELECT * FROM area WHERE idAreaSever = '"+id+"'", null);
			if(cur.getCount()>0){
				if(cur.moveToFirst()){
					double lat=cur.getDouble(cur.getColumnIndex(COLUMN_LAT));
					double lng=cur.getDouble(cur.getColumnIndex(COLUMN_LNG));
					latLng=new LatLng(lat, lng);
					
				}
			}	
		return latLng;
	}
}