package br.com.fsilveira.onroute_mobile.api;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.json.JSONException;

import android.os.AsyncTask;
import br.com.fsilveira.onroute_mobile.model.Travel;
import br.com.fsilveira.onroute_mobile.model.Vehicle;
import br.com.fsilveira.onroute_mobile.parser.TravelParser;
import br.com.fsilveira.onroute_mobile.travel.TravelListener;

public class TaskTravelAPI extends AsyncTask<Vehicle, Void, List<Travel>> {
	protected TravelListener listener;

	public TaskTravelAPI(TravelListener listener) {
		this.listener = listener;
	}

	protected void dispatchOnStart() {
		listener.onRoutingStart();
	}

	protected void dispatchOnFailure(String msg) {
		listener.onRoutingFailure(msg);
	}

	protected void dispatchOnSuccess(List<Travel> travels) {
		listener.onRoutingSuccess(travels);
	}

	@Override
	protected List<Travel> doInBackground(Vehicle... vehicles) {

		if (vehicles == null)
			return null;

		try {
			return TravelParser.parse(constructURL(vehicles[0].getId()));
		} catch (JSONException e) {
			dispatchOnFailure(e.getMessage());
		} catch (ApiException e) {
			dispatchOnFailure(e.getMessage());
		}
		return null;
	}

	protected String constructURL(Integer id) throws ApiException {

		final StringBuffer mBuf = new StringBuffer();
		mBuf.append("http://onroute.apiary-mock.com/api/veiculos/" + id + "/viagens");

		try {
			URL url = new URL(mBuf.toString());
			InputStream is = url.openConnection().getInputStream();
			return ApiUtil.convertStreamToString(is);
		} catch (MalformedURLException e) {
			throw new ApiException(e);
		} catch (IOException e) {
			throw new ApiException(e);
		}

	}

	@Override
	protected void onPreExecute() {
		dispatchOnStart();
	}

	@Override
	protected void onPostExecute(List<Travel> travels) {
		if (travels == null || travels.size() == 0) {
			dispatchOnFailure("Nenhuma viagem encontrada");
		} else {
			dispatchOnSuccess(travels);
		}
	}
}
