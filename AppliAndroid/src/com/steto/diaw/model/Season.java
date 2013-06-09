package com.steto.diaw.model;

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

}
