package com.steto.diaw.model;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Stephane on 02/06/13.
 */
@Root(name = "Data", strict = false)
public class TVDBContainerData {

    @ElementList(entry="Series", inline=true)
    public ArrayList<Show> Series;
}
