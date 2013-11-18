package com.steto.diaw.service;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpStatus;

import roboguice.service.RoboIntentService;
import roboguice.util.Ln;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;

import com.google.inject.Inject;
import com.steto.diaw.dao.DatabaseHelper;
import com.steto.diaw.dao.EpisodeDao;
import com.steto.diaw.dao.ShowDao;
import com.steto.diaw.model.Episode;
import com.steto.diaw.model.Show;
import com.steto.diaw.web.ParseConnector;
import com.steto.diaw.web.ShowConnector;

/**
 * Created by Benjamin on 09/06/13.
 */
public class ParseUpdateEpisodeService extends RoboIntentService {

	public static final String INTENT_RESULT_RECEIVER = "INTENT_RESULT_RECEIVER";
	public static final String INTENT_OBJECT_TO_RENAME = "INTENT_OBJECT_TO_RENAME";
	public static final String INTENT_KEY = "INTENT_KEY";
	public static final String INTENT_VALUE = "INTENT_VALUE";
	public static final String RESULT_DATA = "RESULT_DATA";
	public static final int RESULT_CODE_OK = 0;

	@Inject
	private DatabaseHelper mDatabaseHelper;

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
			// la maj ne donne que la updateDate, on refait donc un appel au serveur pour avoir exactement les mêmes données
			myWeb.requestFromNetwork("", ParseConnector.HTTPMethod.GET, "");
			if (myWeb.getStatusCode() == HttpStatus.SC_OK) {
				// pas de parsing des données JSON car le JSON donne que la updateDate

				try {
					EpisodeDao epDao = mDatabaseHelper.getDao(Episode.class);
					Episode episodeUpdated = epDao.queryForEq(Episode.COLUMN_OBJECT_ID, objectId).get(0);
					String oldShowName = episodeUpdated.getShowName();
					episodeUpdated.setUpdatedAt(new Date());
					
					if (Episode.COLUMN_SHOWNAME.equals(key)) {
						episodeUpdated.setShowName(value);

						ShowDao showDao = mDatabaseHelper.getDao(Show.class);
						if(showDao.queryFromName(value) == null) {
							Ln.d("recherche si le show n'existe pas pour l'ajouter en base");

							Show show = new Show(value);
							showDao.create(show);
						}
						
						epDao.updateEpisodeAfterRename(episodeUpdated);
						
						if(epDao.queryForName(oldShowName).isEmpty()) {
							showDao.deleteFromName(oldShowName);
							Ln.d("Suppression de l'ancien Show " + oldShowName + " devenu inutile");
						}
					}
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
			EpisodeDao epDao = mDatabaseHelper.getDao(Episode.class);
			allEp = epDao.queryForAll();
		} catch (SQLException e) {
			responseCode = DatabaseHelper.ERROR_BDD;
			e.printStackTrace();
		}

		// retour à l'appelant
		Bundle ret = new Bundle();
		ret.putSerializable(RESULT_DATA, (Serializable) allEp);
		sender.send(responseCode, ret);
	}
}
