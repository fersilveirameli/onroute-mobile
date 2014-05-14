package br.com.fsilveira.onroute_mobile.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Route implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer id;
	private String name;
	private String origin;
	private String destination;
	private List<WayPoint> wayPoints;

	public Route() {
		wayPoints = new ArrayList<WayPoint>();
	}

	public String getOrigin() {
		return origin;
	}

	public void setOrigin(String origin) {
		this.origin = origin;
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public List<WayPoint> getWayPoints() {
		return wayPoints;
	}

	public void setWayPoints(List<WayPoint> wayPoints) {
		this.wayPoints = wayPoints;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
