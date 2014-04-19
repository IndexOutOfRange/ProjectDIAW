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

public class ParseDeleteEpisodeService extends AbstractIntentService {

	public static final String EXTRA_INPUT_OBJECTS_TO_DELETE = "INTENT_OBJECTS_TO_DELETE";
	public static final String EXTRA_OUTPUT_OBJECTS_NOT_DELETED = "INTENT_OBJECTS_NOT_DELETED";
	public static final String EXTRA_OUTPUT_RESULT_DATA = "INTENT_RESULT_DATA";

	@Inject
	private DatabaseHelper mDatabaseHelper;
	private List<Episode> mListEpisodeToDelete;
	private EpisodeDao mEpisodeDao;
	private List<Episode> mResultFail;

	public ParseDeleteEpisodeService() {
		super("ParseDeleteEpisodeService");
	}

	public ParseDeleteEpisodeService(String name) {
		super(name);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void processInputExtras(Bundle bundle) {
		super.processInputExtras(bundle);
		mListEpisodeToDelete = (List<Episode>) bundle.getSerializable(EXTRA_INPUT_OBJECTS_TO_DELETE);
	}

	@Override
	protected void processRequest() {
		mResultFail = new ArrayList<Episode>();
		try {
			mEpisodeDao = mDatabaseHelper.getDao(Episode.class);
		} catch (SQLException e) {
			mServiceStatusCode = AbstractIntentService.DATABASE_ERROR;
			setServiceResponseCode(ServiceResponseCode.KO);
			Ln.e(e);
			return;
		}

		for (Episode episodeToDelete : mListEpisodeToDelete) {
			Ln.d("Episode to delete : " + episodeToDelete.getMCustomId());
			Response response = null;
			try {
				response = deleteResponse(episodeToDelete.getObjectId(), null);
			} catch (IOException e) {
				Ln.e(e);
				mServiceStatusCode = AbstractIntentService.NETWORK_ERROR;
				setServiceResponseCode(ServiceResponseCode.KO);
				return;
			}

			if (response.getStatusCode() == HttpStatus.SC_OK) {
				try {
					mEpisodeDao.delete(episodeToDelete);
				} catch (SQLException e) {
					mResultFail.add(episodeToDelete);
					mServiceStatusCode = AbstractIntentService.DATABASE_ERROR;
					setServiceResponseCode(ServiceResponseCode.KO);
					Ln.e(e);
				}
			} else {
				mServiceStatusCode = AbstractIntentService.HTTP_ERROR;
				setServiceResponseCode(ServiceResponseCode.KO);
				mResultFail.add(episodeToDelete);
			}
		}
	}

	@Override
	protected void fillBundleResponse(Bundle bundle) {
		List<Episode> allEp = new ArrayList<Episode>();
		try {
			if(mEpisodeDao == null) {
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
		bundle.putSerializable(EXTRA_OUTPUT_OBJECTS_NOT_DELETED, (Serializable) mResultFail);
	}

	@Override
	protected String getQuery() {
		return "/1/classes/Show/";
	}

	@Override
	protected IHttpsConnector getConnector() {
		return new ParseConnector();
	}

}
