package br.com.fsilveira.onroute_mobile.api;

public class ApiException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ApiException() {
		// TODO Auto-generated constructor stub
	}

	public ApiException(String detailMessage) {
		super(detailMessage);
		// TODO Auto-generated constructor stub
	}

	public ApiException(Throwable throwable) {
		super(throwable);
		// TODO Auto-generated constructor stub
	}

	public ApiException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
		// TODO Auto-generated constructor stub
	}

}
