package br.com.fsilveira.onroute_mobile.model;

import java.io.Serializable;

public class WayPoint implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String point;
	
	public WayPoint( ) {
	}

	public WayPoint(String point) {
		this.point = point;
	}

	public String getPoint() {
		return point;
	}

	public void setPoint(String point) {
		this.point = point;
	}

}
