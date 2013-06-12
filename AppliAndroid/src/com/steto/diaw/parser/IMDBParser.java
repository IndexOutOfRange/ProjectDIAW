package com.steto.diaw.parser;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.steto.diaw.model.IMDBContainerData;
import com.steto.diaw.model.Show;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Stephane on 09/06/13.
 */
public class IMDBParser extends AbstractParser {
	public List<Show> parse(InputStream in) {
		ObjectMapper myMapper = new ObjectMapper();
		myMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		IMDBContainerData current = null;
		try {
			current = myMapper.readValue(in, IMDBContainerData.class);
		} catch (JsonParseException e) {
			setStatusCode(PARSER_MALFORMED_JSON);
			e.printStackTrace();
		} catch (JsonMappingException e) {
			setStatusCode(PARSER_OBJECT_MALFORMED);
			e.printStackTrace();
		} catch (IOException e) {
			setStatusCode(PARSER_GENERAL_EXCEPTION);
			e.printStackTrace();
		}
		return current == null ? null : Arrays.asList(current.mShow);
	}
}
