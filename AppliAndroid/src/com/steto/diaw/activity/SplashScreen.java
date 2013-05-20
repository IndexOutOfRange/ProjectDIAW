package com.steto.diaw.activity;

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
import com.steto.diaw.tools.Tools;
import com.steto.projectdiaw.R;

public class SplashScreen extends Activity {

    public static final String BUNDLE_LIST_EPISODE = "BUNDLE_LIST_EPISODE";


    private String login = "";
    private ResultReceiver showResultReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        if (needToLogin()) {
            Intent in = new Intent(this, LoginActivity.class);
            startActivity(in);
        } else {
            initShowResultReceiver();
            getBackShows();
        }
    }


    private void getBackShows() {
        Intent in = new Intent(this, ShowService.class);
        in.putExtra(ShowService.INTENT_LOGIN, login);
        in.putExtra(ShowService.INTENT_RESULT_RECEIVER, showResultReceiver);
        startService(in);

    }

    private ResultReceiver initShowResultReceiver() {
        if (showResultReceiver == null) {
            showResultReceiver = new ResultReceiver(null) {

                @Override
                protected void onReceiveResult(int resultCode, Bundle resultData) {
                    // TODO Auto-generated method stub
                    super.onReceiveResult(resultCode, resultData);
                    if (resultCode == ShowService.RESULT_CODE_OK) {
                        @SuppressWarnings("unchecked")
                        List<Episode> allEp = (List<Episode>) resultData.get(ShowService.RESULT_DATA);
                        Intent in = new Intent(SplashScreen.this, HomeActivity.class);
                        in.putExtra(BUNDLE_LIST_EPISODE, (Serializable) allEp);
                        startActivity(in);
                        finish();
                    } else {
                        Toast.makeText(SplashScreen.this, getString(R.string.msg_erreur_reseau), Toast.LENGTH_SHORT).show();
                    }
                }
            };

        }
        return showResultReceiver;
    }

    private boolean needToLogin() {
        SharedPreferences mySP = getSharedPreferences(Tools.SHARED_PREF_FILE, 0);
        login = mySP.getString(Tools.SHARED_PREF_LOGIN, "");
        if ("".equals(login)) {
            return true;
        } else {
            return false;
        }
    }
}
