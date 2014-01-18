package com.steto.diaw.activity;

import roboguice.activity.RoboActivity;
import roboguice.inject.ContentView;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;

import com.google.inject.Inject;
import com.steto.diaw.tools.Tools;
import com.steto.projectdiaw.R;

@ContentView(R.layout.activity_splash_screen)
public class SplashScreen extends RoboActivity {

	private String mLogin = null;

	@Inject
	private SharedPreferences mSharedPreferences;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (needToLogin()) {
			Intent intent = new Intent(this, LoginActivity.class);
			startActivity(intent);
			finish();
		} else {
			// TODO to delete if Sync is ok
			// getShowsFromNetwork();
			Intent intent = new Intent(SplashScreen.this, EpisodesSeenActivity.class);
			startActivity(intent);
			finish();
		}
	}

	private boolean needToLogin() {
		mLogin = mSharedPreferences.getString(Tools.SHARED_PREF_LOGIN, "");
		if (TextUtils.isEmpty(mLogin)) {
			return true;
		} else {
			return false;
		}
	}

	// private void getShowsFromNetwork() {
	// Intent intent = new Intent(this, ParseGetEpisodesService.class);
	// intent.putExtra(ParseGetEpisodesService.INTENT_LOGIN, mLogin);
	// intent.putExtra(ParseGetEpisodesService.INTENT_RESULT_RECEIVER, new ShowResultReceiver());
	// startService(intent);
	// }
	// private final class ShowResultReceiver extends ResultReceiver {
	//
	// private ShowResultReceiver() {
	// super(new Handler());
	// }
	//
	// @Override
	// protected void onReceiveResult(int resultCode, Bundle resultData) {
	// super.onReceiveResult(resultCode, resultData);
	// if (resultCode == ParseGetEpisodesService.RESULT_CODE_OK) {
	// Intent intent = new Intent(SplashScreen.this, EpisodesSeenActivity.class);
	// startActivity(intent);
	// finish();
	// } else {
	// Toast.makeText(SplashScreen.this, getString(R.string.msg_erreur_reseau), Toast.LENGTH_SHORT).show();
	// }
	// }
	// }
}
