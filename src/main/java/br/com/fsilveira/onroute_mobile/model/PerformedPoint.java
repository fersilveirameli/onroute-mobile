package br.com.fsilveira.onroute_mobile.model;

import java.io.Serializable;
import java.util.Date;

public class PerformedPoint implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Date date;
	private double lat;
	private double lng;

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLng() {
		return lng;
	}

	public void setLng(double lng) {
		this.lng = lng;
	}

}
