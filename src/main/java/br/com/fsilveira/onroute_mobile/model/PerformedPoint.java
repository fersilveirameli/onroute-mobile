package br.com.fsilveira.onroute_mobile.model;

import java.io.Serializable;

import com.google.android.gms.maps.model.LatLng;

public class PerformedPoint implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String date;
	private double lat;
	private double lng;

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
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

	public LatLng getLatLng() {
		if (lat != 0 && lng != 0) {
			return new LatLng(lat, lng);
		}
		return null;
	}

}
