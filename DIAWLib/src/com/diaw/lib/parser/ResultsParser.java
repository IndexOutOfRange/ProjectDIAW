package com.diaw.lib.parser;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import com.diaw.lib.model.Episode;
import com.diaw.lib.model.Results;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ResultsParser {
	
	private int mResultCode = -1;
	public static final int DATA_OK = 0;
	public static final int PARSER_KO_JSON_MALFORMED = 1;
	public static final int PARSER_KO_JSON_OBJETS_INVALID = 2;
	public static final int PARSER_KO = 3;

	public Results deserialize( String JSON) {
		setResultCode(-1);
		ObjectMapper mapper = new ObjectMapper(); // can reuse, share globally
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		Results JSONObjResponse = null;
		ByteArrayInputStream in = new ByteArrayInputStream(JSON.getBytes());
		try {
			JSONObjResponse = mapper.readValue(in, Results.class);
			setResultCode(DATA_OK);
		} catch (JsonParseException e) {
			setResultCode(PARSER_KO_JSON_MALFORMED);
			// e.printStackTrace();
		} catch (JsonMappingException e) {
			// e.printStackTrace();
			setResultCode(PARSER_KO_JSON_OBJETS_INVALID);
		} catch (IOException e) {
			// e.printStackTrace();
			setResultCode(PARSER_KO);
		}

		return JSONObjResponse;
	}

	public int getResultCode() {
		return mResultCode;
	}

	public void setResultCode(int mResultCode) {
		this.mResultCode = mResultCode;
	}
}
