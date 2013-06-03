package com.steto.diaw.web;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

/**
 * Created by Stephane on 02/06/13.
 */
public class SeriesIDConnector extends WebConnector {

    private static final String URL = "series/";
    private static final String DNS = "http://thetvdb.com/api/";
    private static final String APIKEY = "487CA0A45BDF427D";

    @Override
    protected String getURL() {
        return URL;
    }

    @Override
    protected String getDNS() {
        return DNS+APIKEY+"/";
    }

    @Override
    public InputStream getResponseBody() {
        try {
            return new GZIPInputStream(mResponseBody);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
