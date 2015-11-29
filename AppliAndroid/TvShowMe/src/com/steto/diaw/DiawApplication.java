package com.steto.diaw;

import android.app.Application;

import com.google.inject.Stage;

import roboguice.RoboGuice;

public class DiawApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		RoboGuice.setBaseApplicationInjector(this, Stage.PRODUCTION, RoboGuice.newDefaultRoboModule(this), new DiawModule());
	}
}
