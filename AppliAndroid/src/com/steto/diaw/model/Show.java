package com.steto.diaw.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.steto.diaw.dao.ShowDao;

import java.io.Serializable;

@DatabaseTable(tableName = "Show", daoClass = ShowDao.class)
public class Show implements Serializable, Comparable<Show> {

	private static final long serialVersionUID = 2402273750582116603L;

	@DatabaseField(generatedId = true)
	private int mId;
	@DatabaseField
	private String mShowName;
	@DatabaseField
	private int mNumberSeasons;
	@DatabaseField
	private int mNumberEpisodes;

	public Show() {
	}

	public Show(String name) {
		setShowName(name);
	}

	public String getShowName() {
		return mShowName;
	}

	public void setShowName(String showName) {
		this.mShowName = showName;
	}

	public int getmNumberSeasons() {
		return mNumberSeasons;
	}

	public void setNumberSeasons(int numberSeasons) {
		this.mNumberSeasons = numberSeasons;
	}

	public int getmNumberEpisodes() {
		return mNumberEpisodes;
	}

	public void setNumberEpisodes(int numberEpisodes) {
		this.mNumberEpisodes = numberEpisodes;
	}

	public int getId() {
		return mId;
	}

	public void setId(int id) {
		this.mId = id;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Show show = (Show) o;

		if (mShowName != null ? !mShowName.equals(show.mShowName) : show.mShowName != null) return false;

		return true;
	}

	@Override
	public int compareTo(Show show) {
		return this.getShowName().compareTo(show.getShowName());
	}
}
