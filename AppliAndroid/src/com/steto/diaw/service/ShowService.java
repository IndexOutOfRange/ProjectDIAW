package com.steto.diaw.service;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import com.steto.diaw.dao.EpisodeDao;
import com.steto.diaw.dao.ShowDao;
import com.steto.diaw.model.Episode;
import com.steto.diaw.parser.ShowParser;
import com.steto.diaw.dao.DatabaseHelper;
import com.steto.diaw.web.QueryString;
import com.steto.diaw.tools.Tools;
import com.steto.diaw.web.ParseConnector;
import com.steto.diaw.web.ShowConnector;
import org.apache.http.HttpStatus;

import java.io.InputStream;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ShowService extends IntentService {

	public static final String INTENT_RESULT_RECEIVER = "INTENT_RESULT_RECEIVER";
	public static final String INTENT_LOGIN = "INTENT_LOGIN";
	public static final String URL_SHOW = "/1/classes/Show";
	public static final int RESULT_CODE_OK = 0;
	public static final String RESULT_DATA = "RESULT_DATA";
    private static String WS_QUERY_WHERE = "where";

	public ShowService() {
		super("ShowService");
	}

	public ShowService(String name) {
		super(name);
	}

	public String createQueryString(String mail) {
		QueryString myQuery = new QueryString();
		myQuery.add("limit", "500");
		myQuery.add(WS_QUERY_WHERE, createWhereClause(mail));
		return myQuery.toString();
	}

	public String createWhereClause(String mail) {
		return "{\"login\": \"" + mail + "\" }";
	}

	@Override
	protected void onHandleIntent(Intent intent) {
        //récupération des input de recherche
		ResultReceiver sender = (ResultReceiver) intent.getExtras().get(INTENT_RESULT_RECEIVER);
		String login = intent.getExtras().getString(INTENT_LOGIN);
		int responseCode = RESULT_CODE_OK;
        List<Episode> allEp = null;

        if( isDataExpired() ) {

            //recupération des données sur le web
            String query = createQueryString(login);
            ShowConnector myWeb = new ShowConnector();
            myWeb.requestFromNetwork(query, ParseConnector.HTTPMethod.GET, null);
            if (myWeb.getStatusCode() == HttpStatus.SC_OK) {

                //parsing des données JSON
                InputStream response = myWeb.getResponseBody();
                ShowParser myParser = new ShowParser();
                allEp = myParser.parse(response);
                responseCode = myParser.getStatusCode();

                //enregistrement en BDD
                try {
                    EpisodeDao epDAO = DatabaseHelper.getInstance(this).getEpisodeDao();
                    ShowDao showDAO = DatabaseHelper.getInstance(this).getShowDao();
                    epDAO.createOrUpdate(allEp);
                    for(Episode current : allEp) {
                        showDAO.createIfNotExists(epDAO.getShowFromEpisode(current));
                    }

                    //mise à jour de la date de MAJ
                    SharedPreferences settings = getSharedPreferences(Tools.SHARED_PREF_FILE, Activity.MODE_PRIVATE);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putLong(Tools.SHARED_PREF_LAST_UPDATE, (new Date()).getTime());
                    editor.commit();
                } catch (SQLException e) {
                    responseCode = DatabaseHelper.ERROR_BDD;
                    e.printStackTrace();
                }
            } else {
                responseCode = myWeb.getStatusCode();
            }
        } else {
            try {
                allEp = DatabaseHelper.getInstance(this).getEpisodeDao().queryForAll();
            } catch (SQLException e) {
                responseCode = DatabaseHelper.ERROR_BDD;
                e.printStackTrace();
            }
        }


        //retour à l'appelant
		Bundle ret = new Bundle();
		ret.putSerializable(RESULT_DATA, (Serializable) allEp);
		sender.send(responseCode, ret);
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
