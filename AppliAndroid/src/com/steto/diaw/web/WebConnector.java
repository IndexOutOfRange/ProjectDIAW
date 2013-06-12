package com.steto.diaw.web;

import com.steto.diaw.tools.FakeSocketFactory;
import com.steto.diaw.tools.MySSLSocketFactory;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.*;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import java.io.*;
import java.security.KeyStore;

/**
 * Created by Stephane on 02/06/13.
 */
public abstract class WebConnector {
	private HttpClient mHttpClient;
	private int mStatusCode = 0;
	protected InputStream mResponseBody = null;

	public enum HTTPMethod {
		GET, POST, PUT, DELETE
	}

	public WebConnector() {
		initHttpClient();
	}

	protected abstract String getDNS();

	protected abstract String getURL();

	protected void initHttpClient() {
		try {
			// Self signed HTTPS server support
			KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
			trustStore.load(null, null);
			SSLSocketFactory stocketFactory = new MySSLSocketFactory(trustStore);
			stocketFactory.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

			//
			HttpParams params = new BasicHttpParams();
			HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
			HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
			int timeoutConnection = 0;
			HttpConnectionParams.setConnectionTimeout(params, timeoutConnection);
			int timeoutSocket = 0;
			HttpConnectionParams.setSoTimeout(params, timeoutSocket);

			SchemeRegistry registry = new SchemeRegistry();
			registry.register(new Scheme("http", new PlainSocketFactory(), 80));
			registry.register(new Scheme("https", new FakeSocketFactory(), 443));

			ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);


			mHttpClient = new DefaultHttpClient(ccm, params);

		} catch (Exception e) {
			mHttpClient = new DefaultHttpClient();
		}

	}

	public void requestFromNetwork(String query, HTTPMethod verb, String body) {

		HttpResponse response = null;
		HttpRequestBase call = null;
		switch (verb) {
			case GET:
				call = buildGetRequest(query);
				break;
			case PUT:
				call = buildPutRequest(query, body);
				break;
			case POST:
				call = buildPostRequest(query, body);
				break;
			case DELETE:
				call = buildDeleteRequest();
				break;
			default:
				call = buildGetRequest(query);
				break;

		}

		System.out.println("Will call URI " + call.getURI());

		try {
			response = mHttpClient.execute(call);
			setResponseBody(response.getEntity().getContent());
			setStatusCode(response.getStatusLine().getStatusCode());
			System.out.println("http call to " + call.getURI() + " status code : " + mStatusCode + " body response : " + mResponseBody);
		} catch (IOException e) {
			System.out.println("http call to " + call.getURI() + " generating exception : " + e);
		}
	}

	public String streamToString(InputStream in) {
		InputStreamReader is = new InputStreamReader(in);
		StringBuilder sb = new StringBuilder();
		BufferedReader br = new BufferedReader(is);
		String read;
		try {
			read = br.readLine();

			while (read != null) {
				//System.out.println(read);
				sb.append(read);
				read = br.readLine();

			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return sb.toString();
	}

	protected void addHeader(HttpRequestBase request) {
		request.setHeader("Accept-Encoding", "gzip");
	}

	/**
	 * @param query
	 * @return httpResponse
	 */
	private HttpGet buildGetRequest(String query) {

		HttpGet get = new HttpGet(getDNS() + getURL() + query);
		addHeader(get);

		return get;
	}

	private HttpDelete buildDeleteRequest() {
		HttpDelete delete = new HttpDelete(getDNS() + getURL());
		addHeader(delete);

		return delete;
	}

	public HttpPut buildPutRequest(String query, String content) {

		HttpPut put = new HttpPut(getDNS() + getURL() + query);

		StringEntity input = null;
		try {
			input = new StringEntity(content);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		input.setContentType("application/json");

		put.setEntity(input);

		addHeader(put);

		return put;
	}

	public HttpPost buildPostRequest(String query, String content) {
		HttpPost post = new HttpPost(getDNS() + getURL() + query);

		StringEntity input = null;
		try {
			input = new StringEntity(content);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		input.setContentType("application/json");

		post.setEntity(input);

		addHeader(post);

		return post;
	}

	public int getStatusCode() {
		return mStatusCode;
	}

	public void setStatusCode(int mStatusCode) {
		this.mStatusCode = mStatusCode;
	}

	public InputStream getResponseBody() {
		return mResponseBody;
	}

	public void setResponseBody(InputStream mResponseBody) {
		this.mResponseBody = mResponseBody;
	}
}
