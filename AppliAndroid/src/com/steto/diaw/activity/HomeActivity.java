package com.steto.diaw.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
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
import com.steto.diaw.service.ParseService;
import com.steto.diaw.tools.Tools;
import com.steto.projectdiaw.R;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends SherlockListActivity {

	private static final String TAG = "HomeActivity";
	private static final String INTENT_UPDATE = "IntentUpdate";
    public static final String INTENT_LIST_EPISODE = "INTENT_LIST_EPISODE";

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
        mAllEp = (List<Episode>)getIntent().getExtras().get(INTENT_LIST_EPISODE);

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



	/* Update TVDBContainerData */
	private void startUpdateListEpisodes() {
		HomeActivity.sUpdateInProgress = true;
		invalidateOptionsMenu();
		initShowResultReceiver();
		getBackShows();
	}

	private void getBackShows() {
		SharedPreferences mySP = getSharedPreferences(Tools.SHARED_PREF_FILE, Activity.MODE_PRIVATE);
		String login = mySP.getString(Tools.SHARED_PREF_LOGIN, "");
		Intent in = new Intent(this, ParseService.class);
		in.putExtra(ParseService.INTENT_LOGIN, login);
		in.putExtra(ParseService.INTENT_RESULT_RECEIVER, mShowResultReceiver);
		startService(in);
	}

	private void initShowResultReceiver() {
		if (mShowResultReceiver == null) {

			mShowResultReceiver = new ResultReceiver(new Handler()) {

				@Override
				protected void onReceiveResult(int resultCode, Bundle resultData) {
					super.onReceiveResult(resultCode, resultData);
					Log.i(TAG, "onResult");
					if (resultCode == ParseService.RESULT_CODE_OK) {
                        mAllEp.clear();
                        mAllEp.addAll((List<Episode>)resultData.get(ParseService.RESULT_DATA));
                        processUpdateListEpisodes();
					} else {
						Toast.makeText(HomeActivity.this, getString(R.string.msg_erreur_reseau), Toast.LENGTH_SHORT).show();
					}
				}
			};
		}
	}

	private void processUpdateListEpisodes() {
		HomeActivity.sUpdateInProgress = false;
		invalidateOptionsMenu();
		mAdapter.notifyDataSetChanged();
	}

}