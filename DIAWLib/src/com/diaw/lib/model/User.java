package com.diaw.lib.model;

public class User {

	private String mLogin;
	private String mPass;
	
	public User() {
		
	}
	
	public User( String login, String pass ) {
		setLogin(login);
		setPass(pass);
	}

	public String getPass() {
		return mPass;
	}

	public void setPass(String mPass) {
		this.mPass = mPass;
	}

	public String getLogin() {
		return mLogin;
	}

	public void setLogin(String mLogin) {
		this.mLogin = mLogin;
	}
}
