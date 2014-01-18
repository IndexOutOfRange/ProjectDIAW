package com.steto.diaw.web;

import org.apache.http.client.methods.HttpRequestBase;

public abstract class ParseConnector extends WebConnector {

	private static String DNS = "https://api.parse.com";
	private static String DIAW_APPKEY = "NWvYWhOOjIfE3cwQhHGH4Ic6Sdc8FYbTWBKYwPR8";
	private static String DIAW_RESTAPIKEY = "Pq2pfW4DLkU1TZfcotp2igvsAosgNhDN0UMIRV87";

	@Override
	protected String getDNS() {
		return DNS;
	}

	@Override
	protected void addHeader(HttpRequestBase request) {
		super.addHeader(request);
		request.setHeader("X-Parse-Application-Id", DIAW_APPKEY);
		request.setHeader("X-Parse-REST-API-Key", DIAW_RESTAPIKEY);
		request.setHeader("Content-Type", "application/json");
	}
}
