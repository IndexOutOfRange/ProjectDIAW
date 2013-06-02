package com.steto.diaw.parser;

import com.steto.diaw.model.Show;
import com.steto.diaw.model.TVDBContainerData;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Stephane on 02/06/13.
 */
public class SeriesParser extends AbstractParser {

    public List<Show> parse(InputStream in) {
        TVDBContainerData current = null;
        Serializer serializer = new Persister();

        try {
            current = serializer.read(TVDBContainerData.class, in);
        } catch (Exception e) {
            setStatusCode(PARSER_GENERAL_EXCEPTION);
            e.printStackTrace();
        }
        List<Show> ret = new ArrayList<Show>();
        ret.add(current.mShow);
        return current == null ? null : ret;

    }
}
