package com.steto.diaw.service;

import java.io.InputStream;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
import com.steto.diaw.model.TVDBContainerData;
import com.steto.diaw.parser.AbstractParser;
import com.steto.diaw.parser.SeriesParser;
import com.steto.diaw.web.QueryString;
import com.steto.diaw.web.SeriesIDConnector;
import com.steto.diaw.web.SeriesNameConnector;
import com.steto.diaw.web.WebConnector;

/**
 * Created by Stephane on 02/06/13.
 */
public class TVDBService extends RoboIntentService {

	private static final String NAME = "TVDBService";
	public static final String INPUT_SERIE = "INPUT_SERIE";
	public static final String INPUT_REAL_NAME = "INPUT_REAL_NAME";
	private static final String SERIE_NAME_QUERY = "seriesname";
	public static final String OUTPUT_DATA = "OUTPUT_DATA";
	public static final String INPUT_RESULTRECEIVER = "INPUT_RESULTRECEIVER";
	public static final int RESULT_CODE_OK = 0;
	public static final int RESULT_CODE_AMBIGUITY = -20; // code d'erreur < a -20 sont des erreurs du service, < -10 des erreurs BDD, <0 des erreurs Parser, > 0
															// les codes retours HTTP

	@Inject
	private DatabaseHelper mDatabaseHelper;

	public TVDBService() {
		super(NAME);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Show inputShow = (Show) intent.getExtras().get(INPUT_SERIE);
		ResultReceiver receiver = (ResultReceiver) intent.getExtras().get(INPUT_RESULTRECEIVER);

		Integer id = inputShow.getTVDBID();
		int responseCode = RESULT_CODE_OK;
		List<Show> listShow = null;
		if (id != 0) {
			listShow = getShowsFromId(inputShow, id);
		} else {
			listShow = getShowsFromName(inputShow);
			if (listShow != null && !listShow.isEmpty() && listShow.size() == 1) {
				// si la "recherche" sur TVDB n'a donné qu'un seul resultat alors pas besoin de lancer une recherche sur IMDB
				listShow = getShowsFromId(inputShow, listShow.get(0).getTVDBID());
			} else {
				responseCode = RESULT_CODE_AMBIGUITY;
			}
		}
		sendBackResult(receiver, responseCode, (Serializable) listShow);

	}

	private void sendBackResult(ResultReceiver receiver, int responseCode, Serializable ret) {
		// retour à l'appelant
		Bundle retBundle = new Bundle();
		retBundle.putSerializable(OUTPUT_DATA, ret);
		receiver.send(responseCode, retBundle);
	}

	private List<Show> getShowsFromName(Show show) {
		List<Show> listShow = null;
		SeriesNameConnector myWeb = new SeriesNameConnector();
		QueryString myQuery = new QueryString();
		myQuery.add(SERIE_NAME_QUERY, show.getShowName());
		myWeb.requestFromNetwork(myQuery.getQuery(), WebConnector.HTTPMethod.GET, null);
		if (myWeb.getStatusCode() == HttpStatus.SC_OK) {
			SeriesParser myParser = new SeriesParser();
			InputStream in = myWeb.getResponseBody();
			TVDBContainerData tvdbContainerData = myParser.parse(in);
			if (myParser.getStatusCode() == AbstractParser.PARSER_OK) {
				listShow = tvdbContainerData.series;
			}
		}
		return listShow;
	}

	private List<Show> getShowsFromId(Show input, Integer id) {
		List<Show> listShow = null;

		if (!input.isTVDBConnected()) {
			SeriesIDConnector myWeb = new SeriesIDConnector();
			myWeb.requestFromNetwork(id.toString() + "/all/" + Locale.getDefault().getLanguage() + ".xml", WebConnector.HTTPMethod.GET, null);

			if (myWeb.getStatusCode() == HttpStatus.SC_OK) {
				SeriesParser myParser = new SeriesParser();
				TVDBContainerData tvdbContainerData = myParser.parse(myWeb.getResponseBody());

				if (myParser.getStatusCode() == AbstractParser.PARSER_OK) {
					listShow = tvdbContainerData.series;
					listShow.get(0).setId(input.getId());
					listShow.get(0).setTVDBConnected(true);
					listShow.get(0).setShowName(input.getId());
					Ln.i(tvdbContainerData.episodes.size() + " episodes");
					listShow.get(0).setNumberEpisodes(tvdbContainerData.episodes.size());

					try {
						ShowDao showDao = mDatabaseHelper.getDao(Show.class);
						EpisodeDao episodeDao = mDatabaseHelper.getDao(Episode.class);
						showDao.createOrUpdate(listShow.get(0));
						for (Episode ep : tvdbContainerData.episodes) {
							ep.setShowName(input.getId());
							episodeDao.createIfNotExists(ep);
						}
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}
		} else {
			// si l'episode est deja complet alors on le renvoie à l'appelant
			listShow = new ArrayList<Show>();
			listShow.add(input);
		}
		return listShow;
	}
}
