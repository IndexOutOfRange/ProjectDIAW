package com.steto.diaw.network.connector;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URLConnection;

public class ParseConnector extends HttpsConnector {

	private static String DNS = "https://api.parse.com";

	private static String DIAW_APPKEY = "NWvYWhOOjIfE3cwQhHGH4Ic6Sdc8FYbTWBKYwPR8";
	private static String DIAW_RESTAPIKEY = "Pq2pfW4DLkU1TZfcotp2igvsAosgNhDN0UMIRV87";

	@Override
	protected void addHeaders(URLConnection urlConnection) {
		super.addHeaders(urlConnection);
		urlConnection.addRequestProperty("X-Parse-Application-Id", DIAW_APPKEY);
		urlConnection.addRequestProperty("X-Parse-REST-API-Key", DIAW_RESTAPIKEY);
		urlConnection.addRequestProperty("Content-Type", "application/json");
	}

	@Override
	protected HttpURLConnection createUrlConnection(String url) throws MalformedURLException, IOException {
		return super.createUrlConnection(url.startsWith("/") ? DNS + url : DNS + "/" + url);
	}

}