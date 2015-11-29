package com.steto.diaw.network.connector;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;

public class IMDBConnector extends HttpsConnector {

	private static String DNS = "http://imdbapi.org";

	@Override
	protected HttpURLConnection createUrlConnection(String url) throws MalformedURLException, IOException {
		return super.createUrlConnection(url.startsWith("/") ? DNS + url : DNS + "/" + url);
	}
}
