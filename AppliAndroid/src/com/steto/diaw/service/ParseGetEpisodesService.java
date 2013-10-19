package com.steto.diaw.service;

import java.io.InputStream;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpStatus;

import roboguice.service.RoboIntentService;
import roboguice.util.Ln;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import com.google.inject.Inject;
import com.j256.ormlite.dao.DaoManager;
import com.steto.diaw.dao.DatabaseHelper;
import com.steto.diaw.dao.EpisodeDao;
import com.steto.diaw.dao.ShowDao;
import com.steto.diaw.model.Episode;
import com.steto.diaw.model.Show;
import com.steto.diaw.parser.ShowParser;
import com.steto.diaw.tools.Tools;
import com.steto.diaw.web.ParseConnector;
import com.steto.diaw.web.QueryString;
import com.steto.diaw.web.ShowConnector;

public class ParseGetEpisodesService extends RoboIntentService {

	public static final String INTENT_RESULT_RECEIVER = "INTENT_RESULT_RECEIVER";
	public static final String INTENT_FORCE_UPDATE = "INTENT_FORCE_UPDATE";
	public static final String INTENT_LOGIN = "INTENT_LOGIN";
	public static final int RESULT_CODE_OK = 0;
	public static final int RESULT_CODE_ERROR = -1;
	public static final String RESULT_DATA = "RESULT_DATA";
	private static String WS_QUERY_WHERE = "where";
	
	@Inject
	private DatabaseHelper mDatabaseHelper;
	
	public ParseGetEpisodesService() {
		super("ParseGetEpisodesService");
	}

	public ParseGetEpisodesService(String name) {
		super(name);
	}

	public String createQueryString(String mail) {
		QueryString myQuery = new QueryString();
		myQuery.add("limit", "1000");
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
		boolean forceUpdate = intent.getExtras().getBoolean(INTENT_FORCE_UPDATE);
		int responseCode = RESULT_CODE_ERROR;
		List<Episode> allEp = new ArrayList<Episode>();

		if (forceUpdate || isDataExpired()) {

			//recupération des données sur le web
			String query = createQueryString(login);
			ShowConnector myWeb = new ShowConnector();
			myWeb.requestFromNetwork(query, ParseConnector.HTTPMethod.GET, null);
			if (myWeb.getStatusCode() == HttpStatus.SC_OK) {

				//parsing des données JSON
				InputStream response = myWeb.getResponseBody();
				ShowParser myParser = new ShowParser();
				allEp = myParser.parse(response);
				Ln.d("on a parse : " + allEp.size() + " episodes");
				responseCode = myParser.getStatusCode();

				//enregistrement en BDD
				try {
					EpisodeDao epDAO = mDatabaseHelper.getDao(Episode.class);
					ShowDao showDAO = mDatabaseHelper.getDao(Show.class);
					int nbCreated = epDAO.createOrUpdate(allEp);
					Log.d("ShowService", nbCreated + " episodes créés en base");
					for (Episode current : allEp) {
						Show currentShow = new Show(current.getShowName());
						showDAO.createIfNotExists(currentShow);
					}

					allEp = epDAO.queryForAll();

					//mise à jour de la date de MAJ
					SharedPreferences settings = getSharedPreferences(Tools.SHARED_PREF_FILE, Activity.MODE_PRIVATE);
					SharedPreferences.Editor editor = settings.edit();
					editor.putLong(Tools.SHARED_PREF_LAST_UPDATE, (new Date()).getTime());
					editor.commit();
				} catch (SQLException e) {
					responseCode = DatabaseHelper.ERROR_BDD;
					Ln.e(e);
				}
			} else {
				responseCode = myWeb.getStatusCode();
			}
		} else {
			try {
				EpisodeDao episodeDao = DaoManager.createDao(mDatabaseHelper.getConnectionSource(), Episode.class);
				allEp = episodeDao.queryForAll();
				responseCode = RESULT_CODE_OK;
			} catch (SQLException e) {
				responseCode = DatabaseHelper.ERROR_BDD;
				Ln.e(e);
			}
		}

		//retour à l'appelant
		Bundle ret = new Bundle();
		ret.putSerializable(RESULT_DATA, (Serializable) allEp);
		sender.send(responseCode, ret);
	}

	private boolean isDataExpired() {
		SharedPreferences settings = getSharedPreferences(Tools.SHARED_PREF_FILE, Activity.MODE_PRIVATE);
		long lastUpdate = settings.getLong(Tools.SHARED_PREF_LAST_UPDATE, 0);
		long now = new Date().getTime();
		long oneDay = TimeUnit.MILLISECONDS.convert(1, TimeUnit.DAYS);

		if (now > lastUpdate + oneDay) {
			Ln.d("Update the show from Parse");

			return true;
		} else {
			Ln.d("Use database");
			return false;
		}
	}


}
