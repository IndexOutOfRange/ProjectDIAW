package com.steto.diaw.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.steto.diaw.dao.EpisodeDao;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Date;

@Root(name = "Episode", strict = false)
@DatabaseTable(tableName = "Episode", daoClass = EpisodeDao.class)
public class Episode implements Serializable, Comparable<Episode> {

	private static final long serialVersionUID = 1L;
	public static final String COLUMN_SHOWNAME = "showName";
	public static final String COLUMN_OBJECT_ID = "objectId";
	public static final String COLUMN_UPDATED_AT = "updatedAt";
	public static final String COLUMN_SEEN = "seen";
	public static final String COLUMN_EPISODE_NUMBER = "episodeNumber";
	public static final String COLUMN_SEASON_NUMBER = "seasonNumber";
	public static final String COLUMN_ID = "ID";
	public static final String COLUMN_EPISODE_NAME = "episodeName";
	public static final String COLUMN_FIRST_AIRED = "firstAired";
	public static final String COLUMN_OVERVIEW = "overview";

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

	@Element(name = "EpisodeName", required = false)
	@DatabaseField(columnName = COLUMN_EPISODE_NAME)
	private String mEpisodeName = "";

	@Element(name = "FirstAired", required = false)
	@DatabaseField(columnName = COLUMN_FIRST_AIRED)
	private String mFirstAired = "";

	@Element(name = "Overview", required = false)
	@DatabaseField(columnName = COLUMN_OVERVIEW)
	private String mOverview = "";

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

	public String getEpisodeName() {
		return mEpisodeName;
	}

	public void setEpisodeName(String episodeName) {
		mEpisodeName = episodeName;
	}

	public String getOverview() {
		return mOverview;
	}

	public void setOverview(String overview) {
		mOverview = overview;
	}

	public String getFirstAired() {
		return mFirstAired;
	}

	public void setFirstAired(String firstAired) {
		mFirstAired = firstAired;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((mCustomId == null) ? 0 : mCustomId.hashCode());
		result = prime * result + (mDoubleEpisode ? 1231 : 1237);
		result = prime * result + ((mEpisodeName == null) ? 0 : mEpisodeName.hashCode());
		result = prime * result + mEpisodeNumber;
		result = prime * result + ((mFirstAired == null) ? 0 : mFirstAired.hashCode());
		result = prime * result + ((mObjectId == null) ? 0 : mObjectId.hashCode());
		result = prime * result + ((mOverview == null) ? 0 : mOverview.hashCode());
		result = prime * result + mSeasonNumber;
		result = prime * result + (mSeen ? 1231 : 1237);
		result = prime * result + ((mShowName == null) ? 0 : mShowName.hashCode());
		result = prime * result + ((mUpdatedAt == null) ? 0 : mUpdatedAt.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Episode other = (Episode) obj;
		if (mCustomId == null) {
			if (other.mCustomId != null)
				return false;
		} else if (!mCustomId.equals(other.mCustomId))
			return false;
		if (mDoubleEpisode != other.mDoubleEpisode)
			return false;
		if (mEpisodeName == null) {
			if (other.mEpisodeName != null)
				return false;
		} else if (!mEpisodeName.equals(other.mEpisodeName))
			return false;
		if (mEpisodeNumber != other.mEpisodeNumber)
			return false;
		if (mFirstAired == null) {
			if (other.mFirstAired != null)
				return false;
		} else if (!mFirstAired.equals(other.mFirstAired))
			return false;
		if (mObjectId == null) {
			if (other.mObjectId != null)
				return false;
		} else if (!mObjectId.equals(other.mObjectId))
			return false;
		if (mOverview == null) {
			if (other.mOverview != null)
				return false;
		} else if (!mOverview.equals(other.mOverview))
			return false;
		if (mSeasonNumber != other.mSeasonNumber)
			return false;
		if (mSeen != other.mSeen)
			return false;
		if (mShowName == null) {
			if (other.mShowName != null)
				return false;
		} else if (!mShowName.equals(other.mShowName))
			return false;
		if (mUpdatedAt == null) {
			if (other.mUpdatedAt != null)
				return false;
		} else if (!mUpdatedAt.equals(other.mUpdatedAt))
			return false;
		return true;
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
