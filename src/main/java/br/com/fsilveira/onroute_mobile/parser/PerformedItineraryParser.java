package br.com.fsilveira.onroute_mobile.parser;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import br.com.fsilveira.onroute_mobile.model.PerformedPoint;

public class PerformedItineraryParser {

	public static List<PerformedPoint> parse(String result) throws JSONException {
		JSONArray json = new JSONArray(result);

		List<PerformedPoint> points = new ArrayList<PerformedPoint>();

		for (int index = 0; index < json.length(); index++) {
			JSONObject pointJson = json.getJSONObject(index);

			PerformedPoint point = new PerformedPoint();
			point.setDate(pointJson.getString("date"));
			point.setLat(pointJson.getDouble("latitude"));
			point.setLng(pointJson.getDouble("longitude"));
			points.add(point);

		}

		return points;

	}

}
