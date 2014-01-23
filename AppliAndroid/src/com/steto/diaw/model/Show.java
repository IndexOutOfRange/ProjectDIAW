package com.steto.diaw.model;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Date;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.steto.diaw.dao.ShowDao;

@JsonIgnoreProperties(ignoreUnknown = true)
@Root(name = "Series", strict = false)
@DatabaseTable(tableName = "Show", daoClass = ShowDao.class)
public class Show implements Serializable, Comparable<Show> {

	private static final long serialVersionUID = 1L;
	public static final String COLUMN_ID = "ID";
	public static final String COLUMN_SHOWNAME = "ShowName";
	public static final String COLUMN_SEASON_NUMBER = "NumberSeasons";
	public static final String COLUMN_EPISODE_NUMBER = "NumberEpisodes";
	public static final String COLUMN_TVDB_ID = "TVDB_ID";
	public static final String COLUMN_IMDB_ID = "IMDB_ID";
	public static final String COLUMN_DATE_DEBUT = "DateDebut";
	public static final String COLUMN_GENRE = "Genre";
	public static final String COLUMN_CHAINE = "Chaine";
	public static final String COLUMN_RESUME = "Resume";
	public static final String COLUMN_STATUS = "Status";
	public static final String COLUMN_BANNER_URL = "BannerUrl";
	public static final String COLUMN_BANNER_DATA = "BannerData";
	public static final String COLUMN_TVDB_CONNECTED = "TVDB_CONNECTED";

	@DatabaseField(id = true, useGetSet = true, columnName = COLUMN_ID)
	private String mCustomId;

	@JsonProperty("title")
	@Element(name = "SeriesName")
	@DatabaseField(columnName = COLUMN_SHOWNAME)
	private String mShowName;

	@DatabaseField(columnName = COLUMN_SEASON_NUMBER)
	private int mNumberSeasons;

	@DatabaseField(columnName = COLUMN_EPISODE_NUMBER)
	private int mNumberEpisodes;

	@DatabaseField(columnName = COLUMN_TVDB_ID)
	@Element(name = "id")
	private int mTVDBID;

	@JsonProperty("imdb_id")
	@DatabaseField(columnName = COLUMN_IMDB_ID)
	private String mIMDBID;

	@DatabaseField(columnName = COLUMN_DATE_DEBUT)
	@Element(name = "FirstAired", required = false)
	private Date mDateDebut;

	@DatabaseField(columnName = COLUMN_GENRE)
	@Element(name = "Genre", required = false)
	private String mGenre;

	@DatabaseField(columnName = COLUMN_CHAINE)
	@Element(name = "Network", required = false)
	private String mChaine;

	@DatabaseField(columnName = COLUMN_RESUME)
	@Element(name = "Overview", required = false)
	private String mResume;

	@DatabaseField(columnName = COLUMN_STATUS)
	@Element(name = "Status", required = false)
	private String mStatus;

	@DatabaseField(columnName = COLUMN_BANNER_URL)
	@Element(name = "banner", required = false)
	private String mBannerURL;

	@DatabaseField(dataType = DataType.BYTE_ARRAY, columnName = COLUMN_BANNER_DATA)
	private byte[] mBanner;

	@DatabaseField(columnName = COLUMN_TVDB_CONNECTED)
	private boolean mTVDBConnected;

	public Show() {}

	public Show(String name) {
		setShowName(name);
	}

	public Show(String name, int numberSeasons, int numberEpisodes) {
		setShowName(name);
		setNumberSeasons(numberSeasons);
		setNumberEpisodes(numberEpisodes);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		Show show = (Show) o;

		if (mShowName != null ? !mShowName.equals(show.mShowName) : show.mShowName != null)
			return false;

		return true;
	}

	@Override
	public int compareTo(Show show) {
		return this.getShowName().compareTo(show.getShowName());
	}

	public String getShowName() {
		return mShowName;
	}

	public void setShowName(String showName) {
		this.mShowName = showName;
	}

	public int getNumberSeasons() {
		return mNumberSeasons;
	}

	public void setNumberSeasons(int numberSeasons) {
		this.mNumberSeasons = numberSeasons;
	}

	public int getNumberEpisodes() {
		return mNumberEpisodes;
	}

	public void setNumberEpisodes(int numberEpisodes) {
		this.mNumberEpisodes = numberEpisodes;
	}

	public String getId() {
		return getMCustomId();
	}

	public void setId(String id) {
		setMCustomId(id);
	}

	public int getTVDBID() {
		return mTVDBID;
	}

	public void setTVDBID(int tvdbId) {
		mTVDBID = tvdbId;
	}

	public String getStatus() {
		return mStatus;
	}

	public void setStatus(String status) {
		mStatus = status;
	}

	public String getResume() {
		return mResume;
	}

	public void setResume(String resume) {
		mResume = resume;
	}

	public String getChaine() {
		return mChaine;
	}

	public void setChaine(String chaine) {
		mChaine = chaine;
	}

	public String getGenre() {
		return mGenre;
	}

	public void setGenre(String genre) {
		mGenre = genre;
	}

	public Date getDateDebut() {
		return mDateDebut;
	}

	public void setDateDebut(Date dateDebut) {
		mDateDebut = dateDebut;
	}

	public String getBannerURL() {
		return mBannerURL;
	}

	public void setBannerURL(String bannerURL) {
		mBannerURL = bannerURL;
	}

	public byte[] getBanner() {
		return mBanner;
	}

	public Bitmap getBannerAsBitmap() {
		InputStream is = new ByteArrayInputStream(mBanner);
		Bitmap bmp = BitmapFactory.decodeStream(is);
		return bmp;
	}

	public void setBanner(byte[] banner) {
		mBanner = banner;
	}

	@JsonIgnore
	public void setBanner(Bitmap banner) {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		banner.compress(Bitmap.CompressFormat.PNG, 100, stream);
		mBanner = stream.toByteArray();
	}

	public boolean isTVDBConnected() {
		return mTVDBConnected;
	}

	public void setTVDBConnected(boolean tvdbConnected) {
		mTVDBConnected = tvdbConnected;
	}

	public String getMCustomId() {
		if (mCustomId == null) {
			setMCustomId(mShowName);
		}
		return mCustomId;
	}

	public void setMCustomId(String customId) {
		mCustomId = customId;
	}

	public String getIMDBID() {
		return mIMDBID;
	}

	public void setIMDBID(String imdb) {
		mIMDBID = imdb;
	}
}
