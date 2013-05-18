package com.diaw.lib.parser;

import java.io.IOException;
import java.io.StringWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.diaw.lib.model.Episode;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class EpisodeParser {

//	private Episode mParsedEpisode;


	private int mResultCode = -1;
	private static final String REGEX_NUMBER_ONE = "(.*?)[.\\s_-]*[sS]?(\\d{1,})[eExX](\\d{1,})(.*)";
	private static final String REGEX_DOUBLE_EPISODE = "[eExX_-](\\d{1,}).*";
	public static final int DATA_OK = 0;
	public static final int PARSER_KO_JSON_MALFORMED = 1;
	public static final int PARSER_KO_JSON_OBJETS_INVALID = 2;
	public static final int PARSER_KO = 3;
	
	//episode's URI from vlc re like : file:///E:/BRD%20WIP/South%20Park/Saison1/South%20Park%201x01_-_Cartman_a_une_sonde_anale.avi
	//file:///E:/BRD WIP/South Park/Saison1/South Park 1x01_-_Cartman_a_une_sonde_anale.avi
	
	public Episode parse( String episodeName ) {
		episodeName = episodeName.replace("%20", " ");
		episodeName = episodeName.replace("_", " ");
		episodeName = episodeName.replace("%28", "(");
		episodeName = episodeName.replace("%29", ")");
		Episode ret = null;
		ret = parseRegex( episodeName );
		if( ret != null ) {
			ret.cleanShowName();
		}
	    return ret;
	}
	
	private Episode  parseRegex(String episodeName) {
		episodeName = extractFileName(episodeName);
		Pattern p = Pattern.compile(REGEX_NUMBER_ONE);
		Matcher m = p.matcher(episodeName);
		Episode current = null;
		
	    if (m.matches()) {
	    	current = new Episode(m.group(1), Integer.parseInt(m.group(2)), Integer.parseInt(m.group(3)));
	    	// Manage double episodes
	    	String endName = m.group(4);
	    	Pattern pDoubleEp = Pattern.compile(REGEX_DOUBLE_EPISODE);
	    	Matcher mDoubleEp = pDoubleEp.matcher(endName);
	    	if(mDoubleEp.matches() && Integer.parseInt(mDoubleEp.group(1)) == current.getEpisodeNumber()+1) {
	    		current.setDoubleEpisode(true);
	    	}
	    }
	    return current;
	}

	private String extractFileName(String episodeName) {
		int lastSlash = episodeName.lastIndexOf("/");
		episodeName = episodeName.substring(lastSlash + 1);
		return episodeName;
	}

	public String serialize(Episode ep) {
		mResultCode = -1;
		ObjectMapper mapper = new ObjectMapper(); // can reuse, share globally
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		StringWriter ret = new StringWriter();
		try {
			mapper.writeValue(ret, ep);
			setResultCode(DATA_OK);
		} catch (JsonGenerationException e) {
			setResultCode(PARSER_KO_JSON_MALFORMED);
		} catch (JsonMappingException e) {
			setResultCode(PARSER_KO_JSON_OBJETS_INVALID);
		} catch (IOException e) {
			setResultCode(PARSER_KO);
		}
		return ret.getBuffer().toString();
	}


	public int getResultCode() {
		return mResultCode;
	}

	public void setResultCode(int mResultCode) {
		this.mResultCode = mResultCode;
	}
	
}
