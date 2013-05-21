package com.steto.diaw.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;
import android.view.*;
import android.widget.ListView;
import android.widget.Toast;
import com.steto.diaw.adapter.ListEpisodeHomeAdapter;
import com.steto.diaw.model.Episode;
import com.steto.diaw.service.ShowService;
import com.steto.diaw.tools.DatabaseHelper;
import com.steto.diaw.tools.Tools;
import com.steto.projectdiaw.R;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends Activity {

	private static final String TAG = "HomeActivity";
	private List<Episode> mAllEp = new ArrayList<Episode>();
	private ListView mList;
	private ListEpisodeHomeAdapter mAdapter;
	private ResultReceiver mShowResultReceiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_home);

		setProgressBarIndeterminateVisibility(false);
		getActionBar().setTitle("Derniers Episodes Regardés");

		readDatabase();

		mList = (ListView) findViewById(R.id.list_episode);
		mAdapter = new ListEpisodeHomeAdapter(this, mAllEp);
		mList.setAdapter(mAdapter);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		Log.i(TAG, "onNewIntent");

		setProgressBarIndeterminateVisibility(false);
		findViewById(R.id.menu_update).setVisibility(View.VISIBLE);
		readDatabase();
		mAdapter.notifyDataSetChanged();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.episode_list, menu);
		return super.onCreateOptionsMenu(menu);
	}

	/**
	 * Gestion des clics sur la barre d'action
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menu_update:
				findViewById(R.id.menu_update).setVisibility(View.GONE);
				updateListEpisodes();
				return true;

			default:
				return super.onOptionsItemSelected(item);
		}
	}

	private void readDatabase() {
		try {
			mAllEp.clear();
			mAllEp.addAll(DatabaseHelper.getInstance(HomeActivity.this).getEpisodeDao().queryForAll());
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/* Update Data */
	private void updateListEpisodes() {
		Log.i(TAG, "update from menu");
		setProgressBarIndeterminateVisibility(true);
		initShowResultReceiver();
		getBackShows();
	}

	private void getBackShows() {
		SharedPreferences mySP = getSharedPreferences(Tools.SHARED_PREF_FILE, Activity.MODE_PRIVATE);
		String login = mySP.getString(Tools.SHARED_PREF_LOGIN, "");
		Intent in = new Intent(this, ShowService.class);
		in.putExtra(ShowService.INTENT_LOGIN, login);
		in.putExtra(ShowService.INTENT_RESULT_RECEIVER, mShowResultReceiver);
		startService(in);
	}

	private ResultReceiver initShowResultReceiver() {
		if (mShowResultReceiver == null) {
			mShowResultReceiver = new ResultReceiver(null) {

				@Override
				protected void onReceiveResult(int resultCode, Bundle resultData) {
					super.onReceiveResult(resultCode, resultData);
					Log.i(TAG, "onResult");
					if (resultCode == ShowService.RESULT_CODE_OK) {
						startActivity(new Intent(HomeActivity.this, HomeActivity.class));
					} else {
						Toast.makeText(HomeActivity.this, getString(R.string.msg_erreur_reseau), Toast.LENGTH_SHORT).show();
					}
				}
			};
		}
		return mShowResultReceiver;
	}

}
