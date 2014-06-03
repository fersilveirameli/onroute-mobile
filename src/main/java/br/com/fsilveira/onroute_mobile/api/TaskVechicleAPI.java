package br.com.fsilveira.onroute_mobile.api;

/**
 * Async Task to access the Google Direction API and return the routing data
 * which is then parsed and converting to a route overlay using some classes created by Hesham Saeed.
 * @author Joel Dean
 * Requires an instance of the map activity and the application's current context for the progress dialog.
 * 
 */

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.json.JSONException;

import android.os.AsyncTask;
import br.com.fsilveira.onroute_mobile.listener.VehicleListener;
import br.com.fsilveira.onroute_mobile.model.Vehicle;
import br.com.fsilveira.onroute_mobile.parser.VehicleParser;

public class TaskVechicleAPI extends AsyncTask<String, Void, List<Vehicle>> {
	protected VehicleListener listener;

	public TaskVechicleAPI(VehicleListener listener) {
		this.listener = listener;
	}

	protected void dispatchOnStart() {
		listener.onRoutingStart();
	}

	protected void dispatchOnFailure(String msg) {
		listener.onRoutingFailure(msg);
	}

	protected void dispatchOnSuccess(List<Vehicle> vehicles) {
		listener.onRoutingSuccess(vehicles);
	}

	@Override
	protected List<Vehicle> doInBackground(String... aPoints) {

		try {
			return VehicleParser.parse(constructURL());
		} catch (JSONException e) {
			dispatchOnFailure(e.getMessage());
		} catch (ApiException e) {
			dispatchOnFailure(e.getMessage());
		}
		return null;
	}

	protected String constructURL() throws ApiException {

		final StringBuffer mBuf = new StringBuffer();
		mBuf.append(ApiUtil.URL+"/veiculos.json");

		try {
			URL url = new URL(mBuf.toString());
			InputStream is = url.openConnection().getInputStream();
			return ApiUtil.convertStreamToString(is);
		} catch (MalformedURLException e) {
			e.printStackTrace();
			throw new ApiException(e);
		} catch (IOException e) {
			e.printStackTrace();
			throw new ApiException(e);
		}

	}

	@Override
	protected void onPreExecute() {
		dispatchOnStart();
	}

	@Override
	protected void onPostExecute(List<Vehicle> vehicles) {
		if (vehicles == null || vehicles.size() == 0) {
			dispatchOnFailure("Nenhum veiculo encontrado");
		} else {
			dispatchOnSuccess(vehicles);
		}
	}
}
