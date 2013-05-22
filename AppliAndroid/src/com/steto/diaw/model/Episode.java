package com.steto.diaw.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.steto.diaw.dao.EpisodeDao;

import java.io.Serializable;
import java.util.Date;

@DatabaseTable(tableName = "Episode", daoClass = EpisodeDao.class)
public class Episode implements Serializable, Comparable<Episode> {

	private static final long serialVersionUID = 8857517715427495822L;

	@DatabaseField(generatedId = true)
	@JsonIgnore
	private int mId;
	@DatabaseField
	private String mShowName;
	@DatabaseField
	private int mSeasonNumber;
	@DatabaseField
	private int mEpisodeNumber;
	@DatabaseField
	private String mLogin;
	@DatabaseField
	private String mMdp;
	@JsonIgnore
	private boolean mDoubleEpisode = false;
	@DatabaseField
	private Date mUpdatedAt;

	public Episode() {
	}

	public Episode(String name, int season, int episode, String login, String pass, Date updateAt) {
		setShowName(name);
		setSeasonNumber(season);
		setEpisodeNumber(episode);
		setLogin(login);
		setMdp(pass);
		setUpdatedAt(updateAt);
	}

	public Episode(String name, int season, int episode) {
		setShowName(name);
		setSeasonNumber(season);
		setEpisodeNumber(episode);
	}

	public String getLogin() {
		return mLogin;
	}

	public void setLogin(String user) {
		this.mLogin = user;
	}

	public String getMdp() {
		return mMdp;
	}

	public void setMdp(String pass) {
		this.mMdp = pass;
	}

	public int getEpisodeNumber() {
		return mEpisodeNumber;
	}

	public void setEpisodeNumber(int episodeNumber) {
		this.mEpisodeNumber = episodeNumber;
	}

	public int getSeasonNumber() {
		return mSeasonNumber;
	}

	public void setSeasonNumber(int seasonNumber) {
		this.mSeasonNumber = seasonNumber;
	}

	public String getShowName() {
		return mShowName;
	}

	public void setShowName(String showName) {
		this.mShowName = showName;
	}

	public boolean isDoubleEpisode() {
		return mDoubleEpisode;
	}

	public void setDoubleEpisode(boolean doubleEpisode) {
		this.mDoubleEpisode = doubleEpisode;
	}

	public void cleanShowName() {
		mShowName = mShowName.replace(".", " ");
	}

	public Date getUpdatedAt() {
		return mUpdatedAt;
	}

	public void setUpdatedAt(Date updatedAt) {
		this.mUpdatedAt = updatedAt;
	}

	@Override
	public int compareTo(Episode episode) {
		return episode.getUpdatedAt().compareTo(this.getUpdatedAt());
	}
}
