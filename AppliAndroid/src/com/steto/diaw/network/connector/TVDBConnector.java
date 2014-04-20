package com.steto.diaw.network.connector;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;

public class TVDBConnector extends HttpsConnector {

	private static final String DNS = "http://thetvdb.com/api/";

	@Override
	protected HttpURLConnection createUrlConnection(String url) throws MalformedURLException, IOException {
		return super.createUrlConnection(url.startsWith("/") ? DNS + url : DNS + "/" + url);
	}
}
