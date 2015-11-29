package com.steto.diaw.service;

import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.inject.Inject;
import com.steto.diaw.dao.DatabaseHelper;
import com.steto.diaw.dao.EpisodeDao;
import com.steto.diaw.dao.ShowDao;
import com.steto.diaw.model.Episode;
import com.steto.diaw.model.Results;
import com.steto.diaw.model.Show;
import com.steto.diaw.network.Response;
import com.steto.diaw.network.connector.IHttpsConnector;
import com.steto.diaw.network.connector.ParseConnector;
import com.steto.diaw.parser.ShowParser;
import com.steto.diaw.service.model.AbstractIntentService;
import com.steto.diaw.tools.Tools;
import com.steto.diaw.web.QueryString;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import roboguice.util.Ln;

public class ParseGetEpisodeService extends AbstractIntentService {

	public static final String EXTRA_INPUT_FORCE_UPDATE = "EXTRA_INPUT_FORCE_UPDATE";
	public static final String EXTRA_INPUT_LOGIN = "EXTRA_INPUT_LOGIN";
	public static final String EXTRA_OUTPUT_EPISODE_LIST = "EXTRA_OUTPUT_EPISODE_LIST";
	public static final int RESULT_CODE_IN_PROGRESS = 1;

	private static final int MAX_RETRY_COUNT = 2;
	private static String WS_QUERY_WHERE = "where";

	@Inject
	private DatabaseHelper mDatabaseHelper;
	@Inject
	private SharedPreferences mPreferences;
	private EpisodeDao mEpisodeDao;

	private String mLogin = null;
	private boolean mForceUpdate = false;

	private int mRetryCount = 0;
	private int mEpisodeAlreadyGetCount = 0;

	public ParseGetEpisodeService() {
		super("ParseGetEpisodeService");
	}

	@Override
	protected void processInputExtras(Bundle bundle) {
		super.processInputExtras(bundle);

		mLogin = bundle.getString(EXTRA_INPUT_LOGIN, null);
		mForceUpdate = bundle.getBoolean(EXTRA_INPUT_FORCE_UPDATE, false);
	}

	@Override
	protected void processRequest() {
		try {
			mEpisodeDao = mDatabaseHelper.getDao(Episode.class);
		} catch (SQLException e) {
			mServiceStatusCode = AbstractIntentService.DATABASE_ERROR;
			setServiceResponseCode(ServiceResponseCode.KO);
			Ln.e(e);
			return;
		}
		if (mForceUpdate || isDataExpired()) {
			int episodeToGetCount = 1;
			while (mEpisodeAlreadyGetCount < episodeToGetCount && mRetryCount < MAX_RETRY_COUNT) {
				Response response = null;
				try {
					response = getResponse();
				} catch (IOException e) {
					Ln.e(e.getCause());
					mServiceStatusCode = AbstractIntentService.NETWORK_ERROR;
					setServiceResponseCode(ServiceResponseCode.KO);
					return;
				}

				if (response.getStatusCode() == HttpURLConnection.HTTP_OK) {
					mRetryCount = 0;
					// parsing des données JSON
					InputStream responseStream = response.getBody();
					ShowParser showParser = new ShowParser();
					Results results = showParser.parse(responseStream);

					if (showParser.getStatusCode() != ShowParser.PARSER_OK) {
						mServiceStatusCode = AbstractIntentService.PARSING_ERROR;
						setServiceResponseCode(ServiceResponseCode.KO);
						return;
					}

					List<Episode> episodeList = Arrays.asList(results.results);
					Ln.d("on a parse : " + episodeList.size() + " episodes");
					episodeToGetCount = results.count;
					mEpisodeAlreadyGetCount += episodeList.size();

					// enregistrement en BDD
					try {
						EpisodeDao epDAO = mDatabaseHelper.getDao(Episode.class);
						ShowDao showDAO = mDatabaseHelper.getDao(Show.class);
						List<Episode> episodeListSyncedDatabase = epDAO.createFromWebService(episodeList);
						for (int cpt = 0; cpt < episodeList.size(); cpt++) {
							Episode episodeBase = episodeListSyncedDatabase.get(cpt);
							Episode episodeWeb = episodeList.get(cpt);
							episodeBase.setSeen(true);
							episodeBase.setUpdatedAt(episodeWeb.getUpdatedAt());
							episodeBase.setObjectId(episodeWeb.getObjectId());
							epDAO.update(episodeBase);
							Show currentShow = new Show(episodeBase.getShowName());
							showDAO.createIfNotExists(currentShow);
						}
						getResultReceiver().send(RESULT_CODE_IN_PROGRESS, null);
					} catch (SQLException e) {
						Ln.e(e);
						mServiceStatusCode = AbstractIntentService.DATABASE_ERROR;
						setServiceResponseCode(ServiceResponseCode.KO);
						return;
					}

				} else {
					mRetryCount++;
					Ln.d("response : " + response.getStatusCode());
					mServiceStatusCode = AbstractIntentService.HTTP_ERROR;
				}
			}
			// mise à jour de la date de MAJ
			mPreferences.edit().putLong(Tools.SHARED_PREF_LAST_UPDATE, (new Date()).getTime()).commit();
		} else {
			Ln.d("use cache");
		}
	}

	@Override
	protected void fillBundleResponse(Bundle bundle) {
		List<Episode> episodeList = new ArrayList<Episode>();
		try {
			if (mEpisodeDao == null) {
				mEpisodeDao = mDatabaseHelper.getDao(Episode.class);
			}
			episodeList = mEpisodeDao.queryForAllSeen();
		} catch (SQLException e) {
			mServiceStatusCode = AbstractIntentService.DATABASE_ERROR;
			setServiceResponseCode(ServiceResponseCode.KO);
			Ln.e(e);
		}

		// retour à l'appelant
		bundle.putSerializable(EXTRA_OUTPUT_EPISODE_LIST, (Serializable) episodeList);
	}

	@Override
	protected String getQuery() {
		String query = createQueryString(mLogin, getDateLastUpdate(), mEpisodeAlreadyGetCount);
		return "/1/classes/Show" + query;
	}

	@Override
	protected IHttpsConnector getConnector() {
		return new ParseConnector();
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
}