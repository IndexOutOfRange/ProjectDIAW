package com.steto.diaw.web;

public class ShowConnector extends ParseConnector {

	private static String URL = "/1/classes/Show";
	private String objectId = "";

	public  ShowConnector() {
		super();
	}

	public ShowConnector(String objectId) {
		super();
		if(!"".equals(objectId)) {
			this.objectId = "/" + objectId;
		}
	}
	
	@Override
	protected String getURL() {
		return URL + objectId;
	}

}
