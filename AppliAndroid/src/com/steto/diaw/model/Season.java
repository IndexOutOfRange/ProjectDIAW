package com.steto.diaw.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Season {

	private int mNumber;
	private String mYear = "";
	private List<Episode> mEpisodes;

	public Season(int mNumber, String mYear, List<Episode> mEpisodes) {
		this.mNumber = mNumber;
		this.mYear = mYear;
		this.mEpisodes = mEpisodes;
	}

	public Season() {
	}

	public int getNumber() {
		return mNumber;
	}

	public void setNumber(int number) {
		this.mNumber = number;
	}

	public String getYear() {
		return mYear;
	}

	public void setYear(String year) {
		this.mYear = year;
	}

	public List<Episode> getEpisodes() {
		return mEpisodes;
	}

	public void setEpisodes(List<Episode> episodes) {
		this.mEpisodes = episodes;
	}

	public static List<Season> getListSeason(List<Episode> episodes) {
		if (episodes == null || episodes.isEmpty()) {
			return new ArrayList<Season>();
		}

		Collections.sort(episodes, new Episode.OrderShowComparator());
		List<Season> seasons = new ArrayList<Season>();

		Season season = new Season();
		List<Episode> episodesToAdd = new ArrayList<Episode>();

		season.setNumber(episodes.get(0).getSeasonNumber());
		episodesToAdd.add(episodes.get(0));

		for (int i = 1; i < episodes.size(); i++) {
			if (episodes.get(i).getSeasonNumber() == season.getNumber()) {
				episodesToAdd.add(episodes.get(i));
			} else {
				// change of season
				season.setEpisodes(episodesToAdd);
				seasons.add(season);

				season = new Season();
				episodesToAdd = new ArrayList<Episode>();
				season.setNumber(episodes.get(i).getSeasonNumber());
				episodesToAdd.add(episodes.get(i));
			}
		}

		season.setEpisodes(episodesToAdd);
		seasons.add(season);

		return seasons;
	}
}
