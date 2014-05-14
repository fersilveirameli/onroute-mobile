package br.com.fsilveira.onroute_mobile.vehicle;

import java.util.List;

import br.com.fsilveira.onroute_mobile.model.Vehicle;

public interface VehicleListener {
	public void onRoutingFailure(String msg);

	public void onRoutingStart();

	public void onRoutingSuccess(List<Vehicle> vehicles);
}