package com.steto.diaw.network.connector;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.zip.GZIPInputStream;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpStatus;

import roboguice.util.Ln;

import android.text.TextUtils;

import com.steto.diaw.network.Response;

public class HttpsConnector implements IHttpsConnector {

	public enum HttpMethod {
		GET("GET"), POST("POST"), PUT("PUT"), DELETE("DELETE");

		private String name = "";

		HttpMethod(String name) {
			this.name = name;
		}

		public String toString() {
			return name;
		}
	}

	public static final int NO_HTTP_STATUS_CODE = 0;
	private static final int CONNECTION_CONNECT_TIMEOUT = 30000;
	private static final int CONNECTION_READ_TIMEOUT = 15000;

	private static final String GZIP = "gzip";

	@Override
	public Response getData(String url) throws IOException {
		return requestData(url, HttpMethod.GET, null);
	}

	@Override
	public Response postData(String url, String content) throws IOException {
		return requestData(url, HttpMethod.POST, content);
	}

	@Override
	public Response putData(String url, String content) throws IOException {
		return requestData(url, HttpMethod.PUT, content);
	}

	@Override
	public Response deleteData(String url, String content) throws IOException {
		return requestData(url, HttpMethod.DELETE, content);
	}

	protected Response requestData(String url, HttpMethod method, String content) throws IOException {
		Response response = new Response();
		HttpURLConnection urlConnection = null;

		try{
			urlConnection = createUrlConnection(url);
			urlConnection.setConnectTimeout(CONNECTION_CONNECT_TIMEOUT);
			urlConnection.setReadTimeout(CONNECTION_READ_TIMEOUT);
			addHeaders(urlConnection);
			urlConnection.setRequestMethod(method.name);
			
			Ln.d("appel a l'url : \"" + urlConnection.getURL().toString() + "\"");
			
			if (method != HttpMethod.GET && !TextUtils.isEmpty(content)) {
				Ln.i("writing content:" + content);
				urlConnection.setDoOutput(true);
				try {
					IOUtils.write(content, urlConnection.getOutputStream());
				} finally {
					IOUtils.closeQuietly(urlConnection.getOutputStream());
				}
			}

			response.setStatusCode(urlConnection.getResponseCode()); 
			response.setHeaders(urlConnection.getHeaderFields());

			InputStream inputstream;
			if (urlConnection.getResponseCode() < HttpStatus.SC_BAD_REQUEST) {
				inputstream = urlConnection.getInputStream();
			} else {
				inputstream = urlConnection.getErrorStream();
			}

			if(inputstream != null){
				if(GZIP.equals(urlConnection.getContentEncoding())){
					response.setBody(new GZIPInputStream(inputstream));
				}else{
					response.setBody(inputstream);
				}
			}else{
				response.setBody(new ByteArrayInputStream("body is null".getBytes("UTF-8")));
			}

			String bodyRecupere = IOUtils.toString(response.getBody());
			response.setBody(new ByteArrayInputStream(bodyRecupere.getBytes()));

			Ln.d("reponse a l'url = \"" + urlConnection.getURL().toString() + "\" response code " + response.getStatusCode() + " (" + urlConnection.getResponseMessage() + ")");
			Ln.v("body = \"" + bodyRecupere + "\"");

		} catch (IOException e){
			Ln.e(e.getMessage(), e);
			throw e;
		}
		return response;
	}

	protected HttpURLConnection createUrlConnection(String url) throws MalformedURLException, IOException {
		return (HttpURLConnection) new URL(url).openConnection();
	}

	// ------------------------------
	// PROTECTED METHODS
	// ------------------------------
	protected void addHeaders(URLConnection urlConnection) {
		urlConnection.setRequestProperty("Accept-Encoding", GZIP);
		// urlConnection.setRequestProperty("Content-type", "lpc-signature/brut");
		// https://code.google.com/p/google-http-java-client/issues/detail?id=213
		urlConnection.setRequestProperty("Connection", "close");
	}

}