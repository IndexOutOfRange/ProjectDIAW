package com.steto.diaw.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;
import com.steto.diaw.dao.DatabaseHelper;
import com.steto.diaw.dao.EpisodeDao;
import com.steto.diaw.model.Episode;
import com.steto.diaw.web.ParseConnector;
import com.steto.diaw.web.ShowConnector;
import org.apache.http.HttpStatus;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Benjamin on 09/06/13.
 */
public class ParseDeleteEpisodeService extends IntentService {

	public static final String INTENT_RESULT_RECEIVER = "INTENT_RESULT_RECEIVER";
	public static final String INTENT_OBJECTS_TO_DELETE = "INTENT_OBJECTS_TO_DELETE";
	public static final String INTENT_OBJECTS_NOT_DELETED = "INTENT_OBJECTS_NOT_DELETED";
	public static final String RESULT_DATA = "RESULT_DATA";
	public static final int RESULT_CODE_OK = 0;
	public static final int RESULT_CODE_ERROR = -1;
	private static final String TAG = "ParseDeleteEpisodeService";

	public ParseDeleteEpisodeService() {
		super(TAG);
	}

	public ParseDeleteEpisodeService(String name) {
		super(name);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		ResultReceiver sender = (ResultReceiver) intent.getExtras().get(INTENT_RESULT_RECEIVER);
		List<Episode> episodesToDelete = (List<Episode>) intent.getExtras().getSerializable(INTENT_OBJECTS_TO_DELETE);
		int responseCode = RESULT_CODE_ERROR;
		List<Episode> allEp = new ArrayList<Episode>();
		List<Episode> resultFail = new ArrayList<Episode>();

		for (Episode episodeToDelete : episodesToDelete) {
			Log.d(TAG, "Episode to delete : " + episodeToDelete.getMCustomId());
			ShowConnector myWeb = new ShowConnector(episodeToDelete.getObjectId());
			myWeb.requestFromNetwork(null, ParseConnector.HTTPMethod.DELETE, null);
			if (myWeb.getStatusCode() == HttpStatus.SC_OK) {
				try {
					EpisodeDao epDao = DatabaseHelper.getInstance(this).getEpisodeDao();
					Episode episodeUpdated = epDao.queryForEq(Episode.COLUMN_OBJECT_ID, episodeToDelete.getObjectId()).get(0);
					epDao.delete(episodeUpdated);
				} catch (SQLException e) {
					resultFail.add(episodeToDelete);
					responseCode = DatabaseHelper.ERROR_BDD;
					e.printStackTrace();
				}
			} else {
				responseCode = myWeb.getStatusCode();
				resultFail.add(episodeToDelete);
			}
		}

		try {
			allEp = DatabaseHelper.getInstance(this).getEpisodeDao().queryForAll();
			if (responseCode == RESULT_CODE_ERROR) responseCode = RESULT_CODE_OK;
		} catch (SQLException e) {
			responseCode = DatabaseHelper.ERROR_BDD;
			e.printStackTrace();
		}

		//retour à l'appelant
		Bundle ret = new Bundle();
		ret.putSerializable(RESULT_DATA, (Serializable) allEp);
		ret.putSerializable(INTENT_OBJECTS_NOT_DELETED, (Serializable) resultFail);
		sender.send(responseCode, ret);
	}
}