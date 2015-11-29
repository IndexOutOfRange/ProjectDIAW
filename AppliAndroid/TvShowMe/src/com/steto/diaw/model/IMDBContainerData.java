package com.steto.diaw.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Stephane on 09/06/13.
 */
public class IMDBContainerData {

	@JsonProperty("result")
	public Show[] mShow;

	@JsonProperty("total_found")
	public int mTotalResult;
}
