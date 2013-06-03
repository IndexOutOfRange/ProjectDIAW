package com.steto.diaw.service;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import com.steto.diaw.dao.DatabaseHelper;
import com.steto.diaw.dao.ShowDao;
import com.steto.diaw.model.Show;
import com.steto.diaw.parser.AbstractParser;
import com.steto.diaw.parser.SeriesParser;
import com.steto.diaw.tools.Tools;
import com.steto.diaw.web.QueryString;
import com.steto.diaw.web.SeriesIDConnector;
import com.steto.diaw.web.SeriesNameConnector;
import com.steto.diaw.web.WebConnector;

import org.apache.http.HttpStatus;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by Stephane on 02/06/13.
 */
public class SeriesService extends IntentService {

    private static final String NAME = "SeriesService";
    public static final String INPUT_SERIE = "INPUT_SERIE";
    private static final String SERIE_NAME_QUERY = "seriesname";
    public static final String OUTPUT_DATA = "OUTPUT_DATA";
    public static final String INPUT_RESULTRECEIVER = "INPUT_RESULTRECEIVER";
    public static final int RESULT_CODE_OK = 0;

    public SeriesService() {
        super(NAME);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Show input = (Show) intent.getExtras().get(INPUT_SERIE);
        ResultReceiver receiver = (ResultReceiver) intent.getExtras().get(INPUT_RESULTRECEIVER);
        Integer id = input.getTVDBID();
        int responseCode = RESULT_CODE_OK;
        List<Show> ret = null;
        if( id != 0) {
            //3 TODO DIFFERENTS!!
            //TODO identifier si on a deja fait l'appel a tv db avec l'id et qu'on a donc toutes les infos
            //TODO une fois qu'on a identifier ca il faut voire si on veut rafraichir les données en rapellant tv db
            //TODO si on veut pas rapeller TV DB il faut juste faire l'appel en base
            SeriesIDConnector myWeb = new SeriesIDConnector();
            myWeb.requestFromNetwork(id.toString() + "/en.xml", WebConnector.HTTPMethod.GET, null);

            if( myWeb.getStatusCode() == HttpStatus.SC_OK) {
                SeriesParser myParser = new SeriesParser();
                ret = myParser.parse(myWeb.getResponseBody());
                if( myParser.getStatusCode() == AbstractParser.PARSER_OK) {
                    ret.get(0).setId(input.getId());
                    try {
                        ShowDao myDAO = null;
                        myDAO = DatabaseHelper.getInstance(this).getShowDao();
                        myDAO.createOrUpdate(ret != null ? ret.get(0) : null);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else {
            SeriesNameConnector myWeb = new SeriesNameConnector();
            QueryString myQuery = new QueryString();
            myQuery.add(SERIE_NAME_QUERY, input.getShowName());
            myWeb.requestFromNetwork(myQuery.getQuery(), WebConnector.HTTPMethod.GET,null);
            if( myWeb.getStatusCode() == HttpStatus.SC_OK ) {
                SeriesParser myParser = new SeriesParser();
                ret = myParser.parse(myWeb.getResponseBody());

                if( myParser.getStatusCode() == AbstractParser.PARSER_OK) {
                    ret.get(0).setId(input.getId());
                    try {
                        ShowDao myDAO = null;
                        myDAO = DatabaseHelper.getInstance(this).getShowDao();
                        myDAO.createOrUpdate(ret != null ? ret.get(0) : null);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        //retour à l'appelant
        Bundle retBundle = new Bundle();
        retBundle.putSerializable(OUTPUT_DATA, (Serializable) ret);
        receiver.send(responseCode, retBundle);
    }

    private boolean isDataExpired( ) {
        SharedPreferences settings = getSharedPreferences(Tools.SHARED_PREF_FILE, Activity.MODE_PRIVATE);
        long lastUpdate = settings.getLong(Tools.SHARED_PREF_LAST_UPDATE, 0);
        long now = new Date().getTime();
        long oneDay = TimeUnit.MILLISECONDS.convert(1, TimeUnit.DAYS);

        if (now > lastUpdate + oneDay) {
            Log.d("ShowService", "Update the show from Parse");

            return true;
        } else {
            Log.d("ShowService", "Use database");
            return false;
        }
    }
}
