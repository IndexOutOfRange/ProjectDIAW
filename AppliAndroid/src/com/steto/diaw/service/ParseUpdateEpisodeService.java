package com.steto.diaw.service;

import java.io.IOException;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpStatus;

import roboguice.util.Ln;
import android.os.Bundle;

import com.google.inject.Inject;
import com.steto.diaw.dao.DatabaseHelper;
import com.steto.diaw.dao.EpisodeDao;
import com.steto.diaw.model.Episode;
import com.steto.diaw.network.Response;
import com.steto.diaw.network.connector.IHttpsConnector;
import com.steto.diaw.network.connector.ParseConnector;
import com.steto.diaw.service.model.AbstractIntentService;

public class ParseUpdateEpisodeService extends AbstractIntentService {

	public static final String EXTRA_INPUT_EPISODES_TO_UPDATE = "EXTRA_INPUT_EPISODES_TO_UPDATE";
	public static final String EXTRA_INPUT_KEY = "EXTRA_INPUT_KEY";
	public static final String EXTRA_INPUT_VALUE = "EXTRA_INPUT_VALUE";
	public static final String EXTRA_OUTPUT_RESULT_DATA = "EXTRA_OUTPUT_RESULT_DATA";

	@Inject
	private DatabaseHelper mDatabaseHelper;
	private EpisodeDao mEpisodeDao;

	private List<Episode> mEpisodeToUpdateList;
	private int mIndex = 0;
	private String mNewValue;
	private String mKeyOfNewValue;

	public ParseUpdateEpisodeService() {
		super("ParseUpdateEpisodeService");
	}

	public ParseUpdateEpisodeService(String name) {
		super(name);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void processInputExtras(Bundle bundle) {
		super.processInputExtras(bundle);
		mEpisodeToUpdateList = (List<Episode>) bundle.getSerializable(EXTRA_INPUT_EPISODES_TO_UPDATE);
		mKeyOfNewValue = bundle.getString(EXTRA_INPUT_KEY);
		mNewValue = bundle.getString(EXTRA_INPUT_VALUE);
	}

	public String createUpdateBody(String key, String value) {
		return "{\"" + key + "\": \"" + value + "\" }";
	}

	@Override
	protected void processRequest() {
		String body = createUpdateBody(mKeyOfNewValue, mNewValue);

		for (mIndex = 0; mIndex < mEpisodeToUpdateList.size(); mIndex++) {
			Episode episodeToUpdate = mEpisodeToUpdateList.get(mIndex);
			Response response = null;
			try {
				response = putResponse(body);
				if (response.getStatusCode() == HttpStatus.SC_OK) {
					// la maj ne donne que la updateDate, on refait donc un appel au serveur pour avoir exactement les mêmes données
					response = getResponse();
					if (response.getStatusCode() == HttpStatus.SC_OK) {
						// pas de parsing des données JSON car le JSON donne que la updateDate
						try {
							EpisodeDao episodeDao = mDatabaseHelper.getDao(Episode.class);
							episodeDao.updateEpisode(episodeToUpdate, mKeyOfNewValue, mNewValue);
						} catch (SQLException e) {
							Ln.e(e.getCause());
							mServiceStatusCode = AbstractIntentService.DATABASE_ERROR;
							setServiceResponseCode(ServiceResponseCode.KO);
							return;
						}
					}
				} else {
					mServiceStatusCode = AbstractIntentService.HTTP_ERROR;
					setServiceResponseCode(ServiceResponseCode.KO);
					return;
				}
			} catch (IOException e) {
				Ln.e(e.getCause());
				mServiceStatusCode = AbstractIntentService.NETWORK_ERROR;
				setServiceResponseCode(ServiceResponseCode.KO);
				return;
			}
		}
	}

	@Override
	protected void fillBundleResponse(Bundle bundle) {
		List<Episode> allEp = new ArrayList<Episode>();
		try {
			if (mEpisodeDao == null) {
				mEpisodeDao = mDatabaseHelper.getDao(Episode.class);
			}
			allEp = mEpisodeDao.queryForAllSeen();
		} catch (SQLException e) {
			mServiceStatusCode = AbstractIntentService.DATABASE_ERROR;
			setServiceResponseCode(ServiceResponseCode.KO);
			Ln.e(e);
		}

		// retour à l'appelant
		bundle.putSerializable(EXTRA_OUTPUT_RESULT_DATA, (Serializable) allEp);

	}

	@Override
	protected String getQuery() {
		return "/1/classes/Show/" + mEpisodeToUpdateList.get(mIndex).getObjectId();
	}

	@Override
	protected IHttpsConnector getConnector() {
		return new ParseConnector();
	}

}
