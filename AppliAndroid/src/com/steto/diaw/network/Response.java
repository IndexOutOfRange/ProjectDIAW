package com.steto.diaw.network;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

public class Response {

	private int mStatusCode;
	private InputStream mBody;
	private Map<String, List<String>> mHeaders;

	public int getStatusCode() {
		return mStatusCode;
	}

	public void setStatusCode(int statusCode) {
		this.mStatusCode = statusCode;
	}

	public InputStream getBody() {
		return mBody;
	}

	public void setBody(InputStream body) {
		this.mBody = body;
	}
	
	public void setBody(String string){
		this.mBody = new ByteArrayInputStream(string.getBytes());
	}

	public Map<String, List<String>> getHeaders() {
		return mHeaders;
	}

	public void setHeaders(Map<String, List<String>> headers) {
		this.mHeaders = headers;
	}
}
