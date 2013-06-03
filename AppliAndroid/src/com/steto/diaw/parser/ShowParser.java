package com.steto.diaw.parser;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.steto.diaw.model.Episode;
import com.steto.diaw.model.Results;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

public class ShowParser extends AbstractParser{

	public List<Episode> parse(InputStream in) {
		ObjectMapper myMapper = new ObjectMapper();
		myMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		Results current = null;
		try {
			current = myMapper.readValue(in, Results.class);
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
		return current == null ? null : Arrays.asList(current.results);

	}
}
