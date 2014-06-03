package br.com.fsilveira.onroute_mobile.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ApiUtil {
	
	public static final String URL = "http://192.168.2.7:3000";

	public static String convertStreamToString(final InputStream input) throws ApiException {
		if (input == null)
			return null;

		final BufferedReader reader = new BufferedReader(new InputStreamReader(input));
		final StringBuilder sBuf = new StringBuilder();

		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sBuf.append(line);
			}
		} catch (IOException e) {
			throw new ApiException(e);
		} finally {
			try {
				input.close();
			} catch (IOException e) {
				throw new ApiException(e);
			}
		}
		return sBuf.toString();
	}
}
