package com.steto.diaw;

import roboguice.inject.SharedPreferencesName;

import com.google.inject.AbstractModule;
import com.steto.diaw.tools.Tools;

public class DiawModule extends AbstractModule {

	@Override
	protected void configure() {
		bindConstant().annotatedWith(SharedPreferencesName.class).to(Tools.SHARED_PREF_FILE);
	}
}
