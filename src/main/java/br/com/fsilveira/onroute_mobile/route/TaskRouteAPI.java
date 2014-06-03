package br.com.fsilveira.onroute_mobile.route;

/**
 * Async Task to access the Google Direction API and return the routing data
 * which is then parsed and converting to a route overlay using some classes created by Hesham Saeed.
 * @author Joel Dean
 * Requires an instance of the map activity and the application's current context for the progress dialog.
 * 
 */

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;

import android.os.AsyncTask;
import br.com.fsilveira.onroute_mobile.directions.RoutingAPI;
import br.com.fsilveira.onroute_mobile.directions.RoutingListener;
import br.com.fsilveira.onroute_mobile.directions.TravelMode;
import br.com.fsilveira.onroute_mobile.model.Travel;
import br.com.fsilveira.onroute_mobile.model.WayPoint;

//public class TaskRouteAPI<T> extends AsyncTask<T, Void, Travel> {
//	protected ArrayList<RoutingListener> _aListeners;
//	protected TravelMode _mTravelMode;
//	private Routing<String> routing;
//
//	public TaskRouteAPI(TravelMode mTravelMode) {
//		this._aListeners = new ArrayList<RoutingListener>();
//		this._mTravelMode = mTravelMode;
//		routing = new Routing<String>(mTravelMode);
//	}
//
//	public void registerListener(RoutingListener mListener) {
//		_aListeners.add(mListener);
//		routing.registerListener(mListener);
//	}
//
//	protected void dispatchOnStart() {
//		for (RoutingListener mListener : _aListeners) {
//			mListener.onRoutingStart();
//		}
//	}
//
//	protected void dispatchOnFailure() {
//		for (RoutingListener mListener : _aListeners) {
//			mListener.onRoutingFailure();
//		}
//	}
//
//	/**
//	 * Performs the call to the google maps API to acquire routing data and deserializes it to a format the map can display.
//	 * 
//	 * @param aPoints
//	 * @return
//	 */
//	@Override
//	protected Travel doInBackground(T... aPoints) {
//		for (T mPoint : aPoints) {
//			if (mPoint == null)
//				return null;
//		}
//
//		try {
//			return new TravelParser(constructURL()).parse();
//		} catch (JSONException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return null;
//	}
//
//	
//
//	@Override
//	protected void onPreExecute() {
//		dispatchOnStart();
//	}
//
//	@Override
//	protected void onPostExecute(Travel result) {
//		if (result == null) {
//			dispatchOnFailure();
//		} else {
//
//			List<String> pointsStr = new ArrayList<String>();
//			pointsStr.add(result.getRoute().getOrigin());
//			for (WayPoint wayPoint : result.getRoute().getWayPoints()) {
//				pointsStr.add(wayPoint.getPoint());
//			}
//			pointsStr.add(result.getRoute().getDestination());
//
//			String[] paintsArray = new String[pointsStr.size()];
//			routing.execute(pointsStr.toArray(paintsArray));
//		}
//	}
//}
