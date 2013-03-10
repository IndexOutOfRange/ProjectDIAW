package com.steto.projectdiaw;

import java.io.Serializable;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.widget.Toast;

import com.steto.diaw.model.Episode;
import com.steto.diaw.service.ShowService;

public class SplashScreen extends Activity {

	private static final String SHARED_PREF_FILE = "DIAWPrefFile";
	private static final String SHARED_PREF_LOGIN = "SharedPrefLogin";
	private static final String SHARED_PREF_PASS = "SharedPrefPass";
	public static final String BUNDLE_LIST_EPISODE = "BUNDLE_LIST_EPISODE";

	private String login = "";
	private String pass = "";
	private ResultReceiver showResultReceiver;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash_screen);
		initShowResultReceiver();
		if( false ) { //needToLogin()
			Intent in = new Intent(this, LoginActivity.class);
			startActivity(in);
		} else {
			getBackShows();
		}
	}

	private void getBackShows() {
		Intent in = new Intent(this, ShowService.class);
		in.putExtra(ShowService.INTENT_LOGIN, login);
		in.putExtra(ShowService.INTENT_PASS, pass);
		in.putExtra(ShowService.INTENT_RESULT_RECEIVER, showResultReceiver);
		startService(in);
		
	}

	private ResultReceiver initShowResultReceiver() {
		if( showResultReceiver == null ) {
			showResultReceiver = new ResultReceiver(null) {
				
				@Override
				protected void onReceiveResult(int resultCode, Bundle resultData) {
					// TODO Auto-generated method stub
					super.onReceiveResult(resultCode, resultData);
					if( resultCode == ShowService.RESULT_CODE_OK ) {
						@SuppressWarnings("unchecked")
						List<Episode> allEp = (List<Episode>) resultData.get(ShowService.RESULT_DATA);
						Intent in = new Intent(SplashScreen.this, AcceuilActivity.class);
						in.putExtra(BUNDLE_LIST_EPISODE, (Serializable)allEp);
						startActivity(in);
					} else {
						Toast.makeText(SplashScreen.this, "Unable to get response from server", Toast.LENGTH_SHORT).show();
					}
				}
			};
					
		}
		return showResultReceiver;
	}
	
	private boolean needToLogin() {
		SharedPreferences mySP = getSharedPreferences(SHARED_PREF_FILE, 0);
		login = mySP.getString(SHARED_PREF_LOGIN, "");
		if( "".equals(login)) {
			return true;
		} else {
			pass = mySP.getString(SHARED_PREF_PASS, "");
		}
		return true;
	}
}
