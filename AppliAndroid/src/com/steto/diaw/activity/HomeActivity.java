package com.steto.diaw.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;
import com.actionbarsherlock.app.SherlockListActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;
import com.steto.diaw.adapter.ListEpisodeHomeAdapter;
import com.steto.diaw.model.Episode;
import com.steto.diaw.service.ShowService;
import com.steto.diaw.tools.DatabaseHelper;
import com.steto.diaw.tools.Tools;
import com.steto.projectdiaw.R;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends SherlockListActivity {

	private static final String TAG = "HomeActivity";
	private static final String INTENT_UPDATE = "IntentUpdate";

	private static boolean sUpdateInProgress;
	private List<Episode> mAllEp = new ArrayList<Episode>();
	private ListView mList;
	private ListEpisodeHomeAdapter mAdapter;
	private ResultReceiver mShowResultReceiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_home);

		readDatabase();

		mList = getListView();
		mAdapter = new ListEpisodeHomeAdapter(this, mAllEp);
		mList.setAdapter(mAdapter);
	}

	@Override
	protected void onResume() {
		super.onResume();
		invalidateOptionsMenu();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		boolean update = intent.getExtras().containsKey(INTENT_UPDATE);
		if (update) {
			processUpdateListEpisodes();
		}
		super.onNewIntent(intent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.episode_list, menu);

		menu.getItem(0).setVisible(!HomeActivity.sUpdateInProgress);
		setSupportProgressBarIndeterminateVisibility(HomeActivity.sUpdateInProgress);

		return super.onCreateOptionsMenu(menu);
	}

	/**
	 * Gestion des clics sur la barre d'action
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menu_update:
				startUpdateListEpisodes();
				return true;
			case R.id.menu_go_to_shows_list:
				startActivity(new Intent(this, ListShowsActivity.class));
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
	private void startUpdateListEpisodes() {
		HomeActivity.sUpdateInProgress = true;
		invalidateOptionsMenu();
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
						Intent intent = new Intent(HomeActivity.this, HomeActivity.class);
						intent.putExtra(INTENT_UPDATE, true);
						startActivity(intent);
					} else {
						Toast.makeText(HomeActivity.this, getString(R.string.msg_erreur_reseau), Toast.LENGTH_SHORT).show();
					}
				}
			};
		}
		return mShowResultReceiver;
	}

	private void processUpdateListEpisodes() {
		HomeActivity.sUpdateInProgress = false;
		invalidateOptionsMenu();
		readDatabase();
		mAdapter.notifyDataSetChanged();
	}

}