package com.steto.diaw.service;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.http.HttpStatus;

import roboguice.service.RoboIntentService;
import roboguice.util.Ln;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import com.google.inject.Inject;
import com.steto.diaw.dao.DatabaseHelper;
import com.steto.diaw.dao.EpisodeDao;
import com.steto.diaw.model.Episode;
import com.steto.diaw.web.ParseConnector;
import com.steto.diaw.web.ShowConnector;

/**
 * Created by Benjamin on 09/06/13.
 */
public class DeleteService extends RoboIntentService {

	public static final String INTENT_RESULT_RECEIVER = "INTENT_RESULT_RECEIVER";
	
	public static final String EXTRA_INPUT_OBJECTS_TO_DELETE = "INTENT_OBJECTS_TO_DELETE";
	public static final String EXTRA_OUTPUT_OBJECTS_NOT_DELETED = "INTENT_OBJECTS_NOT_DELETED";
	public static final String EXTRA_OUTPUT_RESULT_DATA = "INTENT_RESULT_DATA";
	
	public static final int RESULT_CODE_OK = 0;
	public static final int RESULT_CODE_ERROR = -1;
	private static final String TAG = "ParseDeleteEpisodeService";

	@Inject
	private DatabaseHelper mDatabaseHelper;

	public DeleteService() {
		super(TAG);
	}

	public DeleteService(String name) {
		super(name);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void onHandleIntent(Intent intent) {
		ResultReceiver sender = (ResultReceiver) intent.getExtras().get(INTENT_RESULT_RECEIVER);
		List<Episode> episodesToDelete = (List<Episode>) intent.getExtras().getSerializable(EXTRA_INPUT_OBJECTS_TO_DELETE);
		int responseCode = RESULT_CODE_ERROR;
		List<Episode> allEp = new ArrayList<Episode>();
		List<Episode> resultFail = new ArrayList<Episode>();
		EpisodeDao episodeDao = null;

		try {
			episodeDao = mDatabaseHelper.getDao(Episode.class);
		} catch (SQLException e) {
			Ln.e(e);
		}

		for (Episode episodeToDelete : episodesToDelete) {
			Log.d(TAG, "Episode to delete : " + episodeToDelete.getMCustomId());
			ShowConnector myWeb = new ShowConnector(episodeToDelete.getObjectId());
			myWeb.requestFromNetwork(null, ParseConnector.HTTPMethod.DELETE, null);
			if (myWeb.getStatusCode() == HttpStatus.SC_OK) {
				try {
					Episode episodeUpdated = episodeDao.queryForEq(Episode.COLUMN_OBJECT_ID, episodeToDelete.getObjectId()).get(0);
					episodeDao.delete(episodeUpdated);
				} catch (SQLException e) {
					resultFail.add(episodeToDelete);
					responseCode = DatabaseHelper.ERROR_BDD;
					Ln.e(e);
				}
			} else {
				responseCode = myWeb.getStatusCode();
				resultFail.add(episodeToDelete);
			}
		}

		try {
			allEp = episodeDao.queryForAll();
			Collections.sort(allEp);
			if (responseCode == RESULT_CODE_ERROR)
				responseCode = RESULT_CODE_OK;
		} catch (SQLException e) {
			responseCode = DatabaseHelper.ERROR_BDD;
			Ln.e(e);
		}

		// retour à l'appelant
		Bundle ret = new Bundle();
		ret.putSerializable(EXTRA_OUTPUT_RESULT_DATA, (Serializable) allEp);
		ret.putSerializable(EXTRA_OUTPUT_OBJECTS_NOT_DELETED, (Serializable) resultFail);
		sender.send(responseCode, ret);
	}
}