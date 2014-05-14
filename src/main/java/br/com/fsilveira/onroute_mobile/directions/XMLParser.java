package br.com.fsilveira.onroute_mobile.directions;

//by Haseem Saheed
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class XMLParser {
	// names of the XML tags
	protected static final String MARKERS = "markers";
	protected static final String MARKER = "marker";

	protected URL feedUrl;

	protected XMLParser(final String feedUrl) throws DirectionsException {
		try {
			this.feedUrl = new URL(feedUrl);
		} catch (MalformedURLException e) {
			throw new DirectionsException(e);
		}
	}

	protected InputStream getInputStream() throws DirectionsException {
		try {
			return feedUrl.openConnection().getInputStream();
		} catch (IOException e) {
			throw new DirectionsException(e);
		}
	}
}