package br.com.fsilveira.onroute_mobile.parser;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import br.com.fsilveira.onroute_mobile.model.Vehicle;

public class VehicleParser {

	public static List<Vehicle> parse(String result) throws JSONException {
		JSONArray json = new JSONArray(result);

		List<Vehicle> vehicles = new ArrayList<Vehicle>();

		for (int index = 0; index < json.length(); index++) {
			JSONObject vehicleJson = json.getJSONObject(index);

			Vehicle vehicle = new Vehicle();
			vehicle.setId(vehicleJson.getInt("id"));
			vehicle.setMark(vehicleJson.getString("marca"));
			vehicle.setPrefix(vehicleJson.getString("placa"));
			vehicle.setType(vehicleJson.getString("tipo"));
			vehicles.add(vehicle);

		}

		return vehicles;

	}

}
