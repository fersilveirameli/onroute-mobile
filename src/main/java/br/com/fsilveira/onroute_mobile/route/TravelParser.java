package br.com.fsilveira.onroute_mobile.route;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import br.com.fsilveira.onroute_mobile.model.Route;
import br.com.fsilveira.onroute_mobile.model.Travel;
import br.com.fsilveira.onroute_mobile.model.WayPoint;

public class TravelParser {

	private String result;

	public TravelParser(String constructURL) {
		result = constructURL;
	}

	public Travel parse() throws JSONException {
		JSONObject json = new JSONObject(result);

//		json.getJSONObject("date");

		JSONObject routeJson = json.getJSONObject("route");
		JSONArray wayPointsJson = routeJson.getJSONArray("wayPoints");

		Route route = new Route();
		route.setOrigin(routeJson.getString("origin"));
		route.setDestination(routeJson.getString("destination"));

		for (int index = 0; index < wayPointsJson.length(); index++) {
			JSONObject pointJson = wayPointsJson.getJSONObject(index);
			route.getWayPoints().add(new WayPoint(pointJson.getString("point")));
		}

		Travel travel = new Travel();
		// travel.setDate(date);
		travel.setRoute(route);
		return travel;

	}

}
