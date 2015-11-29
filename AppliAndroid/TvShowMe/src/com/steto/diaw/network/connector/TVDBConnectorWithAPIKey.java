package com.steto.diaw.network.connector;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;

public class TVDBConnectorWithAPIKey extends HttpsConnector {

	private static final String DNS = "http://thetvdb.com/api/";
	private static final String APIKEY = "487CA0A45BDF427D";

	@Override
	protected HttpURLConnection createUrlConnection(String url) throws MalformedURLException, IOException {
		return super.createUrlConnection(url.startsWith("/") ? DNS + APIKEY + url : DNS + APIKEY + "/" + url);
	}
}
