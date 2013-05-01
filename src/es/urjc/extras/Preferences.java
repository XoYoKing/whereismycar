package es.urjc.extras;


import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class Preferences {
	public static final String PREFS_NAME = "MyPrefsFile";
	private static float latitude;
	private static float longitude;


	
	
	public static float getLatitude(Context myContext) {
		if (latitude == 0) {
			SharedPreferences settings = myContext.getSharedPreferences(
					PREFS_NAME, 0);
			latitude = settings.getFloat("latitude", -1);
		}
		return latitude;
	}

	public static void setLatitude(Context myContext, float vlatitude) {

		
		try {
			SharedPreferences settings = myContext.getSharedPreferences(
					PREFS_NAME, 0);
			SharedPreferences.Editor editor = settings.edit();
			editor.putFloat("latitude", vlatitude);
			editor.commit();
		} catch (Exception e) {
			Log.e("ERROR", e.getMessage());

		}

	}
	
	public static float getLongitude(Context myContext) {
		if (longitude == 0) {
			SharedPreferences settings = myContext.getSharedPreferences(
					PREFS_NAME, 0);
			longitude = settings.getFloat("longitude", -1);
		}
		return longitude;
	}

	public static void setLongitude(Context myContext, float vlongitude) {

		
		try {
			SharedPreferences settings = myContext.getSharedPreferences(
					PREFS_NAME, 0);
			SharedPreferences.Editor editor = settings.edit();
			editor.putFloat("longitude", vlongitude);
			editor.commit();
		} catch (Exception e) {
			Log.e("ERROR", e.getMessage());

		}

	}

}
