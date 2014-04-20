package com.steto.diaw;

import roboguice.RoboGuice;
import android.app.Application;

import com.google.inject.Stage;

public class DiawApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		RoboGuice.setBaseApplicationInjector(this, Stage.PRODUCTION, RoboGuice.newDefaultRoboModule(this), new DiawModule());
	}
}
