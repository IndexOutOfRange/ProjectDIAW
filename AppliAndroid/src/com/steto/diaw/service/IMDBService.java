package com.steto.diaw.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import com.steto.diaw.model.Show;
import com.steto.diaw.parser.AbstractParser;
import com.steto.diaw.parser.IMDBParser;
import com.steto.diaw.web.IMDBConnector;
import com.steto.diaw.web.QueryString;
import com.steto.diaw.web.WebConnector;
import org.apache.http.HttpStatus;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Stephane on 09/06/13.
 */
public class IMDBService extends IntentService {

    public static final int RESULT_CODE_OK = 0;

    private static String IMDB_SERVICE = "IMDB_SERVICE";
    public static String SERVICE_INPUT_TITLE = "SERVICE_INPUT_TITLE";
    public static String SERVICE_INPUT_RECEIVER = "SERVICE_INPUT_RECEIVER";
    public static String SERVICE_OUTPUT_DATA = "SERVICE_OUTPUT_DATA";

    //QUERY CONSTANTS rien d'interessant...
    private static String QUERY_TITLE = "title";
    private static String QUERY_TYPE = "xml";
    private static String QUERY_PLOT = "plot";
    private static String QUERY_EPISODE = "episode";
    private static String QUERY_LIMIT = "limit";
    private static String QUERY_YEAR = "yg";
    private static String QUERY_SHOWTYPE = "mt";
    private static String QUERY_LANG = "lang";
    private static String QUERY_OFFSET = "offset";
    private static String QUERY_AKA = "aka";
    private static String QUERY_RELEASE_DATE = "release";
    private static String QUERY_BUSINESS = "business";
    private static String QUERY_TECH = "tech";
    private static String QUERY_TYPE_VALUE = "json";
    private static String QUERY_PLOT_VALUE = "none";
    private static String QUERY_EPISODE_VALUE = "0";
    private static String QUERY_YEAR_VALUE = "0";
    private static String QUERY_SHOWTYPE_VALUE = "TVS";
    private static String QUERY_LANG_VALUE = "en-US";
    private static String QUERY_AKA_VALUE = "simple";
    private static String QUERY_RELEASE_DATE_VALUE = "simple";
    private static String QUERY_BUSINESS_VALUE = "0";
    private static String QUERY_TECH_VALUE = "0";

    //QUERY VARIABLE
    private static String QUERY_RESULT_PER_PAGE = "10";

    private Integer mSearchOffset = 0;


    //http://imdbapi.org/?title=South&type=xml&plot=none&episode=0&limit=10&yg=0&mt=TVS&lang=en-US&offset=0&aka=simple&release=simple&business=0&tech=0
    public IMDBService() {
        super(IMDB_SERVICE);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        int resultCode = 0;
        List<Show> parsed = new ArrayList<Show>();

        String title = (String) intent.getExtras().get(SERVICE_INPUT_TITLE);
        ResultReceiver caller = (ResultReceiver) intent.getExtras().get(SERVICE_INPUT_RECEIVER);

        QueryString myQuery = new QueryString();
        myQuery.add(QUERY_TITLE, title);
        myQuery.add(QUERY_TYPE, QUERY_TYPE_VALUE);
        myQuery.add(QUERY_PLOT, QUERY_PLOT_VALUE);
        myQuery.add(QUERY_EPISODE, QUERY_EPISODE_VALUE);
        myQuery.add(QUERY_LIMIT, QUERY_RESULT_PER_PAGE);
        myQuery.add(QUERY_YEAR, QUERY_YEAR_VALUE);
        myQuery.add(QUERY_SHOWTYPE, QUERY_SHOWTYPE_VALUE);
        myQuery.add(QUERY_LANG, QUERY_LANG_VALUE);
        myQuery.add(QUERY_OFFSET, mSearchOffset.toString());
        myQuery.add(QUERY_AKA, QUERY_AKA_VALUE);
        myQuery.add(QUERY_RELEASE_DATE, QUERY_RELEASE_DATE_VALUE);
        myQuery.add(QUERY_BUSINESS, QUERY_BUSINESS_VALUE);
        myQuery.add(QUERY_TECH, QUERY_TECH_VALUE);

        IMDBConnector myWeb = new IMDBConnector();
        myWeb.requestFromNetwork(myQuery.toString(), WebConnector.HTTPMethod.GET, null);
        if( myWeb.getStatusCode() == HttpStatus.SC_OK) {

            IMDBParser myParser = new IMDBParser();
            parsed = myParser.parse (myWeb.getResponseBody());
            if( myParser.getStatusCode() != AbstractParser.PARSER_OK) {
                resultCode = myParser.getStatusCode();
            }
        } else {
            resultCode = myWeb.getStatusCode();
        }
        Bundle bund = new Bundle();
        bund.putSerializable(SERVICE_OUTPUT_DATA, (Serializable) parsed);
        caller.send(resultCode, bund);

    }
}
