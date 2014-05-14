package br.com.fsilveira.onroute_mobile.travel;

import java.util.List;

import br.com.fsilveira.onroute_mobile.model.Travel;

public interface TravelListener {
	public void onRoutingFailure(String msg);

	public void onRoutingStart();

	public void onRoutingSuccess(List<Travel> travels);
}