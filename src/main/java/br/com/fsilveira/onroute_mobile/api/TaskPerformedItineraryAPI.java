package br.com.fsilveira.onroute_mobile.api;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.json.JSONException;

import android.os.AsyncTask;
import br.com.fsilveira.onroute_mobile.listener.PerformedItineraryListener;
import br.com.fsilveira.onroute_mobile.model.PerformedPoint;
import br.com.fsilveira.onroute_mobile.model.Travel;
import br.com.fsilveira.onroute_mobile.parser.PerformedItineraryParser;

public class TaskPerformedItineraryAPI extends AsyncTask<Travel, Void, List<PerformedPoint>> {
	protected PerformedItineraryListener listener;

	public TaskPerformedItineraryAPI(PerformedItineraryListener listener) {
		this.listener = listener;
	}

	protected void dispatchOnStart() {
		listener.onItineraryUpdateStart();
	}

	protected void dispatchOnFailure(String msg) {
		listener.onItineraryUpdateFailure(msg);
	}

	protected void dispatchOnSuccess(List<PerformedPoint> list) {
		listener.onItineraryUpdateSuccess(list);
	}

	@Override
	protected List<PerformedPoint> doInBackground(Travel... travels) {

		if (travels == null)
			return null;

		try {
			return PerformedItineraryParser.parse(constructURL(travels[0].getId()));
		} catch (JSONException e) {
			e.printStackTrace();
			dispatchOnFailure(e.getMessage());
		} catch (ApiException e) {
			dispatchOnFailure(e.getMessage());
		}
		return null;
	}

	protected String constructURL(Integer id) throws ApiException {

		final StringBuffer mBuf = new StringBuffer();
		mBuf.append(ApiUtil.URL + "/api/viagens/" + id + "/itinerario.json");

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
	protected void onPostExecute(List<PerformedPoint> points) {
		if (points == null || points.size() == 0) {
			dispatchOnFailure("Nenhum itinerário encontrado");
		} else {
			dispatchOnSuccess(points);
		}
	}
}
