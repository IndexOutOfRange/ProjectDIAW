package com.diaw.lib.model;

public class Episode {

	private String mShowName;
	private int mSeasonNumber;
	private int mEpisodeNumber;
	private String mLogin;
	private String mMdp;
	private boolean mDoubleEpisode = false;
	
	public Episode() {
	}

	public Episode( String name, int season, int episode, String login, String pass ) {
		setShowName(name);
		setSeasonNumber(season);
		setEpisodeNumber(episode);
		setLogin(login);
		setMdp(pass);
	}
	
	public Episode( String name, int season, int episode ) {
		setShowName(name);
		setSeasonNumber(season);
		setEpisodeNumber(episode);
	}

	public String getLogin() {
		return mLogin;
	}

	public void setLogin(String mUser) {
		this.mLogin = mUser;
	}

	public String getMdp() {
		return mMdp;
	}

	public void setMdp(String mPass) {
		this.mMdp = mPass;
	}

	public int getEpisodeNumber() {
		return mEpisodeNumber;
	}

	public void setEpisodeNumber(int mEpisodeNumber) {
		this.mEpisodeNumber = mEpisodeNumber;
	}

	public int getSeasonNumber() {
		return mSeasonNumber;
	}

	public void setSeasonNumber(int mSeasonNumber) {
		this.mSeasonNumber = mSeasonNumber;
	}

	public String getShowName() {
		return mShowName;
	}

	public void setShowName(String mShowName) {
		this.mShowName = mShowName;
	}
	
	public boolean isDoubleEpisode() {
		return mDoubleEpisode;
	}

	public void setDoubleEpisode(boolean doubleEpisode) {
		this.mDoubleEpisode = doubleEpisode;
	}

	public void cleanShowName() {
		mShowName = mShowName.replace(".", " " );
	}
	
}
