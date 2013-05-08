package com.diaw.lib;

import com.diaw.lib.model.Episode;
import com.diaw.lib.model.Results;
import com.diaw.lib.model.User;
import com.diaw.lib.parser.ConfParser;
import com.diaw.lib.parser.EpisodeParser;
import com.diaw.lib.parser.ResultsParser;
import com.diaw.lib.tool.QueryString;
import com.diaw.lib.web.ShowConnector;
import com.diaw.lib.web.ParseConnector.HTTPMethod;

public class Diaw {

	private String mConfigFilePath = "E:/Code/DIAWRelease/diaw.ini";
	public static String RESTAPI = "Pq2pfW4DLkU1TZfcotp2igvsAosgNhDN0UMIRV87";
	public static String APPKey = "NWvYWhOOjIfE3cwQhHGH4Ic6Sdc8FYbTWBKYwPR8";
	
	public Diaw() {		
	}
	
	public Diaw(String configFilePath) {
		mConfigFilePath = configFilePath;
	}
	
	public void addEpisode( String episodeName ) {
		EpisodeParser myParser = new EpisodeParser();
		Episode current = myParser.parse(episodeName);
		if( current == null ) {
	    	System.out.println("cannot parse episode " + episodeName + " aborting.");
		} else {
			ConfParser myConf = new ConfParser();
			User myUser = myConf.parse(mConfigFilePath);			
			if( myUser == null ) {
		    	System.out.println("cannot parse config file " + mConfigFilePath + " aborting.\n Error code : " + myConf.getResultCode());
			} else {
				current.setLogin(myUser.getLogin());
				current.setMdp(myUser.getPass());
				if( !isAlreadySeen(current)) {
					ShowConnector myConnector = new ShowConnector();
					myConnector.requestFromNetwork("", HTTPMethod.POST, myParser.serialize(current));
				}
			}			
		}		
	}

	private boolean isAlreadySeen(Episode current) {
		ShowConnector myConnector = new ShowConnector();
		myConnector.requestFromNetwork(createQueryString(current), HTTPMethod.GET, "");
		if( myConnector.getStatusCode() == 200 && myConnector.getResponseBody() != null) {
			ResultsParser myParser = new ResultsParser();
			Results ep = myParser.deserialize(myConnector.getResponseBody());
			if( ep != null && ep.results != null && ep.results.length > 0) {
				return true;
			}else {
				return false;
			}
		}
		return false;
	}

	private String createQueryString(Episode current) {
		QueryString myQuery = new QueryString();
		myQuery.add("where", createWhereClause(current));
		return myQuery.getQuery();
	}

	private String createWhereClause(Episode current) {
		return "{\"login\": \"" + current.getLogin() + "\", \"mdp\": \"" + current.getMdp() + "\", \"showName\": \"" + current.getShowName() + "\" , \"episodeNumber\": " + current.getEpisodeNumber() + ", \"seasonNumber\": " + current.getSeasonNumber() + " }";
	}
	
}
