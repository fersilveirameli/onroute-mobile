package br.com.fsilveira.onroute_mobile;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import br.com.fsilveira.onroute_mobile.api.TaskPerformedItineraryAPI;
import br.com.fsilveira.onroute_mobile.directions.DirectionsException;
import br.com.fsilveira.onroute_mobile.directions.Route;
import br.com.fsilveira.onroute_mobile.directions.RoutingAPI;
import br.com.fsilveira.onroute_mobile.directions.RoutingListener;
import br.com.fsilveira.onroute_mobile.directions.TravelMode;
import br.com.fsilveira.onroute_mobile.listener.PerformedItineraryListener;
import br.com.fsilveira.onroute_mobile.model.PerformedPoint;
import br.com.fsilveira.onroute_mobile.model.Travel;
import br.com.fsilveira.onroute_mobile.model.WayPoint;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

public class MapsFragment extends Fragment implements RoutingListener, PerformedItineraryListener {

	private static final String ARG_SECTION_NUMBER = "section_number";

	public static MapsFragment newInstance(int sectionNumber, Travel travel) {
		MapsFragment fragment = new MapsFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_SECTION_NUMBER, sectionNumber);
		args.putSerializable("travel", travel);
		fragment.setArguments(args);
		return fragment;
	}

	public MapsFragment() {
	}

	protected GoogleMap map;
	protected List<String> pointsStr = new ArrayList<String>();
	private RoutingAPI<String> routingAPI;
	private TaskPerformedItineraryAPI itineraryAPI;
	private PolylineOptions polyoptionsRelized;
	private List<PerformedPoint> performedPoints;
	private Travel travel;
	private Marker marker;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_map, container, false);
		map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();

		TextView name = (TextView) rootView.findViewById(R.id.name);
		TextView date = (TextView) rootView.findViewById(R.id.date);

		travel = (Travel) getArguments().getSerializable("travel");

		name.setText(travel.getRoute().getName());
		date.setText("(" + travel.getDate() + ")");

		List<String> pointsStr = new ArrayList<String>();
		pointsStr.add(travel.getRoute().getOrigin());
		for (WayPoint wayPoint : travel.getRoute().getWayPoints()) {
			pointsStr.add(wayPoint.getPoint());
		}
		pointsStr.add(travel.getRoute().getDestination());

		String[] paintsArray = new String[pointsStr.size()];
		routingAPI = new RoutingAPI<String>(TravelMode.DRIVING, this);
		routingAPI.execute(pointsStr.toArray(paintsArray));

		return rootView;
	}

	private void update(Travel travel) {
		itineraryAPI = new TaskPerformedItineraryAPI(MapsFragment.this);
		itineraryAPI.execute(travel);
	}

	@Override
	public void onDestroyView() {

		super.onDestroyView();
		Fragment fragment = (getFragmentManager().findFragmentById(R.id.map));
		FragmentTransaction ft = getActivity().getFragmentManager().beginTransaction();
		ft.remove(fragment);
		ft.commit();
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		((MainActivity) activity).onSectionAttached(getArguments().getInt(ARG_SECTION_NUMBER));
	}

	public void onRoutingSuccess(PolylineOptions mPolyOptions, Route route) {
		buildPlanning(mPolyOptions, route);
		buildRealized();
	}

	public void buildRealized() {

		polyoptionsRelized = new PolylineOptions();
		polyoptionsRelized.color(Color.RED);
		polyoptionsRelized.width(8);

		map.addPolyline(polyoptionsRelized);

		Thread thread = new Thread() {
			@Override
			public void run() {

				while (true) {
					System.out.println("exec");
					update(travel);
					try {
						Thread.sleep(10000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		};
		thread.start();

	}

	public void onItineraryUpdateSuccess(List<PerformedPoint> list) {
		performedPoints = list;
		polyoptionsRelized = new PolylineOptions();
		for (PerformedPoint point : list) {
			polyoptionsRelized.add(point.getLatLng());
		}

		polyoptionsRelized.color(Color.RED);
		polyoptionsRelized.width(8);

		map.addPolyline(polyoptionsRelized);

		LatLng currentPosition = performedPoints.get(performedPoints.size() - 1).getLatLng();
		updatePosition(currentPosition);

	}

	private void updatePosition(LatLng currentPosition) {
		if (marker == null) {
			MarkerOptions options = new MarkerOptions();
			options.position(currentPosition);
			marker = map.addMarker(options);
			options.icon(BitmapDescriptorFactory.defaultMarker());
		} else {
			marker.setPosition(currentPosition);
		}

		zoom(currentPosition, 13);
	}

	private void zoom(LatLng currentPosition, float zoomTo) {
		CameraUpdate center = CameraUpdateFactory.newLatLng(currentPosition);
		CameraUpdate zoom = CameraUpdateFactory.zoomTo(zoomTo);

		map.moveCamera(center);
		map.animateCamera(zoom);
	}

	private void buildPlanning(PolylineOptions mPolyOptions, Route route) {
		PolylineOptions polyoptionsPlanning = new PolylineOptions();
		polyoptionsPlanning.color(Color.BLUE);
		polyoptionsPlanning.width(10);
		polyoptionsPlanning.addAll(mPolyOptions.getPoints());
		map.addPolyline(polyoptionsPlanning);

		// map.setTrafficEnabled(true);

		int index = 0;
		for (LatLng point : route.getMarkers()) {
			BitmapDescriptor bitmap;

			if (index == 0) {
				bitmap = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE);
			} else if (index == route.getMarkers().size() - 1) {
				bitmap = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN);
			} else {
				bitmap = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW);
			}

			MarkerOptions options = new MarkerOptions();
			options.position(point);

			options.icon(bitmap);
			map.addMarker(options);

			index++;
		}

		if (index > 0) {
			zoom(route.getMarkers().get(0), 7);
		}
	}

	public void onRoutingFailure(DirectionsException e) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this.getActivity());

		// set title
		alertDialogBuilder.setTitle("Erro");

		// set dialog message
		alertDialogBuilder.setMessage(e.getMessage());
		alertDialogBuilder.setCancelable(false);
		// alertDialogBuilder.setNeutralButton("Yes", new DialogInterface.OnClickListener() {
		// public void onClick(DialogInterface dialog, int id) {
		//
		// }
		// });
		// alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
		// public void onClick(DialogInterface dialog, int id) {
		// // if this button is clicked, just close
		// // the dialog box and do nothing
		// dialog.cancel();
		// }
		// });

		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();

		// show it
		alertDialog.show();

	}

	public void onRoutingStart() {
		// TODO Auto-generated method stub

	}

	private List<LatLng> getPerformedPoints() {
		List<LatLng> latLngs = new ArrayList<LatLng>();
		latLngs.add(new LatLng(-27.59915, -48.54522));
		latLngs.add(new LatLng(-27.59971, -48.54547));
		latLngs.add(new LatLng(-27.60046, -48.54583));
		latLngs.add(new LatLng(-27.6008, -48.54598));
		latLngs.add(new LatLng(-27.60083, -48.546));
		latLngs.add(new LatLng(-27.60087, -48.54601));
		latLngs.add(new LatLng(-27.60091, -48.54603));
		latLngs.add(new LatLng(-27.60095, -48.54605));
		latLngs.add(new LatLng(-27.601, -48.54606));
		latLngs.add(new LatLng(-27.60102, -48.54607));
		latLngs.add(new LatLng(-27.60102, -48.54607));
		latLngs.add(new LatLng(-27.60109, -48.5461));
		latLngs.add(new LatLng(-27.60115, -48.54611));
		latLngs.add(new LatLng(-27.60119, -48.54612));
		latLngs.add(new LatLng(-27.60126, -48.54614));
		latLngs.add(new LatLng(-27.60134, -48.54616));
		latLngs.add(new LatLng(-27.60151, -48.54618));
		latLngs.add(new LatLng(-27.60167, -48.54621));
		latLngs.add(new LatLng(-27.60167, -48.54621));
		latLngs.add(new LatLng(-27.60168, -48.5462));
		latLngs.add(new LatLng(-27.60169, -48.54619));
		latLngs.add(new LatLng(-27.60169, -48.54617));
		latLngs.add(new LatLng(-27.6017, -48.54616));
		latLngs.add(new LatLng(-27.6017, -48.54615));
		latLngs.add(new LatLng(-27.60171, -48.54614));
		latLngs.add(new LatLng(-27.60171, -48.54612));
		latLngs.add(new LatLng(-27.60171, -48.54611));
		latLngs.add(new LatLng(-27.60171, -48.5461));
		latLngs.add(new LatLng(-27.60171, -48.54608));
		latLngs.add(new LatLng(-27.60171, -48.54607));
		latLngs.add(new LatLng(-27.60171, -48.54605));
		latLngs.add(new LatLng(-27.60171, -48.54604));
		latLngs.add(new LatLng(-27.60152, -48.54602));
		latLngs.add(new LatLng(-27.60132, -48.54601));
		latLngs.add(new LatLng(-27.60121, -48.54601));
		latLngs.add(new LatLng(-27.60108, -48.54598));
		latLngs.add(new LatLng(-27.60106, -48.54597));
		latLngs.add(new LatLng(-27.60103, -48.54596));
		latLngs.add(new LatLng(-27.60103, -48.54596));
		latLngs.add(new LatLng(-27.60101, -48.54596));
		latLngs.add(new LatLng(-27.60099, -48.54595));
		latLngs.add(new LatLng(-27.60097, -48.54595));
		latLngs.add(new LatLng(-27.60095, -48.54594));
		latLngs.add(new LatLng(-27.60092, -48.54592));
		latLngs.add(new LatLng(-27.60068, -48.54581));
		latLngs.add(new LatLng(-27.60045, -48.5457));
		latLngs.add(new LatLng(-27.60022, -48.5456));
		latLngs.add(new LatLng(-27.59976, -48.54538));
		latLngs.add(new LatLng(-27.59968, -48.54535));
		latLngs.add(new LatLng(-27.59914, -48.5451));
		latLngs.add(new LatLng(-27.5986, -48.54487));
		latLngs.add(new LatLng(-27.5984, -48.54478));
		latLngs.add(new LatLng(-27.59819, -48.5447));
		latLngs.add(new LatLng(-27.59731, -48.54429));
		latLngs.add(new LatLng(-27.59642, -48.54387));
		latLngs.add(new LatLng(-27.59629, -48.54382));
		latLngs.add(new LatLng(-27.59616, -48.54376));
		latLngs.add(new LatLng(-27.59557, -48.54351));
		latLngs.add(new LatLng(-27.59554, -48.5435));
		latLngs.add(new LatLng(-27.59552, -48.54349));
		latLngs.add(new LatLng(-27.59549, -48.54348));
		latLngs.add(new LatLng(-27.59547, -48.54347));
		latLngs.add(new LatLng(-27.59544, -48.54346));
		latLngs.add(new LatLng(-27.59541, -48.54345));
		latLngs.add(new LatLng(-27.59538, -48.54344));
		latLngs.add(new LatLng(-27.59536, -48.54343));
		latLngs.add(new LatLng(-27.59533, -48.54343));
		latLngs.add(new LatLng(-27.5953, -48.54342));
		latLngs.add(new LatLng(-27.59524, -48.54341));
		latLngs.add(new LatLng(-27.59522, -48.54341));
		latLngs.add(new LatLng(-27.5952, -48.54341));
		latLngs.add(new LatLng(-27.59517, -48.5434));
		latLngs.add(new LatLng(-27.59515, -48.5434));
		latLngs.add(new LatLng(-27.59513, -48.5434));
		latLngs.add(new LatLng(-27.5951, -48.54341));
		latLngs.add(new LatLng(-27.59487, -48.5434));
		latLngs.add(new LatLng(-27.59426, -48.54336));
		latLngs.add(new LatLng(-27.59423, -48.54336));
		latLngs.add(new LatLng(-27.59419, -48.54337));
		latLngs.add(new LatLng(-27.59401, -48.54336));
		latLngs.add(new LatLng(-27.59397, -48.54336));
		latLngs.add(new LatLng(-27.59394, -48.54336));
		latLngs.add(new LatLng(-27.59387, -48.54335));
		latLngs.add(new LatLng(-27.59383, -48.54334));
		latLngs.add(new LatLng(-27.59379, -48.54334));
		latLngs.add(new LatLng(-27.59375, -48.54333));
		latLngs.add(new LatLng(-27.59371, -48.54333));
		latLngs.add(new LatLng(-27.59315, -48.54328));
		latLngs.add(new LatLng(-27.59302, -48.54327));
		latLngs.add(new LatLng(-27.59281, -48.54325));
		latLngs.add(new LatLng(-27.5926, -48.54323));
		latLngs.add(new LatLng(-27.59218, -48.54319));
		latLngs.add(new LatLng(-27.59208, -48.54318));
		latLngs.add(new LatLng(-27.59198, -48.54317));
		latLngs.add(new LatLng(-27.59164, -48.54315));
		latLngs.add(new LatLng(-27.59131, -48.54312));
		latLngs.add(new LatLng(-27.59098, -48.5431));
		latLngs.add(new LatLng(-27.59066, -48.54308));
		latLngs.add(new LatLng(-27.59039, -48.54305));
		latLngs.add(new LatLng(-27.59033, -48.54305));
		latLngs.add(new LatLng(-27.59027, -48.54304));
		latLngs.add(new LatLng(-27.59021, -48.54303));
		latLngs.add(new LatLng(-27.5902, -48.54303));
		latLngs.add(new LatLng(-27.59012, -48.54302));
		latLngs.add(new LatLng(-27.59003, -48.54302));
		latLngs.add(new LatLng(-27.58998, -48.54301));
		latLngs.add(new LatLng(-27.58992, -48.54301));
		latLngs.add(new LatLng(-27.58985, -48.543));
		latLngs.add(new LatLng(-27.58979, -48.543));
		latLngs.add(new LatLng(-27.58967, -48.543));
		latLngs.add(new LatLng(-27.58961, -48.54299));
		latLngs.add(new LatLng(-27.58954, -48.54299));
		latLngs.add(new LatLng(-27.58915, -48.54298));
		latLngs.add(new LatLng(-27.58874, -48.54298));
		latLngs.add(new LatLng(-27.58832, -48.543));
		latLngs.add(new LatLng(-27.58752, -48.54302));
		latLngs.add(new LatLng(-27.58743, -48.54302));
		latLngs.add(new LatLng(-27.58734, -48.54303));
		latLngs.add(new LatLng(-27.5873, -48.54303));
		latLngs.add(new LatLng(-27.58726, -48.54304));
		latLngs.add(new LatLng(-27.58721, -48.54305));
		latLngs.add(new LatLng(-27.58717, -48.54305));
		latLngs.add(new LatLng(-27.58713, -48.54306));
		latLngs.add(new LatLng(-27.58704, -48.54308));
		latLngs.add(new LatLng(-27.587, -48.54309));
		latLngs.add(new LatLng(-27.58696, -48.5431));
		latLngs.add(new LatLng(-27.58689, -48.54312));
		latLngs.add(new LatLng(-27.58687, -48.54312));
		latLngs.add(new LatLng(-27.58684, -48.54312));
		latLngs.add(new LatLng(-27.58677, -48.54315));
		latLngs.add(new LatLng(-27.58672, -48.54317));
		latLngs.add(new LatLng(-27.58569, -48.54358));
		latLngs.add(new LatLng(-27.58501, -48.54386));
		latLngs.add(new LatLng(-27.58498, -48.54387));
		latLngs.add(new LatLng(-27.58496, -48.54388));
		latLngs.add(new LatLng(-27.58493, -48.54389));
		latLngs.add(new LatLng(-27.58491, -48.5439));
		latLngs.add(new LatLng(-27.58487, -48.54393));
		latLngs.add(new LatLng(-27.58486, -48.54393));
		latLngs.add(new LatLng(-27.58484, -48.54395));
		latLngs.add(new LatLng(-27.58482, -48.54396));
		latLngs.add(new LatLng(-27.58479, -48.54398));
		latLngs.add(new LatLng(-27.58477, -48.54399));
		latLngs.add(new LatLng(-27.58475, -48.54401));
		latLngs.add(new LatLng(-27.58473, -48.54403));
		latLngs.add(new LatLng(-27.5846, -48.54413));
		latLngs.add(new LatLng(-27.5839, -48.54464));
		latLngs.add(new LatLng(-27.58374, -48.54476));
		latLngs.add(new LatLng(-27.58357, -48.54487));
		latLngs.add(new LatLng(-27.58357, -48.54487));
		latLngs.add(new LatLng(-27.58345, -48.54491));
		latLngs.add(new LatLng(-27.58343, -48.54492));
		latLngs.add(new LatLng(-27.5834, -48.54493));
		latLngs.add(new LatLng(-27.58336, -48.54493));
		latLngs.add(new LatLng(-27.58333, -48.54493));
		latLngs.add(new LatLng(-27.58324, -48.54493));
		latLngs.add(new LatLng(-27.58322, -48.54491));
		latLngs.add(new LatLng(-27.58314, -48.54484));
		latLngs.add(new LatLng(-27.58314, -48.54484));
		latLngs.add(new LatLng(-27.58307, -48.54479));
		latLngs.add(new LatLng(-27.58299, -48.54473));
		latLngs.add(new LatLng(-27.5829, -48.54468));
		latLngs.add(new LatLng(-27.58281, -48.54462));
		latLngs.add(new LatLng(-27.58272, -48.54458));
		latLngs.add(new LatLng(-27.58263, -48.54454));
		latLngs.add(new LatLng(-27.58253, -48.5445));
		latLngs.add(new LatLng(-27.5824, -48.54446));
		latLngs.add(new LatLng(-27.58192, -48.54437));
		latLngs.add(new LatLng(-27.58089, -48.54413));
		latLngs.add(new LatLng(-27.58079, -48.54411));
		latLngs.add(new LatLng(-27.58061, -48.54408));
		latLngs.add(new LatLng(-27.58013, -48.54399));
		latLngs.add(new LatLng(-27.57978, -48.54393));
		latLngs.add(new LatLng(-27.57974, -48.54393));
		latLngs.add(new LatLng(-27.57925, -48.54386));
		latLngs.add(new LatLng(-27.579, -48.54378));
		latLngs.add(new LatLng(-27.57875, -48.54374));
		latLngs.add(new LatLng(-27.57848, -48.54368));
		latLngs.add(new LatLng(-27.57836, -48.54366));
		latLngs.add(new LatLng(-27.57822, -48.54363));
		latLngs.add(new LatLng(-27.57798, -48.54358));
		latLngs.add(new LatLng(-27.57775, -48.54352));
		latLngs.add(new LatLng(-27.57745, -48.54344));
		latLngs.add(new LatLng(-27.57733, -48.54341));
		latLngs.add(new LatLng(-27.57722, -48.54338));
		latLngs.add(new LatLng(-27.5771, -48.54334));
		latLngs.add(new LatLng(-27.57699, -48.5433));
		latLngs.add(new LatLng(-27.57688, -48.54325));
		latLngs.add(new LatLng(-27.57677, -48.5432));
		latLngs.add(new LatLng(-27.57661, -48.54311));
		latLngs.add(new LatLng(-27.57655, -48.54306));
		latLngs.add(new LatLng(-27.57641, -48.54296));
		latLngs.add(new LatLng(-27.57627, -48.54286));
		latLngs.add(new LatLng(-27.57621, -48.54282));
		latLngs.add(new LatLng(-27.57603, -48.5427));
		latLngs.add(new LatLng(-27.57595, -48.54264));
		latLngs.add(new LatLng(-27.57586, -48.54257));
		latLngs.add(new LatLng(-27.57578, -48.5425));
		latLngs.add(new LatLng(-27.57577, -48.54249));
		latLngs.add(new LatLng(-27.57569, -48.54242));
		latLngs.add(new LatLng(-27.57561, -48.54234));
		latLngs.add(new LatLng(-27.57554, -48.54227));
		latLngs.add(new LatLng(-27.57539, -48.5421));
		latLngs.add(new LatLng(-27.57529, -48.54198));
		latLngs.add(new LatLng(-27.57519, -48.54186));
		latLngs.add(new LatLng(-27.57509, -48.54174));
		latLngs.add(new LatLng(-27.575, -48.54161));
		latLngs.add(new LatLng(-27.57496, -48.54153));
		latLngs.add(new LatLng(-27.57466, -48.54103));
		latLngs.add(new LatLng(-27.57455, -48.54079));
		latLngs.add(new LatLng(-27.57448, -48.54064));
		latLngs.add(new LatLng(-27.57444, -48.54056));
		latLngs.add(new LatLng(-27.57421, -48.54014));
		latLngs.add(new LatLng(-27.57414, -48.54001));
		latLngs.add(new LatLng(-27.57414, -48.54001));
		latLngs.add(new LatLng(-27.57396, -48.53985));
		latLngs.add(new LatLng(-27.57385, -48.53969));
		latLngs.add(new LatLng(-27.57372, -48.53947));
		latLngs.add(new LatLng(-27.57356, -48.5392));
		latLngs.add(new LatLng(-27.57348, -48.53905));
		latLngs.add(new LatLng(-27.57347, -48.53903));
		latLngs.add(new LatLng(-27.57346, -48.53902));
		latLngs.add(new LatLng(-27.57344, -48.53902));
		latLngs.add(new LatLng(-27.57343, -48.53902));
		latLngs.add(new LatLng(-27.5734, -48.53903));
		latLngs.add(new LatLng(-27.57335, -48.53906));
		latLngs.add(new LatLng(-27.57335, -48.53906));
		latLngs.add(new LatLng(-27.57333, -48.53902));
		latLngs.add(new LatLng(-27.57314, -48.5387));
		latLngs.add(new LatLng(-27.57247, -48.53749));
		latLngs.add(new LatLng(-27.57238, -48.5373));
		latLngs.add(new LatLng(-27.57234, -48.53721));
		latLngs.add(new LatLng(-27.57221, -48.53695));
		latLngs.add(new LatLng(-27.57211, -48.53671));
		latLngs.add(new LatLng(-27.57204, -48.53654));
		latLngs.add(new LatLng(-27.57198, -48.53636));
		latLngs.add(new LatLng(-27.57194, -48.53621));
		latLngs.add(new LatLng(-27.57189, -48.53602));
		latLngs.add(new LatLng(-27.57185, -48.53585));
		latLngs.add(new LatLng(-27.57182, -48.5357));
		latLngs.add(new LatLng(-27.5718, -48.53553));
		latLngs.add(new LatLng(-27.57178, -48.53537));
		latLngs.add(new LatLng(-27.57176, -48.53519));
		latLngs.add(new LatLng(-27.57175, -48.53504));
		latLngs.add(new LatLng(-27.57175, -48.53487));
		latLngs.add(new LatLng(-27.57174, -48.53468));
		latLngs.add(new LatLng(-27.57175, -48.5345));
		latLngs.add(new LatLng(-27.57178, -48.53414));
		latLngs.add(new LatLng(-27.57181, -48.53396));
		latLngs.add(new LatLng(-27.57183, -48.53381));
		latLngs.add(new LatLng(-27.57186, -48.53362));
		latLngs.add(new LatLng(-27.5719, -48.53345));
		latLngs.add(new LatLng(-27.57201, -48.5331));
		latLngs.add(new LatLng(-27.57207, -48.53291));
		latLngs.add(new LatLng(-27.57207, -48.53289));
		latLngs.add(new LatLng(-27.57212, -48.53272));
		latLngs.add(new LatLng(-27.57216, -48.53259));
		latLngs.add(new LatLng(-27.57249, -48.53161));
		latLngs.add(new LatLng(-27.57271, -48.53091));
		latLngs.add(new LatLng(-27.57284, -48.53053));
		latLngs.add(new LatLng(-27.57297, -48.53009));
		latLngs.add(new LatLng(-27.57329, -48.52914));
		latLngs.add(new LatLng(-27.57336, -48.52894));
		latLngs.add(new LatLng(-27.57344, -48.52868));
		latLngs.add(new LatLng(-27.57355, -48.5284));
		latLngs.add(new LatLng(-27.57363, -48.52819));
		latLngs.add(new LatLng(-27.57374, -48.52795));
		latLngs.add(new LatLng(-27.57383, -48.52777));
		latLngs.add(new LatLng(-27.57393, -48.52759));
		latLngs.add(new LatLng(-27.57403, -48.52743));
		latLngs.add(new LatLng(-27.57412, -48.52728));
		latLngs.add(new LatLng(-27.57424, -48.52711));
		latLngs.add(new LatLng(-27.57438, -48.52691));
		latLngs.add(new LatLng(-27.57452, -48.52674));
		latLngs.add(new LatLng(-27.57467, -48.52657));
		latLngs.add(new LatLng(-27.57485, -48.52639));
		latLngs.add(new LatLng(-27.57499, -48.52625));
		latLngs.add(new LatLng(-27.57514, -48.52612));
		latLngs.add(new LatLng(-27.57527, -48.52601));
		latLngs.add(new LatLng(-27.57548, -48.52586));
		latLngs.add(new LatLng(-27.57568, -48.52571));
		latLngs.add(new LatLng(-27.57588, -48.52559));
		latLngs.add(new LatLng(-27.57614, -48.52543));
		latLngs.add(new LatLng(-27.57646, -48.52526));
		latLngs.add(new LatLng(-27.57669, -48.52512));
		latLngs.add(new LatLng(-27.57669, -48.52512));
		latLngs.add(new LatLng(-27.57675, -48.52513));
		latLngs.add(new LatLng(-27.57679, -48.52513));
		latLngs.add(new LatLng(-27.57681, -48.52512));
		latLngs.add(new LatLng(-27.57685, -48.52512));
		latLngs.add(new LatLng(-27.57689, -48.5251));
		latLngs.add(new LatLng(-27.57694, -48.52509));
		latLngs.add(new LatLng(-27.57702, -48.52506));
		latLngs.add(new LatLng(-27.5771, -48.52503));
		latLngs.add(new LatLng(-27.57721, -48.52499));
		latLngs.add(new LatLng(-27.57734, -48.52495));
		latLngs.add(new LatLng(-27.57744, -48.52492));
		latLngs.add(new LatLng(-27.5775, -48.52491));
		latLngs.add(new LatLng(-27.57757, -48.5249));
		latLngs.add(new LatLng(-27.57761, -48.52489));
		latLngs.add(new LatLng(-27.57765, -48.52489));
		latLngs.add(new LatLng(-27.57771, -48.52488));
		latLngs.add(new LatLng(-27.57775, -48.52488));
		latLngs.add(new LatLng(-27.57779, -48.52489));
		latLngs.add(new LatLng(-27.57783, -48.52489));
		latLngs.add(new LatLng(-27.5779, -48.5249));
		latLngs.add(new LatLng(-27.5779, -48.5249));
		latLngs.add(new LatLng(-27.57793, -48.5249));
		latLngs.add(new LatLng(-27.57797, -48.5249));
		latLngs.add(new LatLng(-27.57801, -48.52489));
		latLngs.add(new LatLng(-27.57805, -48.52489));
		latLngs.add(new LatLng(-27.57809, -48.52489));
		latLngs.add(new LatLng(-27.57813, -48.52489));
		latLngs.add(new LatLng(-27.57817, -48.52489));
		latLngs.add(new LatLng(-27.57822, -48.52489));
		latLngs.add(new LatLng(-27.57826, -48.52489));
		latLngs.add(new LatLng(-27.5783, -48.52489));
		latLngs.add(new LatLng(-27.57834, -48.52489));
		latLngs.add(new LatLng(-27.57838, -48.5249));
		latLngs.add(new LatLng(-27.57842, -48.5249));
		latLngs.add(new LatLng(-27.57847, -48.52491));
		latLngs.add(new LatLng(-27.57851, -48.52491));
		latLngs.add(new LatLng(-27.57855, -48.52492));
		latLngs.add(new LatLng(-27.57859, -48.52493));
		latLngs.add(new LatLng(-27.57865, -48.52494));
		latLngs.add(new LatLng(-27.57867, -48.52494));
		latLngs.add(new LatLng(-27.57869, -48.52494));
		latLngs.add(new LatLng(-27.57871, -48.52494));
		latLngs.add(new LatLng(-27.57873, -48.52495));
		latLngs.add(new LatLng(-27.57875, -48.52495));
		latLngs.add(new LatLng(-27.57877, -48.52495));
		latLngs.add(new LatLng(-27.57881, -48.52494));
		latLngs.add(new LatLng(-27.57883, -48.52494));
		latLngs.add(new LatLng(-27.57885, -48.52494));
		latLngs.add(new LatLng(-27.57886, -48.52493));
		latLngs.add(new LatLng(-27.57888, -48.52493));
		latLngs.add(new LatLng(-27.5789, -48.52492));
		latLngs.add(new LatLng(-27.57892, -48.52491));
		latLngs.add(new LatLng(-27.57894, -48.52491));
		latLngs.add(new LatLng(-27.57896, -48.5249));
		latLngs.add(new LatLng(-27.57899, -48.52488));
		latLngs.add(new LatLng(-27.57902, -48.52486));
		latLngs.add(new LatLng(-27.57904, -48.52484));
		latLngs.add(new LatLng(-27.57907, -48.52482));
		latLngs.add(new LatLng(-27.5791, -48.5248));
		latLngs.add(new LatLng(-27.57912, -48.52478));
		latLngs.add(new LatLng(-27.57914, -48.52476));
		latLngs.add(new LatLng(-27.57917, -48.52473));
		latLngs.add(new LatLng(-27.5792, -48.5247));
		latLngs.add(new LatLng(-27.57921, -48.52468));
		latLngs.add(new LatLng(-27.57924, -48.52465));
		latLngs.add(new LatLng(-27.57925, -48.52462));
		latLngs.add(new LatLng(-27.57927, -48.52459));
		latLngs.add(new LatLng(-27.57928, -48.52457));
		latLngs.add(new LatLng(-27.57929, -48.52456));
		latLngs.add(new LatLng(-27.57931, -48.52452));
		latLngs.add(new LatLng(-27.57932, -48.52449));
		latLngs.add(new LatLng(-27.57932, -48.52448));
		latLngs.add(new LatLng(-27.57933, -48.52446));
		latLngs.add(new LatLng(-27.57934, -48.52443));
		latLngs.add(new LatLng(-27.57935, -48.52439));
		latLngs.add(new LatLng(-27.57936, -48.52436));
		latLngs.add(new LatLng(-27.57937, -48.52432));
		latLngs.add(new LatLng(-27.57937, -48.5243));
		latLngs.add(new LatLng(-27.57938, -48.52427));
		latLngs.add(new LatLng(-27.57938, -48.52423));
		latLngs.add(new LatLng(-27.57938, -48.52421));
		latLngs.add(new LatLng(-27.57938, -48.52418));
		latLngs.add(new LatLng(-27.57938, -48.52414));
		latLngs.add(new LatLng(-27.57938, -48.5241));
		latLngs.add(new LatLng(-27.57937, -48.52406));
		latLngs.add(new LatLng(-27.57937, -48.52401));
		latLngs.add(new LatLng(-27.57936, -48.52399));
		latLngs.add(new LatLng(-27.57936, -48.52397));
		latLngs.add(new LatLng(-27.57935, -48.52393));
		latLngs.add(new LatLng(-27.57935, -48.52392));
		latLngs.add(new LatLng(-27.57934, -48.52388));
		latLngs.add(new LatLng(-27.57933, -48.52385));
		latLngs.add(new LatLng(-27.57932, -48.52383));
		latLngs.add(new LatLng(-27.57931, -48.5238));
		latLngs.add(new LatLng(-27.57927, -48.52371));
		latLngs.add(new LatLng(-27.57927, -48.5237));
		latLngs.add(new LatLng(-27.57926, -48.52368));
		latLngs.add(new LatLng(-27.57925, -48.52366));
		latLngs.add(new LatLng(-27.57917, -48.52346));
		latLngs.add(new LatLng(-27.57916, -48.52345));
		latLngs.add(new LatLng(-27.57911, -48.52333));
		latLngs.add(new LatLng(-27.57907, -48.52321));
		latLngs.add(new LatLng(-27.57903, -48.52312));
		latLngs.add(new LatLng(-27.57897, -48.52298));
		latLngs.add(new LatLng(-27.57893, -48.52287));
		latLngs.add(new LatLng(-27.57888, -48.52273));
		latLngs.add(new LatLng(-27.5788, -48.52251));
		latLngs.add(new LatLng(-27.57861, -48.522));
		latLngs.add(new LatLng(-27.57856, -48.52184));
		latLngs.add(new LatLng(-27.57851, -48.52169));
		latLngs.add(new LatLng(-27.57846, -48.52153));
		latLngs.add(new LatLng(-27.57841, -48.52138));
		latLngs.add(new LatLng(-27.57836, -48.52123));
		latLngs.add(new LatLng(-27.57834, -48.52114));
		latLngs.add(new LatLng(-27.57831, -48.52105));
		latLngs.add(new LatLng(-27.57828, -48.52096));
		latLngs.add(new LatLng(-27.57827, -48.5209));
		latLngs.add(new LatLng(-27.57825, -48.52084));
		latLngs.add(new LatLng(-27.57824, -48.52079));
		latLngs.add(new LatLng(-27.57824, -48.52075));
		latLngs.add(new LatLng(-27.57824, -48.52068));
		latLngs.add(new LatLng(-27.5781, -48.52028));
		latLngs.add(new LatLng(-27.57802, -48.52007));
		latLngs.add(new LatLng(-27.57795, -48.51986));
		latLngs.add(new LatLng(-27.57781, -48.51936));
		latLngs.add(new LatLng(-27.57773, -48.5191));
		latLngs.add(new LatLng(-27.57765, -48.51885));
		latLngs.add(new LatLng(-27.57757, -48.5186));
		latLngs.add(new LatLng(-27.5775, -48.51838));
		latLngs.add(new LatLng(-27.5773, -48.51769));
		latLngs.add(new LatLng(-27.57726, -48.51756));
		latLngs.add(new LatLng(-27.57715, -48.51719));
		latLngs.add(new LatLng(-27.57704, -48.51683));
		latLngs.add(new LatLng(-27.57678, -48.51602));
		latLngs.add(new LatLng(-27.57663, -48.51559));
		latLngs.add(new LatLng(-27.57661, -48.51552));
		latLngs.add(new LatLng(-27.57658, -48.51545));
		latLngs.add(new LatLng(-27.57656, -48.5154));
		latLngs.add(new LatLng(-27.57655, -48.51536));
		latLngs.add(new LatLng(-27.57653, -48.51531));
		latLngs.add(new LatLng(-27.57651, -48.51527));
		latLngs.add(new LatLng(-27.57641, -48.51508));
		latLngs.add(new LatLng(-27.5764, -48.51506));
		latLngs.add(new LatLng(-27.57639, -48.51503));
		latLngs.add(new LatLng(-27.57637, -48.515));
		latLngs.add(new LatLng(-27.57636, -48.51498));
		latLngs.add(new LatLng(-27.57633, -48.51493));
		latLngs.add(new LatLng(-27.57631, -48.5149));
		latLngs.add(new LatLng(-27.57629, -48.51488));
		latLngs.add(new LatLng(-27.57627, -48.51485));
		latLngs.add(new LatLng(-27.57625, -48.51483));
		latLngs.add(new LatLng(-27.57624, -48.51481));
		latLngs.add(new LatLng(-27.57621, -48.51478));
		latLngs.add(new LatLng(-27.5762, -48.51477));
		latLngs.add(new LatLng(-27.57616, -48.51474));
		latLngs.add(new LatLng(-27.57613, -48.51471));
		latLngs.add(new LatLng(-27.5761, -48.51469));
		latLngs.add(new LatLng(-27.57604, -48.51464));
		latLngs.add(new LatLng(-27.57601, -48.51462));
		latLngs.add(new LatLng(-27.57598, -48.5146));
		latLngs.add(new LatLng(-27.57595, -48.51457));
		latLngs.add(new LatLng(-27.57592, -48.51455));
		latLngs.add(new LatLng(-27.57585, -48.51451));
		latLngs.add(new LatLng(-27.57577, -48.51447));
		latLngs.add(new LatLng(-27.57575, -48.51446));
		latLngs.add(new LatLng(-27.57534, -48.51432));
		latLngs.add(new LatLng(-27.57528, -48.5143));
		latLngs.add(new LatLng(-27.57467, -48.51411));
		latLngs.add(new LatLng(-27.57462, -48.5141));
		latLngs.add(new LatLng(-27.57457, -48.51408));
		latLngs.add(new LatLng(-27.57453, -48.51407));
		latLngs.add(new LatLng(-27.57448, -48.51405));
		latLngs.add(new LatLng(-27.57443, -48.51404));
		latLngs.add(new LatLng(-27.57439, -48.51402));
		latLngs.add(new LatLng(-27.5743, -48.51398));
		latLngs.add(new LatLng(-27.57425, -48.51396));
		latLngs.add(new LatLng(-27.5742, -48.51395));
		latLngs.add(new LatLng(-27.57416, -48.51392));
		latLngs.add(new LatLng(-27.57413, -48.51391));
		latLngs.add(new LatLng(-27.57409, -48.51389));
		latLngs.add(new LatLng(-27.57405, -48.51387));
		latLngs.add(new LatLng(-27.57401, -48.51384));
		latLngs.add(new LatLng(-27.57397, -48.51382));
		latLngs.add(new LatLng(-27.57393, -48.5138));
		latLngs.add(new LatLng(-27.5739, -48.51378));
		latLngs.add(new LatLng(-27.57386, -48.51375));
		latLngs.add(new LatLng(-27.57349, -48.5135));
		latLngs.add(new LatLng(-27.5732, -48.51329));
		latLngs.add(new LatLng(-27.57297, -48.51312));
		latLngs.add(new LatLng(-27.57295, -48.51311));
		latLngs.add(new LatLng(-27.57292, -48.5131));
		latLngs.add(new LatLng(-27.57287, -48.51307));
		latLngs.add(new LatLng(-27.57277, -48.51302));
		latLngs.add(new LatLng(-27.57272, -48.51299));
		latLngs.add(new LatLng(-27.57267, -48.51297));
		latLngs.add(new LatLng(-27.57262, -48.51294));
		latLngs.add(new LatLng(-27.57257, -48.51292));
		latLngs.add(new LatLng(-27.57252, -48.5129));
		latLngs.add(new LatLng(-27.57247, -48.51287));
		latLngs.add(new LatLng(-27.57225, -48.51278));
		latLngs.add(new LatLng(-27.57221, -48.51277));
		latLngs.add(new LatLng(-27.57217, -48.51276));
		latLngs.add(new LatLng(-27.57213, -48.51274));
		latLngs.add(new LatLng(-27.57209, -48.51273));
		latLngs.add(new LatLng(-27.57196, -48.5127));
		latLngs.add(new LatLng(-27.57189, -48.51268));
		latLngs.add(new LatLng(-27.57182, -48.51267));
		latLngs.add(new LatLng(-27.57175, -48.51265));
		latLngs.add(new LatLng(-27.57168, -48.51264));
		latLngs.add(new LatLng(-27.57156, -48.51261));
		latLngs.add(new LatLng(-27.57155, -48.51261));
		latLngs.add(new LatLng(-27.57104, -48.51251));
		latLngs.add(new LatLng(-27.57041, -48.51239));
		latLngs.add(new LatLng(-27.5695, -48.5122));
		latLngs.add(new LatLng(-27.56947, -48.51219));
		latLngs.add(new LatLng(-27.56944, -48.51219));
		latLngs.add(new LatLng(-27.56942, -48.51218));
		latLngs.add(new LatLng(-27.56939, -48.51217));
		latLngs.add(new LatLng(-27.56936, -48.51216));
		latLngs.add(new LatLng(-27.56934, -48.51215));
		latLngs.add(new LatLng(-27.56931, -48.51214));
		latLngs.add(new LatLng(-27.56928, -48.51213));
		latLngs.add(new LatLng(-27.56926, -48.51212));
		latLngs.add(new LatLng(-27.56923, -48.51211));
		latLngs.add(new LatLng(-27.56918, -48.51209));
		latLngs.add(new LatLng(-27.56916, -48.51207));
		latLngs.add(new LatLng(-27.56914, -48.51206));
		latLngs.add(new LatLng(-27.56911, -48.51204));
		latLngs.add(new LatLng(-27.56909, -48.51203));
		latLngs.add(new LatLng(-27.56907, -48.51201));
		latLngs.add(new LatLng(-27.56904, -48.512));
		latLngs.add(new LatLng(-27.56902, -48.51198));
		latLngs.add(new LatLng(-27.569, -48.51196));
		latLngs.add(new LatLng(-27.56898, -48.51194));
		latLngs.add(new LatLng(-27.56894, -48.51191));
		latLngs.add(new LatLng(-27.5689, -48.51187));
		latLngs.add(new LatLng(-27.56888, -48.51185));
		latLngs.add(new LatLng(-27.56885, -48.51181));
		latLngs.add(new LatLng(-27.56883, -48.51178));
		latLngs.add(new LatLng(-27.5688, -48.51175));
		latLngs.add(new LatLng(-27.56878, -48.51172));
		latLngs.add(new LatLng(-27.56875, -48.51168));
		latLngs.add(new LatLng(-27.56873, -48.51165));
		latLngs.add(new LatLng(-27.56869, -48.51158));
		latLngs.add(new LatLng(-27.56867, -48.51154));
		latLngs.add(new LatLng(-27.56865, -48.51151));
		latLngs.add(new LatLng(-27.56862, -48.51147));
		latLngs.add(new LatLng(-27.5686, -48.51144));
		latLngs.add(new LatLng(-27.56808, -48.51051));
		latLngs.add(new LatLng(-27.56802, -48.51039));
		latLngs.add(new LatLng(-27.568, -48.51035));
		latLngs.add(new LatLng(-27.56798, -48.51032));
		latLngs.add(new LatLng(-27.56797, -48.51028));
		latLngs.add(new LatLng(-27.56795, -48.51024));
		latLngs.add(new LatLng(-27.56794, -48.5102));
		latLngs.add(new LatLng(-27.56792, -48.51017));
		latLngs.add(new LatLng(-27.56791, -48.51013));
		latLngs.add(new LatLng(-27.56789, -48.51009));
		latLngs.add(new LatLng(-27.56788, -48.51005));
		latLngs.add(new LatLng(-27.56787, -48.51001));
		latLngs.add(new LatLng(-27.56786, -48.50997));
		latLngs.add(new LatLng(-27.56785, -48.50993));
		latLngs.add(new LatLng(-27.56784, -48.50989));
		latLngs.add(new LatLng(-27.56783, -48.50985));
		latLngs.add(new LatLng(-27.56782, -48.50981));
		latLngs.add(new LatLng(-27.56781, -48.50977));
		latLngs.add(new LatLng(-27.56781, -48.50973));
		latLngs.add(new LatLng(-27.5678, -48.50969));
		latLngs.add(new LatLng(-27.56779, -48.50965));
		latLngs.add(new LatLng(-27.56779, -48.50961));
		latLngs.add(new LatLng(-27.56778, -48.50957));
		latLngs.add(new LatLng(-27.56775, -48.50933));
		latLngs.add(new LatLng(-27.56775, -48.50933));
		latLngs.add(new LatLng(-27.56768, -48.50883));
		latLngs.add(new LatLng(-27.56767, -48.50877));
		latLngs.add(new LatLng(-27.56745, -48.50728));
		latLngs.add(new LatLng(-27.56742, -48.50711));
		latLngs.add(new LatLng(-27.56735, -48.50666));
		latLngs.add(new LatLng(-27.56733, -48.50659));
		latLngs.add(new LatLng(-27.56732, -48.50652));
		latLngs.add(new LatLng(-27.5673, -48.50645));
		latLngs.add(new LatLng(-27.56728, -48.50638));
		latLngs.add(new LatLng(-27.56727, -48.50631));
		latLngs.add(new LatLng(-27.56725, -48.50624));
		latLngs.add(new LatLng(-27.56723, -48.50617));
		latLngs.add(new LatLng(-27.56721, -48.5061));
		latLngs.add(new LatLng(-27.5672, -48.50607));
		latLngs.add(new LatLng(-27.56719, -48.50604));
		latLngs.add(new LatLng(-27.56718, -48.50601));
		latLngs.add(new LatLng(-27.56716, -48.50598));
		latLngs.add(new LatLng(-27.56715, -48.50595));
		latLngs.add(new LatLng(-27.56714, -48.50593));
		latLngs.add(new LatLng(-27.56711, -48.50587));
		latLngs.add(new LatLng(-27.56709, -48.50584));
		latLngs.add(new LatLng(-27.56706, -48.50579));
		latLngs.add(new LatLng(-27.56704, -48.50576));
		latLngs.add(new LatLng(-27.56701, -48.50571));
		latLngs.add(new LatLng(-27.56699, -48.50569));
		latLngs.add(new LatLng(-27.56697, -48.50566));
		latLngs.add(new LatLng(-27.56695, -48.50564));
		latLngs.add(new LatLng(-27.56693, -48.50561));
		latLngs.add(new LatLng(-27.56691, -48.50559));
		latLngs.add(new LatLng(-27.56688, -48.50557));
		latLngs.add(new LatLng(-27.56686, -48.50555));
		latLngs.add(new LatLng(-27.56684, -48.50553));
		latLngs.add(new LatLng(-27.5668, -48.50549));
		latLngs.add(new LatLng(-27.56677, -48.50547));
		latLngs.add(new LatLng(-27.56675, -48.50546));
		latLngs.add(new LatLng(-27.56672, -48.50544));
		latLngs.add(new LatLng(-27.5667, -48.50542));
		latLngs.add(new LatLng(-27.56667, -48.50541));
		latLngs.add(new LatLng(-27.56665, -48.50539));
		latLngs.add(new LatLng(-27.56662, -48.50538));
		latLngs.add(new LatLng(-27.5666, -48.50537));
		latLngs.add(new LatLng(-27.56657, -48.50535));
		return latLngs;
	}

	public void onItineraryUpdateFailure(String msg) {
		// TODO Auto-generated method stub

	}

	public void onItineraryUpdateStart() {
		// TODO Auto-generated method stub

	}
}