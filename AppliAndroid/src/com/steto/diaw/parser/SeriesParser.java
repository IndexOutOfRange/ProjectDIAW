package com.steto.diaw.parser;

import java.io.InputStream;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import roboguice.util.Ln;

import com.steto.diaw.model.TVDBContainerData;

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
