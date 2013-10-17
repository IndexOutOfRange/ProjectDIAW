package com.steto.diaw.service;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpStatus;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;

import com.steto.diaw.dao.DatabaseHelper;
import com.steto.diaw.dao.EpisodeDao;
import com.steto.diaw.model.Episode;
import com.steto.diaw.web.ParseConnector;
import com.steto.diaw.web.ShowConnector;

/**
 * Created by Benjamin on 09/06/13.
 */
public class ParseUpdateEpisodeService extends IntentService {

	public static final String INTENT_RESULT_RECEIVER = "INTENT_RESULT_RECEIVER";
	public static final String INTENT_OBJECT_TO_RENAME = "INTENT_OBJECT_TO_RENAME";
	public static final String INTENT_KEY = "INTENT_KEY";
	public static final String INTENT_VALUE = "INTENT_VALUE";
	public static final String RESULT_DATA = "RESULT_DATA";
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
		String objectId = intent.getExtras().getString(INTENT_OBJECT_TO_RENAME);
		String key = intent.getExtras().getString(INTENT_KEY);
		String value = intent.getExtras().getString(INTENT_VALUE);
		int responseCode = RESULT_CODE_OK;

		String body = createUpdate(key, value);
		ShowConnector myWeb = new ShowConnector(objectId);
		myWeb.requestFromNetwork("", ParseConnector.HTTPMethod.PUT, body);
		if (myWeb.getStatusCode() == HttpStatus.SC_OK) {
			// la maj ne donne que la updateDate, on refait donc un appel au serveur pour avoir
			// exactement les mêmes données
			myWeb.requestFromNetwork("", ParseConnector.HTTPMethod.GET, "");
			if(myWeb.getStatusCode() == HttpStatus.SC_OK) {
				//pas de parsing des données JSON car le JSON donne que la updateDate

				try {
					EpisodeDao epDao = DatabaseHelper.getInstance(this).getEpisodeDao();
					Episode episodeUpdated = epDao.queryForEq(Episode.COLUMN_OBJECT_ID, objectId).get(0);
					episodeUpdated.setUpdatedAt(new Date());
					if(Episode.COLUMN_SHOWNAME.equals(key)) {
						episodeUpdated.setShowName(value);
					}
					// TODO
					//episodeUpdated.setEpisodeNumber(result.get(0).getEpisodeNumber());
					//episodeUpdated.setSeasonNumber(result.get(0).getSeasonNumber());
					epDao.update(episodeUpdated);
				} catch (SQLException e) {
					responseCode = DatabaseHelper.ERROR_BDD;
					e.printStackTrace();
				}
			}
		} else {
			responseCode = myWeb.getStatusCode();
		}

		List<Episode> allEp = new ArrayList<Episode>();
		try {
			allEp = DatabaseHelper.getInstance(this).getEpisodeDao().queryForAll();
		} catch (SQLException e) {
			responseCode = DatabaseHelper.ERROR_BDD;
			e.printStackTrace();
		}

		//retour à l'appelant
		Bundle ret = new Bundle();
		ret.putSerializable(RESULT_DATA, (Serializable) allEp);
		sender.send(responseCode, ret);
	}
}
