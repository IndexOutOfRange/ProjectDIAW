package com.diaw.lib.parser;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.diaw.lib.model.Episode;
import com.diaw.lib.model.User;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class EpisodeParser {

//	private Episode mParsedEpisode;


	private int mResultCode = -1;
	private static final String REGEX_NUMBER_ONE = "(.*?)[.\\s][sS](\\d{2})[eE](\\d{2}).*";
	public static final int DATA_OK = 0;
	public static final int PARSER_KO_JSON_MALFORMED = 1;
	public static final int PARSER_KO_JSON_OBJETS_INVALID = 2;
	public static final int PARSER_KO = 3;
	private String[] tests = { "xyz title name S01E02 bla bla", "bla bla title name.S03E04", "the season title name s05e03" };
	
	public Episode parse( String episodeName ) {
		Pattern p = Pattern.compile(REGEX_NUMBER_ONE);


		Matcher m = p.matcher(episodeName);
		Episode current = null;
	    if (m.matches()) {
	    	System.out.printf("Name: %-23s Season: %s Episode: %s%n", m.group(1), m.group(2), m.group(3));
	    	current = new Episode(m.group(1), Integer.parseInt(m.group(2)), Integer.parseInt(m.group(3)));
	    }
	    return current;
	}
	
	public String serialize( Episode ep) {
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
