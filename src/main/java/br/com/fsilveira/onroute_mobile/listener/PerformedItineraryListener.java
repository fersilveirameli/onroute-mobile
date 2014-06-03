package br.com.fsilveira.onroute_mobile.listener;

import java.util.List;

import br.com.fsilveira.onroute_mobile.model.PerformedPoint;

public interface PerformedItineraryListener {
	public void onItineraryUpdateFailure(String msg);

	public void onItineraryUpdateStart();

	public void onItineraryUpdateSuccess(List<PerformedPoint> list);
}