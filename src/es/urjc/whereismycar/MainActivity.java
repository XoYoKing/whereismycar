package es.urjc.whereismycar;

import java.util.List;

import com.google.analytics.tracking.android.Log;
import com.google.android.gms.common.GooglePlayServicesUtil;

import es.urjc.extras.DbHandler;
import es.urjc.extras.Preferences;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothClass.Device;
import android.content.Context;
import android.content.Intent;

public class MainActivity extends Activity implements OnClickListener {
	private Button btWhereIs;
	private Button btIamHere;
	private double CurrentLatitude;
	private double CurrentLongitude;
	private DbHandler db;
	private LocationManager lm;
	private MyLocationListener ll;
	private Activity actv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		btIamHere = (Button) findViewById(R.id.button1);
		btWhereIs = (Button) findViewById(R.id.button2);
		btIamHere.setOnClickListener(this);
		btWhereIs.setOnClickListener(this);
		//activateBl();
		actv = this;
		GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
		db = new DbHandler(this);
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.button2:
			Intent intent = new Intent(this, MyCarActivity.class);
			startActivity(intent);
			break;

		case R.id.button1:
			setCurrentLocation();
			break;

		default:

			break;
		}

	}

	private void setCurrentLocation() {

		lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

		ll = new MyLocationListener();
		lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, ll);

		if (lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER) == null) {
			lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 30, 100, ll);
			if (lm.getLastKnownLocation(LocationManager.GPS_PROVIDER) != null) {
				CurrentLatitude = lm.getLastKnownLocation(
						LocationManager.GPS_PROVIDER).getLatitude();
				CurrentLongitude = lm.getLastKnownLocation(
						LocationManager.GPS_PROVIDER).getLongitude();
			} else {
				Toast.makeText(getApplicationContext(),
						getString(R.string.mapNopos), Toast.LENGTH_LONG).show();

			}
		} else {

			CurrentLatitude = lm.getLastKnownLocation(
					LocationManager.NETWORK_PROVIDER).getLatitude();
			CurrentLongitude = lm.getLastKnownLocation(
					LocationManager.NETWORK_PROVIDER).getLongitude();
		}
		lm.removeUpdates(ll);

		Preferences.setLatitude(this, (float) CurrentLatitude);
		Preferences.setLongitude(this, (float) CurrentLongitude);

		db.addPosition(CurrentLatitude, CurrentLongitude);

		Toast.makeText(getApplicationContext(), getString(R.string.posSave),
				Toast.LENGTH_LONG).show();

	}

	private class MyLocationListener implements LocationListener {

		public void onLocationChanged(Location argLocation) {
			// TODO Auto-generated method stub
			CurrentLatitude = argLocation.getLatitude();
			CurrentLongitude = argLocation.getLongitude();
		}

		public void onProviderDisabled(String provider) {
			// Toast.makeText(
			// getApplicationContext(),getString(R.string.descripcionGPSDesactivado),Toast.LENGTH_LONG
			// ).show();
		}

		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub
		}

	}

	
}
