package com.steto.diaw.parser;

import com.steto.diaw.model.TVDBContainerData;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.io.InputStream;

import roboguice.util.Ln;

/**
 * Created by Stephane on 02/06/13.
 */
public class SeriesParser extends AbstractParser {

	public TVDBContainerData parse(InputStream in) {
		TVDBContainerData current = null;
		Serializer serializer = new Persister();

		try {
			current = serializer.read(TVDBContainerData.class, in);
		} catch (Exception e) {
			Ln.e(e);
			setStatusCode(PARSER_GENERAL_EXCEPTION);
		}
		return current;
	}
}
