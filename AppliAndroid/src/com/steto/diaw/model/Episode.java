package com.steto.diaw.model;

import java.io.Serializable;

public class Episode implements Serializable {

	private static final long serialVersionUID = 1609053326926138723L;

	public String showName;
	public int seasonNumber;
	public int episodeNumber;

	public Episode(String showName, int seasonNumber, int episodeNumber) {
		this.showName = showName;
		this.seasonNumber = seasonNumber;
		this.episodeNumber = episodeNumber;
	}
}
