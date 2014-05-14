package br.com.fsilveira.onroute_mobile.directions;

public enum TravelMode {
	BIKING("biking"), DRIVING("driving"), WALKING("walking"), TRANSIT("transit");

	protected String _sValue;

	private TravelMode(String sValue) {
		this._sValue = sValue;
	}

	protected String getValue() {
		return _sValue;
	}
}