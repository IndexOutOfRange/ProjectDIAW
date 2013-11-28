package com.steto.diaw.model;

import java.util.ArrayList;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

/**
 * Created by Stephane on 02/06/13.
 */
@Root(name = "Data", strict = false)
public class TVDBContainerData {

    @ElementList(entry="Series", inline=true)
    public ArrayList<Show> Series;
}
