package es.urjc.whereismycar;

import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import org.w3c.dom.Document;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.maps.GeoPoint;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import es.urjc.extras.DbHandler;
import es.urjc.extras.GMapV2Direction;
import es.urjc.extras.Preferences;
import es.urjc.extras.Utils;

public class MyCarActivity extends FragmentActivity implements
		LocationListener, OnClickListener {

	private float carLatitude = 0;
	private float carLongitude = 0;
	private LocationManager lm;
	private LocationListener ll;

	private GoogleMap mapView;
	private ImageView goTo;
	private ImageView lastParking;

	@SuppressWarnings("unused")
	private double latitude;
	@SuppressWarnings("unused")
	private double longitude;

	private Activity act;

	double FirstLatitude = 0;
	double FirstLongitude = 0;

	double CurrentLatitude = 0;
	double CurrentLongitude = 0;

	double PointLatitude = 0;
	double PointLongitude = 0;
	public Context mContext;

	GeoPoint src;
	GeoPoint dest;
	int color;
	GoogleMap mMapView01;
	GMapV2Direction md;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		act = this;
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mycar_activity);

		if (!Utils.haveInternet(act)) {
			Utils.CrearToast(act, getString(R.string.errorConexion));
			this.finish();
		} else {
			// carLatitude= Preferences.getLatitude(this);
			// carLongitude=Preferences.getLongitude(this);
			carLatitude = 40.342194f;
			carLongitude = -3.714734f;
			lm = (LocationManager) this
					.getSystemService(Context.LOCATION_SERVICE);

			// itemizedoverlayMyPosition = new MyOverlay(drawable2, this);
			// itemizedoverlay = new MyOverlay(drawable, this);

			SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager()
					.findFragmentById(R.id.map);

			mapView = fm.getMap();

			goTo = (ImageView) findViewById(R.id.boton);
			lastParking = (ImageView) findViewById(R.id.boton2);
			goTo.setOnClickListener(this);
			lastParking.setOnClickListener(this);
			mapView.setMyLocationEnabled(true);
			setCurrentLocation();
			drawMap();
		}
	}

