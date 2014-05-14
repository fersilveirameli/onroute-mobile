package br.com.fsilveira.onroute_mobile.parser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import br.com.fsilveira.onroute_mobile.model.Route;
import br.com.fsilveira.onroute_mobile.model.WayPoint;

public class RouteParser {

	public static Route parse(JSONObject json) throws JSONException {

		Route route = new Route();
		route.setId(json.getInt("id"));
		route.setName(json.getString("name"));
		route.setOrigin(json.getString("origem"));
		route.setDestination(json.getString("destino"));

		JSONArray wayPointsJson = json.getJSONArray("wayPoints");

		for (int index = 0; index < wayPointsJson.length(); index++) {
			JSONObject wayPointJson = wayPointsJson.getJSONObject(index);

			WayPoint point = new WayPoint();
			point.setPoint(wayPointJson.getString("point"));

			route.getWayPoints().add(point);

		}

		return route;

	}

}
