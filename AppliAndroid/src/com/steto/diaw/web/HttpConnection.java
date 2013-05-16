package com.steto.diaw.web;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class HttpConnection {
	
	public static String DNS = "https://api.parse.com";
	
	public HttpConnection() {
		initHTTPSConnection();
	}
	
	private void initHTTPSConnection() {

		try{         
		    //Remplace le verifieur de nom d'hote par un autre moins restrictif
		    HostnameVerifier hostnameVerifier=new HostnameVerifier(){
		        public boolean verify(String urlHostName,SSLSession session){
		            return true;
		        }
		    };
		    HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier);
		 
		    //Remplace le verifieur de certificat par un autre moins restrictif
		    TrustManager[] trustAllCerts=new TrustManager[]{
		        new X509TrustManager(){
		            public java.security.cert.X509Certificate[] getAcceptedIssuers(){return null;}
		            public void checkClientTrusted(java.security.cert.X509Certificate[] certs,String authType){}
		            public void checkServerTrusted(java.security.cert.X509Certificate[] certs,String authType){}
		        }
		    };
		    SSLContext sslContext=SSLContext.getInstance("SSL");
		    sslContext.init(null,trustAllCerts,new java.security.SecureRandom());
		    HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory()); 
		}catch(Exception ex){
		    System.err.println(ex);
		} 
	}
	
	private void addHeader(URLConnection request ) {
		request.setRequestProperty("X-Parse-Application-Id", "NWvYWhOOjIfE3cwQhHGH4Ic6Sdc8FYbTWBKYwPR8");
		request.setRequestProperty("X-Parse-REST-API-Key", "Pq2pfW4DLkU1TZfcotp2igvsAosgNhDN0UMIRV87");
		request.setRequestProperty("Content-Type", "application/json");
	}
	
	public InputStream httpsGet(String path) throws MalformedURLException, IOException{ 
		 
	    //Chargement de la page...
	    URLConnection conn = new URL(DNS + path).openConnection();
	    addHeader(conn);
	    return conn.getInputStream();
		
	}
	public InputStream httpsPost(String path, String content) throws MalformedURLException, IOException { 
		 
	    URL url = new URL(DNS + path);
	    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
	    addHeader(connection);
	    connection.setDoOutput(true); // Pour pouvoir envoyer des donnees
	    connection.setRequestMethod("POST");
	    OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
	    writer.write(content);
	    writer.flush();
	    return connection.getInputStream();
	
	}
}
