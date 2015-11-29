package com.steto.diaw.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.inject.Inject;
import com.steto.diaw.service.LoginService;
import com.steto.diaw.service.ParseGetEpisodeService;
import com.steto.diaw.service.SigningUpService;
import com.steto.diaw.service.model.AbstractIntentService.ServiceResponseCode;
import com.steto.diaw.tools.Tools;
import com.steto.projectdiaw.R;

import roboguice.activity.RoboActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

@ContentView(R.layout.activity_login)
public class LoginActivity extends RoboActivity {

	@InjectView(R.id.login_button)
	private Button mLoginButton;
	@InjectView(R.id.register_button)
	private Button mRegisterButton;
	@InjectView(R.id.login_edit_text)
	private EditText mLoginText;
    @InjectView(R.id.pass_edit_text)
    private EditText mPassText;
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
		mLoginButton.setOnClickListener(new OnLoginClickListener());
        mRegisterButton.setOnClickListener(new OnRegisterClickListener());
	}

	private void getShowsFromNetwork(String login) {
		Intent in = new Intent(this, ParseGetEpisodeService.class);
		in.putExtra(ParseGetEpisodeService.EXTRA_INPUT_LOGIN, login);
		in.putExtra(ParseGetEpisodeService.EXTRA_INPUT_RESULT_RECEIVER, mShowReceiver);
		startService(in);
	}

    private void validateLoginPass(final String login, final String pass) {
        Intent in = new Intent( this, LoginService.class);
        in.putExtra(LoginService.EXTRA_INPUT_LOGIN, login);
        in.putExtra(LoginService.EXTRA_INPUT_PASS, pass);
        in.putExtra(LoginService.EXTRA_INPUT_RESULT_RECEIVER, new ResultReceiver(new Handler()) {
            @Override
            protected void onReceiveResult(int resultCode, Bundle resultData) {
                super.onReceiveResult(resultCode, resultData);
                if(resultCode == ServiceResponseCode.OK.value) {
                    Toast.makeText(LoginActivity.this, "Successfully logged in as " + login,Toast.LENGTH_LONG).show();
                    mSharedPreferences.edit().putString(Tools.SHARED_PREF_LOGIN, login).commit();
                    getShowsFromNetwork(login);
                } else {
                    mEcranLogin.setVisibility(View.VISIBLE);
                    getActionBar().setTitle(R.string.app_name);
                    Toast.makeText(LoginActivity.this, "Error while logging",Toast.LENGTH_LONG).show();
                }
            }
        });
        startService(in);

    }

    private void registerLoginPass(final String login, final String pass) {
        Intent in = new Intent( this, SigningUpService.class);
        in.putExtra(SigningUpService.EXTRA_INPUT_LOGIN, login);
        in.putExtra(SigningUpService.EXTRA_INPUT_PASS, pass);
        in.putExtra(SigningUpService.EXTRA_INPUT_RESULT_RECEIVER, new ResultReceiver(new Handler()) {
            @Override
            protected void onReceiveResult(int resultCode, Bundle resultData) {
                super.onReceiveResult(resultCode, resultData);
                if(resultCode == ServiceResponseCode.OK.value) {
                    Toast.makeText(LoginActivity.this, "Successfully registred as " + login,Toast.LENGTH_LONG).show();
                    mSharedPreferences.edit().putString(Tools.SHARED_PREF_LOGIN, login).commit();
                    getShowsFromNetwork(login);
                } else {
                    mEcranLogin.setVisibility(View.VISIBLE);
                    getActionBar().setTitle(R.string.app_name);
                    Toast.makeText(LoginActivity.this, "Username " + login + " already taken.",Toast.LENGTH_LONG).show();
                }
            }
        });
        startService(in);

    }
	private final class OnLoginClickListener implements View.OnClickListener {

		@Override
		public void onClick(View view) {
			if (!"".equals(mLoginText.getText().toString())) {
				mEcranLogin.setVisibility(View.GONE);
				getActionBar().setTitle(R.string.loading);

				String login = mLoginText.getText().toString();
                String pass = mLoginText.getText().toString();
                validateLoginPass(login, pass);
			} else {
				mLoginText.setError(getString(R.string.login_error));
			}
		}
	}

    private final class OnRegisterClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            if (!"".equals(mLoginText.getText().toString())) {
                mEcranLogin.setVisibility(View.GONE);
                getActionBar().setTitle(R.string.loading);

                String login = mLoginText.getText().toString();
                String pass = mLoginText.getText().toString();
                registerLoginPass(login, pass);
            } else {
                mLoginText.setError(getString(R.string.login_error));
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

			if (resultCode == ServiceResponseCode.OK.value) {
				startActivity(new Intent(LoginActivity.this, EpisodesSeenActivity.class));
				finish();
			} else if (resultCode == ParseGetEpisodeService.RESULT_CODE_IN_PROGRESS) {
				Toast.makeText(LoginActivity.this, getString(R.string.msg_work_in_progress), Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(LoginActivity.this, getString(R.string.msg_erreur_reseau), Toast.LENGTH_SHORT).show();
			}
		}
	}

}
