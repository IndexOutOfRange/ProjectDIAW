package com.steto.diaw.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.steto.diaw.service.ShowService;
import com.steto.diaw.tools.Tools;
import com.steto.projectdiaw.R;

public class LoginActivity extends Activity {

	private Button mValidateButton;
	private EditText mMailText;
	private CheckBox mSendPlugin;
	private ResultReceiver showReceiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		getActionBar().setTitle(R.string.login_title);
		
		initLayoutFields();
		initListeners();
	}

	private void initListeners() {
		mValidateButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

				if (!"".equals(mMailText.getText().toString())) {
					findViewById(R.id.login_form_layout).setVisibility(View.GONE);
					getActionBar().setTitle(R.string.loading);

					initShowResultReceiver();
					String mail = mMailText.getText().toString();

					SharedPreferences mySP = getSharedPreferences(Tools.SHARED_PREF_FILE, Activity.MODE_PRIVATE);
					SharedPreferences.Editor myEditor = mySP.edit();
					myEditor.putString(Tools.SHARED_PREF_LOGIN, mail);
					myEditor.commit();
					getBackShows(mail);
				} else {
					mMailText.setError(getString(R.string.email_error));
				}
			}
		});
	}

	private void initLayoutFields() {
		mMailText = (EditText) findViewById(R.id.email_edit_text);
		mSendPlugin = (CheckBox) findViewById(R.id.send_plugin);
		mValidateButton = (Button) findViewById(R.id.login_button);
	}

	private void getBackShows(String login) {
		Intent in = new Intent(this, ShowService.class);
		in.putExtra(ShowService.INTENT_LOGIN, login);
		in.putExtra(ShowService.INTENT_RESULT_RECEIVER, showReceiver);
		startService(in);

	}

	private ResultReceiver initShowResultReceiver() {
		if (showReceiver == null) {
			showReceiver = new ResultReceiver(null) {

				@Override
				protected void onReceiveResult(int resultCode, Bundle resultData) {
					super.onReceiveResult(resultCode, resultData);
					if (resultCode == ShowService.RESULT_CODE_OK) {
						startActivity(new Intent(LoginActivity.this, HomeActivity.class));
						finish();

					} else {
						Toast.makeText(LoginActivity.this, getString(R.string.msg_erreur_reseau), Toast.LENGTH_SHORT).show();
					}
				}
			};

		}
		return showReceiver;
	}

}
