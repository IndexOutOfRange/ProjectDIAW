package com.steto.diaw.web;

/**
 * Created by Stephane on 09/06/13.
 */
public class IMDBConnector extends WebConnector {

    private static String IMDB_DNS = "http://imdbapi.org/";
    private static String IMDB_URL = "";

    @Override
    protected String getDNS() {
        return IMDB_DNS;
    }

    @Override
    protected String getURL() {
        return IMDB_URL;
    }
}
