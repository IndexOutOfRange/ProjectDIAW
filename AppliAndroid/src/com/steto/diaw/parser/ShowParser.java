package com.steto.diaw.parser;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.steto.diaw.model.Episode;
import com.steto.diaw.model.ListEpisodes;

public class ShowParser {

	public List<Episode> parse( InputStream in) {
		ObjectMapper myMapper = new ObjectMapper();
		ListEpisodes current = null;
		try {
			current = myMapper.readValue(in, ListEpisodes.class);
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return current == null ? null : Arrays.asList(current.results);
		
	}
	
}
