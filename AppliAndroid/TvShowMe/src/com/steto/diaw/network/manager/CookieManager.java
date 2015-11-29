package com.steto.diaw.network.manager;

public final class CookieManager {

	private static CookieManager sCookieManager = null;

	public static CookieManager getInstance() {
		if (sCookieManager == null) {
			sCookieManager = new CookieManager();
		}
		return sCookieManager;
	}

	private CookieManager() {
		super();
	}

	private String mCookie;

	public String getCookie() {
		return mCookie;
	}

	public void setCookie(String mCookie) {
		this.mCookie = mCookie;
	}
}
