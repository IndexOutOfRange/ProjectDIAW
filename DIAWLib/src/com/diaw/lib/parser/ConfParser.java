package com.diaw.lib.parser;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import sun.security.action.GetLongAction;

import com.diaw.lib.model.User;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ConfParser {

	// exemple de conf
	// {
	// "login": "toto",
	// "pass": "titi"
	// }

	public static final int DATA_OK = 0;
	public static final int PARSER_KO_JSON_MALFORMED = 1;
	public static final int PARSER_KO_JSON_OBJETS_INVALID = 2;
	public static final int PARSER_KO = 3;
	private int mResultCode = -1;

	public User parse(String confPath) {
		mResultCode = -1;
		ObjectMapper mapper = new ObjectMapper(); // can reuse, share globally
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		User JSONObjResponse = null;
		File conf = new File(confPath);
		try {
			JSONObjResponse = mapper.readValue(new FileInputStream(conf), User.class);
			setResultCode(DATA_OK);
		} catch (JsonParseException e) {
			setResultCode(PARSER_KO_JSON_MALFORMED);
			// e.printStackTrace();
		} catch (JsonMappingException e) {
			// e.printStackTrace();
			setResultCode(PARSER_KO_JSON_OBJETS_INVALID);
		} catch (IOException e) {
			System.out.println(e);
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
