package com.steto.diaw.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import com.steto.diaw.dao.DatabaseHelper;
import com.steto.diaw.dao.EpisodeDao;
import com.steto.diaw.model.Episode;
import com.steto.diaw.parser.ShowParser;
import com.steto.diaw.web.ParseConnector;
import com.steto.diaw.web.ShowConnector;
import com.steto.diaw.web.WebConnector;
import org.apache.http.HttpStatus;

import java.io.InputStream;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by Benjamin on 09/06/13.
 */
public class ParseDeleteEpisodeService extends IntentService {

	public static final String INTENT_RESULT_RECEIVER = "INTENT_RESULT_RECEIVER";
	public static final String INTENT_OBJECT_ID = "INTENT_OBJECT_ID";
	public static final int RESULT_CODE_OK = 0;

	public ParseDeleteEpisodeService() {
		super("ParseDeleteEpisodeService");
	}

	public ParseDeleteEpisodeService(String name) {
		super(name);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		ResultReceiver sender = (ResultReceiver) intent.getExtras().get(INTENT_RESULT_RECEIVER);
		String objectId = intent.getExtras().getString(INTENT_OBJECT_ID);
		int responseCode = RESULT_CODE_OK;
		List<Episode> result = null;

		ShowConnector myWeb = new ShowConnector(objectId);
		myWeb.requestFromNetwork(null, ParseConnector.HTTPMethod.DELETE, null);
		if (myWeb.getStatusCode() == HttpStatus.SC_OK) {
			try {
				EpisodeDao epDao = DatabaseHelper.getInstance(this).getEpisodeDao();
				Episode episodeUpdated = epDao.queryForEq(Episode.COLUMN_OBJECT_ID, objectId).get(0);
				epDao.delete(episodeUpdated);
			} catch (SQLException e) {
				responseCode = DatabaseHelper.ERROR_BDD;
				e.printStackTrace();
			}
		} else {
			responseCode = myWeb.getStatusCode();
		}

		//retour à l'appelant
		Bundle ret = new Bundle();
		ret.putSerializable(INTENT_OBJECT_ID, objectId);
		sender.send(responseCode, ret);
	}
}
