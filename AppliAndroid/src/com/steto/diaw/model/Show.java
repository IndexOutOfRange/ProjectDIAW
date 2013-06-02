package com.steto.diaw.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.steto.diaw.dao.ShowDao;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import java.io.Serializable;
import java.util.Date;

@Root(name="Series", strict = false)
@DatabaseTable(tableName = "Show", daoClass = ShowDao.class)
public class Show implements Serializable, Comparable<Show> {

	private static final long serialVersionUID = 2402273750582116603L;

	@DatabaseField(generatedId = true)
	private int mId;
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

	public int getId() {
		return mId;
	}

	public void setId(int id) {
		this.mId = id;
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
}