//	private class MyOnClickListener implements OnClickListener {
//
//		public void onClick(View v) {
//
//			switch (v.getId()) {
//			case R.id.boton:
//				setCurrentLocation();
//				drawMap();
//
//				break;
//			case R.id.boton2:
//				DbHandler db = new DbHandler(act);
//				ArrayList<LatLng> parking = db.getLastParking(CurrentLatitude,
//						CurrentLongitude, 5);
//
//				for (LatLng p : parking) {
//					Marker marke2r = mapView.addMarker(new MarkerOptions()
//							.position(p)
//							.title("San Francisco")
//							.snippet("Population: 776733")
//							.icon(BitmapDescriptorFactory
//									.fromResource(R.drawable.marker2)));
//				}
//
//				break;
//			default:
//				break;
//			}
//
//		}
//
//	}

	/**
	 * Obtiene tu posición actual
	 */
	private void setCurrentLocation() {

		lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

		ll = new MyLocationListener();
		lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, ll);

		if (lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER) == null) {
			lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 30, 100, ll);
			if (lm.getLastKnownLocation(LocationManager.GPS_PROVIDER) != null) {
				FirstLatitude = lm.getLastKnownLocation(
						LocationManager.GPS_PROVIDER).getLatitude();
				FirstLongitude = lm.getLastKnownLocation(
						LocationManager.GPS_PROVIDER).getLongitude();
			} else {
				FirstLatitude = carLatitude;
				FirstLongitude = carLongitude;
				Toast.makeText(getApplicationContext(),
						getString(R.string.mapNopos), Toast.LENGTH_LONG).show();

			}
		} else {

			FirstLatitude = lm.getLastKnownLocation(
					LocationManager.NETWORK_PROVIDER).getLatitude();
			FirstLongitude = lm.getLastKnownLocation(
					LocationManager.NETWORK_PROVIDER).getLongitude();
		}

		latitude = FirstLatitude;
		longitude = FirstLongitude;
		lm.removeUpdates(ll);

	}

	/**
	 * Dibuja el mapa haciendo la llamada a drawpath2
	 */

	private void drawMap() {

		CurrentLatitude = FirstLatitude;
		CurrentLongitude = FirstLongitude;

		// erase the markers

		// Showing the current location in Google Map

		new GetRoute().execute(0);

	}

	// LOCATION LISTENER PARA LA POSICION
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

	private class GetRoute extends AsyncTask<Integer, Integer, Document> {

		@Override
		protected Document doInBackground(Integer... params) {
			// TODO Auto-generated method stub
			color = Color.BLUE;
			mMapView01 = mapView;

			LatLng fromPosition = new LatLng(CurrentLatitude, CurrentLongitude);
			LatLng toPosition = new LatLng(carLatitude, carLongitude);

			md = new GMapV2Direction();

			return md.getDocument(fromPosition, toPosition,
					GMapV2Direction.MODE_WALKING);
		}

		protected void onPostExecute(final Document result) {
			Document doc = result;
			if (doc != null) {
				ArrayList<LatLng> directionPoint = md.getDirection(doc);
				PolylineOptions rectLine = new PolylineOptions().width(3)
						.color(Color.RED);

				for (int i = 0; i < directionPoint.size(); i++) {
					rectLine.add(directionPoint.get(i));
				}
				mapView.clear();
				mapView.addPolyline(rectLine);
				double results = (carLatitude + CurrentLatitude) / 2;

				double resultlo = (carLongitude + CurrentLongitude) / 2;

				LatLng pos = new LatLng((double) Preferences.getLatitude(act),
						(double) Preferences.getLongitude(act));

				Marker marker = mapView.addMarker(new MarkerOptions()
						.position(new LatLng(carLatitude, carLongitude))
						.title("San Francisco")
						.snippet("Population: 776733")
						.icon(BitmapDescriptorFactory
								.fromResource(R.drawable.marker2)));

				Marker marke2r = mapView.addMarker(new MarkerOptions()
						.position(pos)
						.title("San Francisco")
						.snippet("Population: 776733")
						.icon(BitmapDescriptorFactory
								.fromResource(R.drawable.marker2)));

				mapView.animateCamera(CameraUpdateFactory.newLatLngZoom(
						new LatLng(results, resultlo), 6.0f));

			}

		}

	}

	public static LatLng getGeoPoint(JSONObject jsonObject) {

		Double lon = new Double(0);
		Double lat = new Double(0);

		try {

			lon = ((JSONArray) jsonObject.get("results")).getJSONObject(0)
					.getJSONObject("geometry").getJSONObject("location")
					.getDouble("lng");

			lat = ((JSONArray) jsonObject.get("results")).getJSONObject(0)
					.getJSONObject("geometry").getJSONObject("location")
					.getDouble("lat");

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return new LatLng(lat, lon);

	}

	void cambiarFuenteTextView(String ruta_fuente, int id_elemento) {
		Typeface tf1 = Typeface.createFromAsset(getAssets(), ruta_fuente);
		TextView tv1 = (TextView) findViewById(id_elemento);
		tv1.setTypeface(tf1);
	}

	@Override
	public void onLocationChanged(Location arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderDisabled(String arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.boton:
			setCurrentLocation();
			drawMap();

			break;
		case R.id.boton2:
			DbHandler db = new DbHandler(act);
			ArrayList<LatLng> parking = db.getLastParking(CurrentLatitude,
					CurrentLongitude, 5);

			for (LatLng p : parking) {
				Marker marke2r = mapView.addMarker(new MarkerOptions()
						.position(p)
						.title("San Francisco")
						.snippet("Population: 776733")
						.icon(BitmapDescriptorFactory
								.fromResource(R.drawable.marker2)));
			}

			break;
		default:
			break;
		}

	}
	// TODO Auto-generated method stub

}
