package com.steto.diaw.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.steto.diaw.dao.EpisodeDao;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Date;

@DatabaseTable(tableName = "Episode", daoClass = EpisodeDao.class)
public class Episode implements Serializable, Comparable<Episode> {

	private static final long serialVersionUID = 8857517715427495822L;
	public static final String COLUMN_SHOWNAME = "showName";
	public static final String COLUMN_OBJECT_ID = "objectId";
	public static final String COLUMN_UPDATED_AT = "updatedAt";

	@DatabaseField(columnName = COLUMN_SHOWNAME)
	private String mShowName;
	@DatabaseField
	private int mSeasonNumber;
	@DatabaseField
	private int mEpisodeNumber;
	@JsonIgnore
	private boolean mDoubleEpisode = false;
	@DatabaseField(columnName =  COLUMN_UPDATED_AT)
	private Date mUpdatedAt;
	@DatabaseField(id = true, useGetSet = true)
	@JsonIgnore
	private String mCustomId;
	@DatabaseField(columnName = COLUMN_OBJECT_ID)
	private String mObjectId;

	public Episode() {
	}

	public Episode(String name, int season, int episode, Date updateAt) {
		setShowName(name);
		setSeasonNumber(season);
		setEpisodeNumber(episode);
		setUpdatedAt(updateAt);
	}

	public Episode(String name, int season, int episode) {
		setShowName(name);
		setSeasonNumber(season);
		setEpisodeNumber(episode);
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

	public String getId() {
		return getMCustomId();
	}

	public void setId(String id) {
		setMCustomId(id);
	}

	public String getMCustomId() {
		if (mCustomId == null) {
			setMCustomId(mShowName + mSeasonNumber + "x" + mEpisodeNumber);
		}
		return mCustomId;
	}

	public void setMCustomId(String customId) {
		mCustomId = customId;
	}

	public String getObjectId() {
		return mObjectId;
	}

	public void setObjectId(String objectId) {
		this.mObjectId = objectId;
	}

	@Override
	public String toString() {
		return "Episode{" +
				"mShowName='" + mShowName + '\'' +
				", mSeasonNumber=" + mSeasonNumber +
				", mEpisodeNumber=" + mEpisodeNumber +
				'}';
	}

	@Override
	public int compareTo(Episode episode) {
		return episode.getUpdatedAt().compareTo(this.getUpdatedAt());
	}

	public static class OrderShowComparator implements Comparator<Episode> {
		public int compare(Episode episode1, Episode episode2) {

			int seasonComp = episode1.getSeasonNumber() - episode2.getSeasonNumber();

			if (seasonComp != 0)
				return seasonComp;
			else
				return episode1.getEpisodeNumber() - episode2.getEpisodeNumber();
		}
	}
}
