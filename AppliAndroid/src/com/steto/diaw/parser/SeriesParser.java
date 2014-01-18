package com.steto.diaw.parser;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import android.util.Log;

import com.steto.diaw.model.TVDBContainerData;

/**
 * Created by Stephane on 02/06/13.
 */
public class SeriesParser extends AbstractParser {

	public TVDBContainerData parse(InputStream in) {
		InputStream inin = in;
		try {
			String tmp = IOUtils.toString(in);
			Log.i("parse", tmp);
			inin = IOUtils.toInputStream(tmp);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		TVDBContainerData current = null;
		Serializer serializer = new Persister();

		try {
			current = serializer.read(TVDBContainerData.class, inin);
		} catch (Exception e) {
			setStatusCode(PARSER_GENERAL_EXCEPTION);
			e.printStackTrace();
		}
		return current;
	}
}
