package com.steto.diaw.activity;

import roboguice.activity.RoboActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.inject.Inject;
import com.steto.diaw.service.ParseGetEpisodesService;
import com.steto.diaw.tools.Tools;
import com.steto.projectdiaw.R;

@ContentView(R.layout.activity_login)
public class LoginActivity extends RoboActivity {

	@InjectView(R.id.login_button)
	private Button mValidateButton;
	@InjectView(R.id.email_edit_text)
	private EditText mMailText;
	@InjectView(R.id.send_plugin)
	private CheckBox mSendPlugin;
	@InjectView(R.id.login_form_layout)
	private View mEcranLogin;

	@Inject
	private SharedPreferences mSharedPreferences;
	private ResultReceiver mShowReceiver = new ShowResultReceiver();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initializeListeners();
	}

	private void initializeListeners() {
		mValidateButton.setOnClickListener(new OnValidateListener());
	}

	private void getShowsFromNetwork(String login) {
		Intent in = new Intent(this, ParseGetEpisodesService.class);
		in.putExtra(ParseGetEpisodesService.INTENT_LOGIN, login);
		in.putExtra(ParseGetEpisodesService.INTENT_RESULT_RECEIVER, mShowReceiver);
		startService(in);
	}

	private final class OnValidateListener implements View.OnClickListener {

		@Override
		public void onClick(View view) {
			if (!"".equals(mMailText.getText().toString())) {
				mEcranLogin.setVisibility(View.GONE);
				getActionBar().setTitle(R.string.loading);

				String mail = mMailText.getText().toString();
				mSharedPreferences.edit().putString(Tools.SHARED_PREF_LOGIN, mail).commit();
				getShowsFromNetwork(mail);
			} else {
				mMailText.setError(getString(R.string.email_error));
			}
		}
	}

	private final class ShowResultReceiver extends ResultReceiver {

		public ShowResultReceiver() {
			super(new Handler());
		}

		@Override
		protected void onReceiveResult(int resultCode, Bundle resultData) {
			super.onReceiveResult(resultCode, resultData);

			if (resultCode == ParseGetEpisodesService.RESULT_CODE_OK) {
				Intent in = new Intent(LoginActivity.this, EpisodesSeenActivity.class);
				in.putExtra(EpisodesSeenActivity.INTENT_LIST_EPISODE, resultData.getSerializable(ParseGetEpisodesService.RESULT_DATA));
				startActivity(in);
				finish();
			} else if (resultCode == ParseGetEpisodesService.RESULT_CODE_IN_PROGRESS) {
				Toast.makeText(LoginActivity.this, getString(R.string.msg_work_in_progress), Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(LoginActivity.this, getString(R.string.msg_erreur_reseau), Toast.LENGTH_SHORT).show();
			}
		}
	}

}
