package br.com.fsilveira.onroute_mobile.model;

import java.io.Serializable;
import java.util.List;

public class Travel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer id;
	private String date;
	private Vehicle vehicle;
	private Route route;
	private List<PerformedPoint> performedPoints;

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public Vehicle getVehicle() {
		return vehicle;
	}

	public void setVehicle(Vehicle vehicle) {
		this.vehicle = vehicle;
	}

	public Route getRoute() {
		return route;
	}

	public void setRoute(Route route) {
		this.route = route;
	}

	public List<PerformedPoint> getPerformedPoints() {
		return performedPoints;
	}

	public void setPerformedPoints(List<PerformedPoint> performedPoints) {
		this.performedPoints = performedPoints;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

}