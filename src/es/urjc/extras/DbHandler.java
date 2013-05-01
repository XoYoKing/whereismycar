package es.urjc.extras;

import java.util.ArrayList;

import com.google.android.gms.maps.model.LatLng;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHandler extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION = 3;

	// Database Name
	private static final String DATABASE_NAME = "whereIsmycarDb";
	private static String TABLE_POSITIONS = "tpositions";
	private static String CREATEDB = "Create table " + TABLE_POSITIONS
			+ " (latitude double ,longitude double)";

	public DbHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATEDB);
		// TODO Auto-generated method stub

	}

	public void addPosition(double latitude, double longitude) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("latitude", latitude); // Question
		values.put("longitude", longitude);
		db.insert(TABLE_POSITIONS, null, values);
		db.close();
	}

	public ArrayList<LatLng> getLastParking(double latitude, double longitude,
			int distance) {
		ArrayList<LatLng> data = new ArrayList<LatLng>();
		String selectQuery = "SELECT  * FROM "
				+ TABLE_POSITIONS;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			do {
				double lat = cursor.getDouble(cursor.getColumnIndex("latitude"));
				double lng = cursor.getDouble(cursor.getColumnIndex("longitude"));
				LatLng pos = new LatLng(lat, lng);
				
				if (distance> getDistanceFromLatLonInKm(latitude, longitude, lat, lng))
					data.add(pos);
				
				
				
			} while (cursor.moveToNext());
		}
		db.close();
		return data;

	}
	private double getDistanceFromLatLonInKm(double lat1,double lon1,double lat2,double lon2) {
		  double R = 6371; // Radius of the earth in km
		  double dLat = deg2rad(lat2-lat1);  // deg2rad below
		  double dLon = deg2rad(lon2-lon1); 
		  double a = 
		    Math.sin(dLat/2) * Math.sin(dLat/2) +
		    Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * 
		    Math.sin(dLon/2) * Math.sin(dLon/2)
		    ; 
		  double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a)); 
		  double d = R * c; // Distance in km
		  return d;
		}

		private double deg2rad(double deg) {
		  return deg * (Math.PI/180);
		}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

}
