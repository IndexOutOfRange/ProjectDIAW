package com.steto.diaw.service;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpStatus;

import roboguice.util.Ln;
import android.os.Bundle;

import com.steto.diaw.model.Show;
import com.steto.diaw.network.Response;
import com.steto.diaw.network.connector.IHttpsConnector;
import com.steto.diaw.parser.AbstractParser;
import com.steto.diaw.parser.IMDBParser;
import com.steto.diaw.service.model.AbstractIntentService;
import com.steto.diaw.web.QueryString;

/**
 * unused
 */
@Deprecated
public class IMDBService extends AbstractIntentService {

	private static String IMDB_SERVICE = "IMDB_SERVICE";

	public static String EXTRA_INPUT_TITLE = "SERVICE_INPUT_TITLE";
	public static String EXTRA_OUTPUT_DATA = "SERVICE_OUTPUT_DATA";

	// QUERY CONSTANTS rien d'interessant...
	private static String QUERY_TITLE = "title";
	private static String QUERY_TYPE = "xml";
	private static String QUERY_PLOT = "plot";
	private static String QUERY_EPISODE = "episode";
	private static String QUERY_LIMIT = "limit";
	private static String QUERY_YEAR = "yg";
	private static String QUERY_SHOWTYPE = "mt";
	private static String QUERY_LANG = "lang";
	private static String QUERY_OFFSET = "offset";
	private static String QUERY_AKA = "aka";
	private static String QUERY_RELEASE_DATE = "release";
	private static String QUERY_BUSINESS = "business";
	private static String QUERY_TECH = "tech";
	private static String QUERY_TYPE_VALUE = "json";
	private static String QUERY_PLOT_VALUE = "none";
	private static String QUERY_EPISODE_VALUE = "0";
	private static String QUERY_YEAR_VALUE = "0";
	private static String QUERY_SHOWTYPE_VALUE = "TVS";
	private static String QUERY_LANG_VALUE = "en-US";
	private static String QUERY_AKA_VALUE = "simple";
	private static String QUERY_RELEASE_DATE_VALUE = "simple";
	private static String QUERY_BUSINESS_VALUE = "0";
	private static String QUERY_TECH_VALUE = "0";

	// QUERY VARIABLE
	private static String QUERY_RESULT_PER_PAGE = "10";

	private Integer mSearchOffset = 0;

	private String mTitle;

	private List<Show> mListShow = new ArrayList<Show>();;

	public IMDBService() {
		super(IMDB_SERVICE);
	}

	@Override
	protected void processInputExtras(Bundle bundle) {
		super.processInputExtras(bundle);

		mTitle = bundle.getString(EXTRA_INPUT_TITLE);

	}

	@Override
	protected void processRequest() {
		try {
			Response response = getResponse();
			if (response.getStatusCode() == HttpStatus.SC_OK) {
				IMDBParser imdbParser = new IMDBParser();
				mListShow = imdbParser.parse(response.getBody());
				if (imdbParser.getStatusCode() != AbstractParser.PARSER_OK) {
					setServiceResponseCode(ServiceResponseCode.KO);
					mServiceStatusCode = AbstractIntentService.PARSING_ERROR;
					mListShow = null;
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
	}

	@Override
	protected void fillBundleResponse(Bundle bundle) {
		bundle.putSerializable(EXTRA_OUTPUT_DATA, (Serializable) mListShow);
	}

	@Override
	protected String getQuery() {
		return getQueryString(mTitle).toString();
	}

	@Override
	protected IHttpsConnector getConnector() {
		// TODO Auto-generated method stub
		return null;
	}

	private QueryString getQueryString(String title) {
		QueryString myQuery = new QueryString();
		myQuery.add(QUERY_TITLE, title);
		myQuery.add(QUERY_TYPE, QUERY_TYPE_VALUE);
		myQuery.add(QUERY_PLOT, QUERY_PLOT_VALUE);
		myQuery.add(QUERY_EPISODE, QUERY_EPISODE_VALUE);
		myQuery.add(QUERY_LIMIT, QUERY_RESULT_PER_PAGE);
		myQuery.add(QUERY_YEAR, QUERY_YEAR_VALUE);
		myQuery.add(QUERY_SHOWTYPE, QUERY_SHOWTYPE_VALUE);
		myQuery.add(QUERY_LANG, QUERY_LANG_VALUE);
		myQuery.add(QUERY_OFFSET, mSearchOffset.toString());
		myQuery.add(QUERY_AKA, QUERY_AKA_VALUE);
		myQuery.add(QUERY_RELEASE_DATE, QUERY_RELEASE_DATE_VALUE);
		myQuery.add(QUERY_BUSINESS, QUERY_BUSINESS_VALUE);
		myQuery.add(QUERY_TECH, QUERY_TECH_VALUE);
		return myQuery;
	}
}
