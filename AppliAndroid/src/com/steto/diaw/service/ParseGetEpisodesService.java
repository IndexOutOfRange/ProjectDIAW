package com.steto.diaw.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpStatus;

import roboguice.service.RoboIntentService;
import roboguice.util.Ln;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.ResultReceiver;

import com.google.inject.Inject;
import com.j256.ormlite.dao.DaoManager;
import com.steto.diaw.dao.DatabaseHelper;
import com.steto.diaw.dao.EpisodeDao;
import com.steto.diaw.dao.ShowDao;
import com.steto.diaw.model.Episode;
import com.steto.diaw.model.Results;
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
    private static final int MAX_RETRY_COUNT = 2;
    public static final int RESULT_CODE_IN_PROGRESS = 1;
    private static String WS_QUERY_WHERE = "where";

	@Inject
	private DatabaseHelper mDatabaseHelper;
	@Inject
	private SharedPreferences mPreferences;
    private int mRetryCount = 0;

    public ParseGetEpisodesService() {
		super("ParseGetEpisodesService");
	}

	public ParseGetEpisodesService(String name) {
		super(name);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		// récupération des input de recherche
		ResultReceiver sender = (ResultReceiver) intent.getExtras().get(INTENT_RESULT_RECEIVER);
		String login = intent.getExtras().getString(INTENT_LOGIN);
		boolean forceUpdate = intent.getExtras().getBoolean(INTENT_FORCE_UPDATE);
		int responseCode = RESULT_CODE_ERROR;
		List<Episode> allEp = new ArrayList<Episode>();

		if (forceUpdate || isDataExpired()) {
            int episodeAlreadyGetCount = 0;
            int episodeToGetCount = 1;
            while( episodeAlreadyGetCount < episodeToGetCount && mRetryCount < MAX_RETRY_COUNT) {
                // recupération des données sur le web
                String date = getDateLastUpdate();
                String query = createQueryString(login, date, episodeAlreadyGetCount);
                ShowConnector myWeb = new ShowConnector();
                myWeb.requestFromNetwork(query, ParseConnector.HTTPMethod.GET, null);
                if (myWeb.getStatusCode() == HttpStatus.SC_OK) {
                    mRetryCount = 0;
                    // parsing des données JSON
                    InputStream response = myWeb.getResponseBody();
                    ShowParser myParser = new ShowParser();
                    Results res = myParser.parse(response);
                    allEp = res == null || res.results == null ? null : Arrays.asList(res.results);

                    Ln.d("on a parse : " + allEp.size() + " episodes");
                    episodeAlreadyGetCount += allEp.size();
                    episodeToGetCount = res.count;
                    responseCode = myParser.getStatusCode();

                    // enregistrement en BDD
                    try {
                        EpisodeDao epDAO = mDatabaseHelper.getDao(Episode.class);
                        ShowDao showDAO = mDatabaseHelper.getDao(Show.class);
                        int nbCreated = epDAO.createOrUpdate(allEp);
                        Ln.d(nbCreated + " episodes créés en base");
                        for (Episode current : allEp) {
                        	current.setSeen(true);
                        	epDAO.update(current);
                            Show currentShow = new Show(current.getShowName());
                            showDAO.createIfNotExists(currentShow);
                        }
                    } catch (SQLException e) {
                        responseCode = DatabaseHelper.ERROR_BDD;
                        Ln.e(e);
                    }

                    sender.send(RESULT_CODE_IN_PROGRESS, null);
                } else {
                    mRetryCount++;
                    try {
                        Ln.d(IOUtils.toString(myWeb.getResponseBody()));
                    } catch (IOException e) {
                        Ln.e(e);
                    }
                    responseCode = myWeb.getStatusCode();
                }
            }
            EpisodeDao epDAO = null;
            try {
                epDAO = mDatabaseHelper.getDao(Episode.class);
                allEp = epDAO.queryForAll();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            // mise à jour de la date de MAJ
            mPreferences.edit().putLong(Tools.SHARED_PREF_LAST_UPDATE, (new Date()).getTime()).commit();
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


		// retour à l'appelant
		Bundle ret = new Bundle();
		ret.putSerializable(RESULT_DATA, (Serializable) allEp);
		sender.send(responseCode, ret);
	}

	private String createQueryString(String mail, String date, Integer resultToSkip) {
		QueryString myQuery = new QueryString();
		myQuery.add("limit", "200");
        myQuery.add("count", "1");
        myQuery.add("skip", resultToSkip.toString());
		myQuery.add(WS_QUERY_WHERE, createWhereClause(mail, date));
		return myQuery.toString();
	}

	private String createWhereClause(String mail, String date) {
		if (date != null) {
			return "{\"login\": \"" + mail + "\", \"updatedAt\":{\"$gte\":{\"__type\":\"Date\",\"iso\":\"" + date + "\"}}}";
		} else {
			return "{\"login\": \"" + mail + "\"}";

		}
	}

	private String getDateLastUpdate() {
		long lastUpdate = mPreferences.getLong(Tools.SHARED_PREF_LAST_UPDATE, 0);
		if (lastUpdate == 0) {
			return null;
		}
		Calendar calendar = Calendar.getInstance(Locale.getDefault());
		calendar.setTimeInMillis(lastUpdate);
		String year = String.valueOf(calendar.get(Calendar.YEAR));
		String month = String.valueOf(calendar.get(Calendar.MONTH) + 1);
		String day = String.valueOf(calendar.get(Calendar.DATE));
		if (month.length() == 1) {
			month = "0" + month;
		}
		if (day.length() == 1) {
			day = "0" + day;
		}
		String date = year + "-" + month + "-" + day + "T00:00:00.000Z";
		return date;
	}

	private boolean isDataExpired() {
		long lastUpdate = mPreferences.getLong(Tools.SHARED_PREF_LAST_UPDATE, 0);
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