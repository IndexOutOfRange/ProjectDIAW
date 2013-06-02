package com.steto.diaw.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.widget.Toast;
import com.actionbarsherlock.app.SherlockActivity;
import com.steto.diaw.model.Show;
import com.steto.diaw.service.SeriesService;
import com.steto.diaw.service.ShowService;
import com.steto.diaw.tools.Tools;
import com.steto.projectdiaw.R;

public class SplashScreen extends SherlockActivity {


	private static final String TAG = "SplashScreen";

	private String mLogin = "";
	private ResultReceiver mShowResultReceiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash_screen);


		if (needToLogin()) {
			Intent in = new Intent(this, LoginActivity.class);
			startActivity(in);
			finish();
		} else {

            initShowResultReceiver();
            getBackShows();

		}

	}

	private void getBackShows() {
		Intent in = new Intent(this, ShowService.class);
		in.putExtra(ShowService.INTENT_LOGIN, mLogin);
		in.putExtra(ShowService.INTENT_RESULT_RECEIVER, mShowResultReceiver);
		startService(in);

	}

	private ResultReceiver initShowResultReceiver() {
		if (mShowResultReceiver == null) {
			mShowResultReceiver = new ResultReceiver(null) {

				@Override
				protected void onReceiveResult(int resultCode, Bundle resultData) {
					super.onReceiveResult(resultCode, resultData);
					if (resultCode == ShowService.RESULT_CODE_OK) {
                        Intent in = new Intent(SplashScreen.this, HomeActivity.class);
                        in.putExtra(HomeActivity.INTENT_LIST_EPISODE, resultData.getSerializable(ShowService.RESULT_DATA));
                        startActivity(in);
						finish();
					} else {
						Toast.makeText(SplashScreen.this, getString(R.string.msg_erreur_reseau), Toast.LENGTH_SHORT).show();
					}
				}
			};
		}
		return mShowResultReceiver;
	}

	private boolean needToLogin() {
		SharedPreferences mySP = getSharedPreferences(Tools.SHARED_PREF_FILE, Activity.MODE_PRIVATE);
		mLogin = mySP.getString(Tools.SHARED_PREF_LOGIN, "");
		if ("".equals(mLogin)) {
			return true;
		} else {
			return false;
		}
	}
}
