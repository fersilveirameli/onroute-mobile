package br.com.fsilveira.onroute_mobile.directions;

/**
 * Async Task to access the Google Direction API and return the routing data
 * which is then parsed and converting to a route overlay using some classes created by Hesham Saeed.
 * @author Joel Dean
 * Requires an instance of the map activity and the application's current context for the progress dialog.
 * 
 */

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import android.os.AsyncTask;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

public class Routing<T> extends AsyncTask<T, Void, Route> {
	protected ArrayList<RoutingListener> _aListeners;
	protected TravelMode _mTravelMode;

	public Routing(TravelMode mTravelMode, RoutingListener listener) {
		this._aListeners = new ArrayList<RoutingListener>();
		_aListeners.add(listener);
		this._mTravelMode = mTravelMode;
	}

	protected void dispatchOnStart() {
		for (RoutingListener mListener : _aListeners) {
			mListener.onRoutingStart();
		}
	}

	protected void dispatchOnFailure(DirectionsException e) {
		for (RoutingListener mListener : _aListeners) {
			mListener.onRoutingFailure(e);
		}
	}

	protected void dispatchOnSuccess(PolylineOptions mOptions, Route route) {
		for (RoutingListener mListener : _aListeners) {
			mListener.onRoutingSuccess(mOptions, route);
		}
	}

	/**
	 * Performs the call to the google maps API to acquire routing data and deserializes it to a format the map can display.
	 * 
	 * @param aPoints
	 * @return
	 */
	@Override
	protected Route doInBackground(T... aPoints) {
		for (T mPoint : aPoints) {
			if (mPoint == null)
				return null;
		}

		try {
			return new GoogleParser(constructURL(aPoints)).parse();
		} catch (DirectionsException e) {
			dispatchOnFailure(e);
		} catch (UnsupportedEncodingException e) {
			dispatchOnFailure(new DirectionsException(e));
		}
		return null;
	}

	protected String constructURL(T... points) throws UnsupportedEncodingException {
		T start = points[0];
		T dest = points[points.length - 1];

		StringBuilder wayPoints = new StringBuilder();
		for (int index = 0; index < points.length; index++) {
			if (index != 0 && index != (points.length - 1)) {
				wayPoints.append((wayPoints.length() > 0) ? "|" : "");
				wayPoints.append(getPoint(points[index]));
			}
		}

		String sJsonURL = "http://maps.googleapis.com/maps/api/directions/json?";

		final StringBuffer mBuf = new StringBuffer(sJsonURL);
		mBuf.append("origin=");
		mBuf.append(URLEncoder.encode(getPoint(start), "UTF-8"));
		mBuf.append("&destination=");
		mBuf.append(URLEncoder.encode(getPoint(dest), "UTF-8"));
		mBuf.append((wayPoints.length() > 0) ? "&waypoints=" + URLEncoder.encode(wayPoints.toString(), "UTF-8") : "");
		mBuf.append("&sensor=true&mode=");
		mBuf.append(_mTravelMode.getValue());

		return mBuf.toString();
	}

	private String getPoint(T pointt) {
		StringBuilder str = new StringBuilder();
		if (pointt instanceof LatLng) {
			LatLng point = (LatLng) pointt;
			str.append(point.latitude);
			str.append(',');
			str.append(point.longitude);
		} else {
			str.append(pointt);
		}
		return str.toString();
	}

	@Override
	protected void onPreExecute() {
		dispatchOnStart();
	}

	@Override
	protected void onPostExecute(Route route) {
		if (route == null) {
			dispatchOnFailure(null);
		} else {
			PolylineOptions mOptions = new PolylineOptions();

			for (LatLng point : route.getPoints()) {
				 System.out.println(point);
				mOptions.add(point);
			}

			dispatchOnSuccess(mOptions, route);
		}
	}// end onPostExecute method
}
