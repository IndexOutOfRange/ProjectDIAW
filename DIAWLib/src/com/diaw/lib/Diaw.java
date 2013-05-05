package com.diaw.lib;

import com.diaw.lib.model.Episode;
import com.diaw.lib.model.User;
import com.diaw.lib.parser.ConfParser;
import com.diaw.lib.parser.EpisodeParser;
import com.diaw.lib.web.ShowConnector;
import com.diaw.lib.web.ParseConnector.HTTPMethod;

public class Diaw {

	private String mConfigFilePath = "E:/Code/DIAW/diaw.ini";
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
			//TODO ?
		} else {
			ConfParser myConf = new ConfParser();
			User myUser = myConf.parse(mConfigFilePath);			
			if( myUser == null ) {
				//TODO check le status code avec myConf.getStatusCode()
			} else {
				current.setLogin(myUser.getLogin());
				current.setMdp(myUser.getPass());
				ShowConnector myConnector = new ShowConnector();
				myConnector.requestFromNetwork("", HTTPMethod.POST, myParser.serialize(current));
			}			
		}		
	}
	
}
