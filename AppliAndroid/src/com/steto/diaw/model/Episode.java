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
	public static final String SHOWNAME = "showname";

	@DatabaseField(columnName = SHOWNAME)
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
    @DatabaseField(id = true, useGetSet = true)
    @JsonIgnore
    private String mCustomId;

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


    public String getId() {
        return getMCustomId();
    }

    public void setId(String id) {
        setMCustomId(id);
    }

    public String getMCustomId() {
        if( mCustomId == null ) {
            setMCustomId(mShowName + mSeasonNumber + "x" + mEpisodeNumber);
        }
        return mCustomId;
    }

    public void setMCustomId(String customId) {
        mCustomId = customId;
    }

    public static class OrderShowComparator implements Comparator {
		public int compare(Object o1, Object o2) {
			Episode episode1 = (Episode) o1;
			Episode episode2 = (Episode) o2;

			int seasonComp = episode1.getSeasonNumber() - episode2.getSeasonNumber();

			if (seasonComp != 0)
				return seasonComp;
			else
				return episode1.getEpisodeNumber() - episode2.getEpisodeNumber();
		}
	}
}
