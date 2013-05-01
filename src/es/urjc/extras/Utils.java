package es.urjc.extras;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.widget.Toast;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.Tracker;

import es.urjc.whereismycar.R;

public class Utils {
	public static Tracker tracker;
	public static int TimeSendAnalytics=10;

	public static void CrearToast(Context context, String texto) {

		Toast.makeText(context, texto, Toast.LENGTH_LONG).show();
	}
	
	public static void CrearAlerta(final Activity context, String texto) {

		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		// builder.setIcon(R.drawable.icon);
		builder.setMessage(texto)
				.setCancelable(false)
				.setPositiveButton(context.getString(R.string.ok),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								// Tocame.this.finish();

							}
						});

		AlertDialog alert = builder.create();
		alert.show();

	}
	
	public static boolean haveInternet(Context context) {
		NetworkInfo info = (NetworkInfo) ((ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE))
				.getActiveNetworkInfo();

		if (info == null || !info.isConnected()) {
			return false;
		}

		if (info.isRoaming()) {
			// here is the roaming option you can change it if you want to
			// disable internet while roaming, just return false
			return true;
		}
		return true;
	}
	
	public static Tracker getTracker(Activity actv) {
		if (Constantes.tracker != null)
			return Constantes.tracker;
		else {
			EasyTracker.getInstance().activityStart(actv);
			Constantes.tracker = EasyTracker.getTracker();
			return Constantes.tracker;
		}
	}
	
	public static void trackPageGoogle(final Activity actv, final String page) {
		new AsyncTask<Integer, Integer, Integer>() {

			@SuppressWarnings("deprecation")
			@Override
			protected Integer doInBackground(Integer... params) {
				// TODO Auto-generated method stub
				if (actv != null) {
					Tracker tracker = getTracker(actv);
					tracker.trackView(page);
					EasyTracker.getInstance().dispatch();
				}
				return null;
			}
		}.execute(0);

	}

}
