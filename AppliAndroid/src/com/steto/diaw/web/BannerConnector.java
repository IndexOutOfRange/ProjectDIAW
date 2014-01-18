package com.steto.diaw.web;

/**
 * Created by Stephane on 02/06/13.
 */
public class BannerConnector extends WebConnector {

	private static final String DNS = "http://thetvdb.com/";
	private static final String URL = "banners/";

	@Override
	protected String getDNS() {
		return DNS;
	}

	@Override
	protected String getURL() {
		return URL;
	}
}
