package br.com.fsilveira.onroute_mobile.parser;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import br.com.fsilveira.onroute_mobile.model.Travel;

public class TravelParser {

	public static List<Travel> parse(String result) throws JSONException {
		JSONArray json = new JSONArray(result);

		List<Travel> travels = new ArrayList<Travel>();

		for (int index = 0; index < json.length(); index++) {
			JSONObject travelJson = json.getJSONObject(index);

			Travel travel = new Travel();
			travel.setId(travelJson.getInt("id"));
			travel.setDate(travelJson.getString("date"));
			travel.setRoute(RouteParser.parse(travelJson.getJSONObject("rota")));
			travels.add(travel);

		}

		return travels;

	}

}
