package com.steto.diaw.model;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Date;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.steto.diaw.dao.EpisodeDao;

@Root(name = "Episode", strict = false)
@DatabaseTable(tableName = "Episode", daoClass = EpisodeDao.class)
public class Episode implements Serializable, Comparable<Episode> {

	private static final long serialVersionUID = 1L;
	public static final String COLUMN_SHOWNAME = "ShowName";
	public static final String COLUMN_OBJECT_ID = "ObjectId";
	public static final String COLUMN_UPDATED_AT = "UpdatedAt";
	public static final String COLUMN_SEEN = "Seen";
	public static final String COLUMN_EPISODE_NUMBER = "EpisodeNumber";
	public static final String COLUMN_SEASON_NUMBER = "SeasonNumber";
	public static final String COLUMN_ID = "ID";
	
	@DatabaseField(id = true, useGetSet = true, columnName = COLUMN_ID)
	@JsonIgnore
	private String mCustomId;
	@DatabaseField(columnName = COLUMN_SHOWNAME)
	private String mShowName;

	@Element(name = "SeasonNumber")
	@DatabaseField(columnName = COLUMN_SEASON_NUMBER)
	private int mSeasonNumber;

	@Element(name = "EpisodeNumber")
	@DatabaseField(columnName = COLUMN_EPISODE_NUMBER)
	private int mEpisodeNumber;

	@JsonIgnore
	private boolean mDoubleEpisode = false;

	@DatabaseField(columnName = COLUMN_UPDATED_AT)
	private Date mUpdatedAt;

	@DatabaseField(columnName = COLUMN_OBJECT_ID)
	private String mObjectId;

	@DatabaseField(columnName = COLUMN_SEEN)
	private boolean mSeen;

	public Episode() {}

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
		generateCustomId();
	}

	public int getEpisodeNumber() {
		return mEpisodeNumber;
	}

	public void setEpisodeNumber(int episodeNumber) {
		this.mEpisodeNumber = episodeNumber;
		generateCustomId();
	}

	public int getSeasonNumber() {
		return mSeasonNumber;
	}

	public void setSeasonNumber(int seasonNumber) {
		this.mSeasonNumber = seasonNumber;
		generateCustomId();
	}

	public String getShowName() {
		return mShowName;
	}

	public void setShowName(String showName) {
		this.mShowName = showName;
		generateCustomId();
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
			generateCustomId();
		}
		return mCustomId;
	}

	private void generateCustomId() {
		setMCustomId(mShowName + mSeasonNumber + "x" + mEpisodeNumber);
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

	public boolean isSeen() {
		return mSeen;
	}

	public void setSeen(boolean mSeen) {
		this.mSeen = mSeen;
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
		if (getUpdatedAt() == null && episode.getUpdatedAt() == null) {
			return 0;
		}
		if (getUpdatedAt() == null) {
			return -1;
		}
		if (episode.getUpdatedAt() == null) {
			return 1;
		}
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
