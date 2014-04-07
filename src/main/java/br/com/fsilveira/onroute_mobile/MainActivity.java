package br.com.fsilveira.onroute_mobile;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import br.com.fsilveira.onroute_mobile.directions.Routing;
import br.com.fsilveira.onroute_mobile.directions.RoutingListener;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

public class MainActivity extends FragmentActivity implements RoutingListener {
	static final LatLng HAMBURG = new LatLng(53.558, 9.927);
	static final LatLng KIEL = new LatLng(53.551, 9.993);

	// @Override
	// protected void onCreate(Bundle savedInstanceState) {
	// super.onCreate(savedInstanceState);
	// setContentView(R.layout.activity_main);
	// map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();

	// Marker hamburg = map.addMarker(new MarkerOptions().position(HAMBURG).title("Hamburg").snippet("Population: 4,137,400"));
	// Marker kiel = map.addMarker(new MarkerOptions().position(KIEL).title("Kiel").snippet("Kiel is cool").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_launcher)));
	//
	// // Move the camera instantly to hamburg with a zoom of 15.
	// map.moveCamera(CameraUpdateFactory.newLatLngZoom(HAMBURG, 15));
	//
	// // Zoom in, animating the camera.
	// map.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);

	// map.moveCamera(CameraUpdateFactory.newLatLngZoom(HAMBURG, 15));
	//
	// GroundOverlayOptions newarkMap = new GroundOverlayOptions()
	// .image(BitmapDescriptorFactory.fromResource(R.drawable.ic_launcher))
	// .position(HAMBURG, 8600f, 6500f);
	//
	// // Add an overlay to the map, retaining a handle to the GroundOverlay object.
	// GroundOverlay imageOverlay = map.addGroundOverlay(newarkMap);

	// }

	protected GoogleMap map;
	protected List<LatLng> points = new ArrayList<LatLng>();

	/**
	 * This activity loads a map and then displays the route and pushpins on it.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		// SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
		// map = fm.getMap();

		map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();

		CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(-27.7994928,-49.487481));
		CameraUpdate zoom = CameraUpdateFactory.zoomTo(15);

		map.moveCamera(center);
		map.animateCamera(zoom);

		// florianopolis > -27.6142357,-48.4828248
		// lages > -27.8045404,-50.3171591
		// vacaria > -28.5083644,-50.9422932
		// torres > -29.334408,-49.7239813

		points.add(new LatLng(-27.6142357, -48.4828248));
		points.add(new LatLng(-27.8045404, -50.3171591));
		points.add(new LatLng(-28.5083644, -50.9422932));
		points.add(new LatLng(-29.334408, -49.7239813));

		Routing routing = new Routing(Routing.TravelMode.DRIVING);
		routing.registerListener(this);

		LatLng[] paintsArray = new LatLng[points.size()];
		routing.execute(points.toArray(paintsArray));
	}

	public void onRoutingSuccess(PolylineOptions mPolyOptions) {
		PolylineOptions polyoptions = new PolylineOptions();
		polyoptions.color(Color.BLUE);
		polyoptions.width(10);
		polyoptions.addAll(mPolyOptions.getPoints());
		map.addPolyline(polyoptions);

		for (LatLng point : points) {
			MarkerOptions options = new MarkerOptions();
			options.position(point);
			options.icon(BitmapDescriptorFactory.fromResource(R.drawable.start_blue));
			map.addMarker(options);
		}

	}

	public void onRoutingFailure() {
		// TODO Auto-generated method stub

	}

	public void onRoutingStart() {
		// TODO Auto-generated method stub

	}

}
