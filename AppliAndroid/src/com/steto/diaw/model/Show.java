package com.steto.diaw.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.steto.diaw.dao.ShowDao;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Root(name="Series", strict = false)
@DatabaseTable(tableName = "Show", daoClass = ShowDao.class)
public class Show implements Serializable, Comparable<Show> {

	private static final long serialVersionUID = 2402273750582116603L;

    @DatabaseField(id = true, useGetSet = true)
    private String mCustomId;
    @Element(name = "SeriesName")
	@DatabaseField
	private String mShowName;
	@DatabaseField
	private int mNumberSeasons;
	@DatabaseField
	private int mNumberEpisodes;
    @DatabaseField
    @Element(name = "id")
    private int mTVDBID;
    @DatabaseField
    @Element(name = "FirstAired", required = false)
    private Date mDateDebut;
    @DatabaseField
    @Element(name = "Genre", required = false )
    private String mGenre;
    @DatabaseField
    @Element(name = "Network", required = false )
    private String mChaine;
    @DatabaseField
    @Element(name = "Overview", required = false )
    private String mResume;
    @DatabaseField
    @Element(name = "Status", required = false )
    private String mStatus;
    @DatabaseField
    @Element(name = "banner", required = false )
    private String mBannerURL;
    @DatabaseField( dataType=DataType.BYTE_ARRAY )
    private byte[] mBanner;
    @DatabaseField
    private boolean mTVDBConnected;

	public Show() {
	}

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

	public String getId() {
		return getMCustomId();
	}
    public void setId(String id) {
        setMCustomId(id);
    }

    public int getTVDBID() {
        return mTVDBID;
    }

    public void setTVDBID(int TVDBID) {
        mTVDBID = TVDBID;
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

    public void setBanner(Bitmap banner) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        banner.compress(Bitmap.CompressFormat.PNG, 100, stream);
        mBanner = stream.toByteArray();
    }

    public boolean isTVDBConnected() {
        return mTVDBConnected;
    }

    public void setTVDBConnected(boolean TVDBConnected) {
        mTVDBConnected = TVDBConnected;
    }

    public String getMCustomId() {
        if( mCustomId == null) {
            setMCustomId(mShowName);
        }
        return mCustomId;
    }

    public void setMCustomId(String customId) {
        mCustomId = customId;
    }
}
