package com.steto.diaw.network.connector;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;

public class BannerConnector extends HttpsConnector {

	private static final String DNS = "http://thetvdb.com/banners";
	
	
	public BannerConnector() {
		// showing the response from the webserver is not usefull
		mShowResponseServerBody = false;
	}

	@Override
	protected HttpURLConnection createUrlConnection(String url) throws MalformedURLException, IOException {
		return super.createUrlConnection(url.startsWith("/") ? DNS + url : DNS + "/" + url);
	}
}
