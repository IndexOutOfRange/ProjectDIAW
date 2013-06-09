package com.steto.diaw.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;
import com.steto.diaw.dao.DatabaseHelper;
import com.steto.diaw.dao.EpisodeDao;
import com.steto.diaw.model.Episode;
import com.steto.diaw.parser.ShowParser;
import com.steto.diaw.web.ParseConnector;
import com.steto.diaw.web.ShowConnector;
import org.apache.http.HttpStatus;

import java.io.InputStream;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by Benjamin on 09/06/13.
 */
public class ParseUpdateEpisodeService extends IntentService {

	public static final String INTENT_RESULT_RECEIVER = "INTENT_RESULT_RECEIVER";
	public static final String INTENT_OBJECT_ID = "INTENT_OBJECT_ID";
	public static final String INTENT_KEY = "INTENT_KEY";
	public static final String INTENT_VALUE = "INTENT_VALUE";
	public static final int RESULT_CODE_OK = 0;

	public ParseUpdateEpisodeService() {
		super("ParseUpdateEpisodeService");
	}

	public ParseUpdateEpisodeService(String name) {
		super(name);
	}

	public String createUpdate(String key, String value) {
		return "{\"" + key + "\": \"" + value + "\" }";
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		ResultReceiver sender = (ResultReceiver) intent.getExtras().get(INTENT_RESULT_RECEIVER);
		String objectId = intent.getExtras().getString(INTENT_OBJECT_ID);
		String key = intent.getExtras().getString(INTENT_KEY);
		String value = intent.getExtras().getString(INTENT_VALUE);
		int responseCode = RESULT_CODE_OK;
		List<Episode> result = null;

		String body = createUpdate(key, value);
		ShowConnector myWeb = new ShowConnector(objectId);
		myWeb.requestFromNetwork("", ParseConnector.HTTPMethod.PUT, body);
		if (myWeb.getStatusCode() == HttpStatus.SC_OK) {
			//parsing des données JSON
			InputStream response = myWeb.getResponseBody();
			ShowParser myParser = new ShowParser();
			result = myParser.parse(response);
			responseCode = myParser.getStatusCode();

			try {
				EpisodeDao epDao = DatabaseHelper.getInstance(this).getEpisodeDao();
				Episode episodeUpdated = epDao.queryForEq(Episode.COLUMN_OBJECT_ID, objectId).get(0);
				episodeUpdated.setUpdatedAt(result.get(0).getUpdatedAt());
				epDao.update(episodeUpdated);
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
