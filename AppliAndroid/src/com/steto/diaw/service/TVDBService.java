package com.steto.diaw.service;

import java.io.IOException;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.http.HttpStatus;

import roboguice.util.Ln;
import android.os.Bundle;

import com.google.inject.Inject;
import com.steto.diaw.dao.DatabaseHelper;
import com.steto.diaw.dao.EpisodeDao;
import com.steto.diaw.dao.ShowDao;
import com.steto.diaw.model.Episode;
import com.steto.diaw.model.Show;
import com.steto.diaw.model.TVDBContainerData;
import com.steto.diaw.network.Response;
import com.steto.diaw.network.connector.IHttpsConnector;
import com.steto.diaw.network.connector.TVDBConnector;
import com.steto.diaw.network.connector.TVDBConnectorWithAPIKey;
import com.steto.diaw.parser.AbstractParser;
import com.steto.diaw.parser.SeriesParser;
import com.steto.diaw.service.model.AbstractIntentService;
import com.steto.diaw.web.QueryString;

public class TVDBService extends AbstractIntentService {

	private static final String NAME = "TVDBService";
	private static final String SERIE_NAME_QUERY = "seriesname";
	private static final String LANGUAGE_QUERY = "language";

	public static final String EXTRA_INPUT_SHOW = "EXTRA_INPUT_SHOW";
	public static final String EXTRA_OUTPUT_DATA = "EXTRA_OUTPUT_DATA";
	public static final String EXTRA_OUTPUT_AMBIGUITY = "EXTRA_OUTPUT_AMBIGUITY";

	@Inject
	private DatabaseHelper mDatabaseHelper;
	private Show mInputShow;
	private List<Show> mShowList;
	private int mIdTvdb = 0;
	private boolean mContainsAmbiguity = false;

	public TVDBService() {
		super(NAME);
	}

	@Override
	protected void processInputExtras(Bundle bundle) {
		super.processInputExtras(bundle);
		mInputShow = (Show) bundle.get(EXTRA_INPUT_SHOW);

	}

	@Override
	protected void processRequest() {
		mIdTvdb = mInputShow.getTVDBID();
		if (mIdTvdb != 0) {
			mShowList = getShowsFromId();
		} else {
			mShowList = getShowsFromName();
			if (mShowList != null && (mShowList.size() == 1 || showIsDoubled())) {
				// si la "recherche" sur TVDB n'a donné qu'un seul resultat
				mIdTvdb = mShowList.get(0).getTVDBID();
				mShowList = getShowsFromId();
			} else {
				mContainsAmbiguity = true;
			}
		}
	}

	@Override
	protected void fillBundleResponse(Bundle bundle) {
		bundle.putBoolean(EXTRA_OUTPUT_AMBIGUITY, mContainsAmbiguity);
		bundle.putSerializable(EXTRA_OUTPUT_DATA, (Serializable) mShowList);
	}

	@Override
	protected String getQuery() {
		if (mIdTvdb != 0) {
			return "series/" + mIdTvdb + "/all/" + Locale.getDefault().getLanguage() + ".xml";
		} else {
			QueryString queryString = new QueryString();
			queryString.add(SERIE_NAME_QUERY, mInputShow.getShowName());
			queryString.add(LANGUAGE_QUERY, Locale.getDefault().getLanguage());
			return "GetSeries.php" + queryString.toString();
		}
	}

	@Override
	protected IHttpsConnector getConnector() {
		if (mIdTvdb != 0) {
			return new TVDBConnectorWithAPIKey();
		} else {
			return new TVDBConnector();
		}
	}

	private List<Show> getShowsFromName() {
		List<Show> listShow = null;
		try {
			Response response = getResponse();
			if (response.getStatusCode() == HttpStatus.SC_OK) {
				SeriesParser myParser = new SeriesParser();
				TVDBContainerData tvdbContainerData = myParser.parse(response.getBody());
				if (myParser.getStatusCode() == AbstractParser.PARSER_OK) {
					listShow = tvdbContainerData.series;
				} else {
					setServiceResponseCode(ServiceResponseCode.KO);
					mServiceStatusCode = AbstractIntentService.PARSING_ERROR;
				}
			} else {
				setServiceResponseCode(ServiceResponseCode.KO);
				mServiceStatusCode = AbstractIntentService.HTTP_ERROR;
			}
		} catch (IOException e) {
			Ln.e(e);
			setServiceResponseCode(ServiceResponseCode.KO);
			mServiceStatusCode = AbstractIntentService.NETWORK_ERROR;
		}
		return listShow;
	}

	private List<Show> getShowsFromId() {
		List<Show> listShow = null;

		if (!mInputShow.isTVDBConnected()) {
			try {
				Response response = getResponse();
				if (response.getStatusCode() == HttpStatus.SC_OK) {
					SeriesParser myParser = new SeriesParser();
					TVDBContainerData tvdbContainerData = myParser.parse(response.getBody());

					if (myParser.getStatusCode() == AbstractParser.PARSER_OK) {
						listShow = tvdbContainerData.series;
						listShow.get(0).setId(mInputShow.getId());
						listShow.get(0).setTVDBConnected(true);
						listShow.get(0).setShowName(mInputShow.getId());
						Ln.i(tvdbContainerData.episodes.size() + " episodes");
						listShow.get(0).setNumberEpisodes(tvdbContainerData.episodes.size());

						try {
							ShowDao showDao = mDatabaseHelper.getDao(Show.class);
							EpisodeDao episodeDao = mDatabaseHelper.getDao(Episode.class);
							showDao.createOrUpdate(listShow.get(0));

							for (Episode ep : tvdbContainerData.episodes) {
								ep.setShowName(mInputShow.getId());
								Episode episodeInDatabase = episodeDao.createIfNotExists(ep);
								if (!episodeInDatabase.equals(episodeDao)) {
									// update
									episodeInDatabase.setEpisodeName(ep.getEpisodeName());
									episodeInDatabase.setFirstAired(ep.getFirstAired());
									episodeInDatabase.setOverview(ep.getOverview());
									episodeDao.update(episodeInDatabase);
								}
							}
						} catch (SQLException e) {
							Ln.e(e);
							setServiceResponseCode(ServiceResponseCode.KO);
							mServiceStatusCode = AbstractIntentService.DATABASE_ERROR;
						}
					} else {
						setServiceResponseCode(ServiceResponseCode.KO);
						mServiceStatusCode = AbstractIntentService.PARSING_ERROR;
					}
				} else {
					setServiceResponseCode(ServiceResponseCode.KO);
					mServiceStatusCode = AbstractIntentService.HTTP_ERROR;
				}
			} catch (IOException e) {
				Ln.e(e);
				setServiceResponseCode(ServiceResponseCode.KO);
				mServiceStatusCode = AbstractIntentService.NETWORK_ERROR;
			}
		} else {
			// si l'episode est deja complet alors on le renvoie à l'appelant
			listShow = new ArrayList<Show>();
			listShow.add(mInputShow);
		}
		return listShow;
	}
	
	private boolean showIsDoubled() {
		boolean twoshow = mShowList.size() == 2;
		boolean hasSameId = mShowList.get(0).getTVDBID() == mShowList.get(1).getTVDBID();
		return twoshow && hasSameId;
	}
}