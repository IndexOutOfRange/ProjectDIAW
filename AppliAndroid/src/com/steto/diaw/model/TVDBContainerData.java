package com.steto.diaw.model;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Created by Stephane on 02/06/13.
 */
@Root(name = "Data", strict = false)
public class TVDBContainerData {

	@Element(name = "Series")
	public Show mShow;
}
