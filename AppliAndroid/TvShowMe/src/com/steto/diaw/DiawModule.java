package com.steto.diaw;

import com.google.inject.AbstractModule;
import com.steto.diaw.tools.Tools;

import roboguice.inject.SharedPreferencesName;

public class DiawModule extends AbstractModule {

	@Override
	protected void configure() {
		bindConstant().annotatedWith(SharedPreferencesName.class).to(Tools.SHARED_PREF_FILE);
	}
}
