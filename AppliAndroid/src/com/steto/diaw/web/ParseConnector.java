package com.steto.diaw.web;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.KeyStore;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
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

import com.steto.diaw.tools.FakeSocketFactory;
import com.steto.diaw.tools.MySSLSocketFactory;


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
