package com.steto.diaw.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import com.actionbarsherlock.app.SherlockListActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;
import com.steto.diaw.actionbarcallback.ActionModeCallbackEpisode;
import com.steto.diaw.adapter.ListEpisodeHomeAdapter;
import com.steto.diaw.model.Episode;
import com.steto.diaw.service.ParseDeleteEpisodeService;
import com.steto.diaw.service.ParseGetEpisodesService;
import com.steto.diaw.tools.Tools;
import com.steto.projectdiaw.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends SherlockListActivity {

	public static final String INTENT_LIST_EPISODE = "INTENT_LIST_EPISODE";
	private static final String TAG = "HomeActivity";
	private boolean sUpdateInProgress;
	private List<Episode> mAllEp = new ArrayList<Episode>();
	private ListView mList;
	private ListEpisodeHomeAdapter mAdapter;
	private ResultReceiver mShowResultReceiver;
	private ResultReceiver mDeleteEpisodeResultReceiver;
	private ResultReceiver mRenameEpisodeResultReceiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_home);
		mAllEp = (List<Episode>) getIntent().getExtras().get(INTENT_LIST_EPISODE);

		mList = getListView();
		mAdapter = new ListEpisodeHomeAdapter(this, mAllEp);
		mList.setAdapter(mAdapter);
		setActionModeCallbackOnList();

		initDeleteEpisodeResultReceiver();
		initRenameEpisodeResultReceiver();
	}

	@Override
	protected void onResume() {
		super.onResume();
		invalidateOptionsMenu();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.episode_list, menu);

		menu.getItem(0).setVisible(!sUpdateInProgress);
		setSupportProgressBarIndeterminateVisibility(sUpdateInProgress);

		return super.onCreateOptionsMenu(menu);
	}

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
		sUpdateInProgress = true;
		invalidateOptionsMenu();
		initShowResultReceiver();
		getBackShows();
	}

	private void getBackShows() {
		SharedPreferences mySP = getSharedPreferences(Tools.SHARED_PREF_FILE, Activity.MODE_PRIVATE);
		String login = mySP.getString(Tools.SHARED_PREF_LOGIN, "");
		Intent in = new Intent(this, ParseGetEpisodesService.class);
		in.putExtra(ParseGetEpisodesService.INTENT_LOGIN, login);
		in.putExtra(ParseGetEpisodesService.INTENT_FORCE_UPDATE, true);
		in.putExtra(ParseGetEpisodesService.INTENT_RESULT_RECEIVER, mShowResultReceiver);
		startService(in);
	}

	private void initShowResultReceiver() {
		if (mShowResultReceiver == null) {

			mShowResultReceiver = new ResultReceiver(new Handler()) {

				@Override
				protected void onReceiveResult(int resultCode, Bundle resultData) {
					super.onReceiveResult(resultCode, resultData);
					Log.i(TAG, "onResult");
					if (resultCode == ParseGetEpisodesService.RESULT_CODE_OK) {
						mAllEp.clear();
						mAllEp.addAll((List<Episode>) resultData.get(ParseGetEpisodesService.RESULT_DATA));
						processUpdateListEpisodes();
					} else {
						Toast.makeText(HomeActivity.this, getString(R.string.msg_erreur_reseau), Toast.LENGTH_SHORT).show();
					}
				}
			};
		}
	}

	private void processUpdateListEpisodes() {
		sUpdateInProgress = false;
		invalidateOptionsMenu();
		mAdapter.notifyDataSetChanged();
	}

	private void setActionModeCallbackOnList() {
		final ActionModeCallbackEpisode actionModeCallback = new ActionModeCallbackEpisode(HomeActivity.this, mAdapter);
		mList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				if (actionModeCallback.getActionMode() != null) {
					// if already in action mode - do nothing
					return false;
				}
				// set checked selected item and enter multi selection mode
				mAdapter.setChecked(arg2, true);
				HomeActivity.this.startActionMode(actionModeCallback);
				actionModeCallback.getActionMode().invalidate();
				return true;
			}
		});

		mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				if (actionModeCallback.getActionMode() != null) {
					// if action mode, toggle checked state of item
					mAdapter.toggleChecked(arg2);
					actionModeCallback.getActionMode().invalidate();
				} else {
					// do whatever you should on item click
				}
			}
		});
	}

	public void deleteEpisodes() {
		List<Episode> episodesToDelete = mAdapter.getCheckedItems();

		sUpdateInProgress = true;
		invalidateOptionsMenu();

		Intent intent = new Intent(this, ParseDeleteEpisodeService.class);
		intent.putExtra(ParseDeleteEpisodeService.INTENT_OBJECTS_TO_DELETE, (Serializable) episodesToDelete);
		intent.putExtra(ParseDeleteEpisodeService.INTENT_RESULT_RECEIVER, mDeleteEpisodeResultReceiver);
		startService(intent);
	}


	public void renameEpisode(Episode episode) {
		// TODO pop up

//		Intent intent = new Intent(this, ParseUpdateEpisodeService.class);
//		intent.putExtra(ParseUpdateEpisodeService.INTENT_OBJECTS_TO_DELETE, episode.getObjectId());
//		intent.putExtra(ParseUpdateEpisodeService.INTENT_RESULT_RECEIVER, mDeleteEpisodeResultReceiver);
//		intent.putExtra(ParseUpdateEpisodeService.INTENT_KEY, "showName");
//		intent.putExtra(ParseUpdateEpisodeService.INTENT_VALUE, episode.getShowName());
//		startService(intent);
	}

	private void initRenameEpisodeResultReceiver() {
		if (mRenameEpisodeResultReceiver == null) {

			mRenameEpisodeResultReceiver = new ResultReceiver(new Handler()) {

				@Override
				protected void onReceiveResult(int resultCode, Bundle resultData) {
					super.onReceiveResult(resultCode, resultData);
					Log.i(TAG, "onResult");
					if (resultCode == ParseGetEpisodesService.RESULT_CODE_OK) {
						mAdapter.notifyDataSetChanged();
					} else {
						Toast.makeText(HomeActivity.this, "Unable to get result from service", Toast.LENGTH_SHORT).show();
					}
				}
			};
		}
	}

	private void initDeleteEpisodeResultReceiver() {
		if (mDeleteEpisodeResultReceiver == null) {

			mDeleteEpisodeResultReceiver = new ResultReceiver(new Handler()) {

				@Override
				protected void onReceiveResult(int resultCode, Bundle resultData) {
					super.onReceiveResult(resultCode, resultData);
					sUpdateInProgress = false;
					invalidateOptionsMenu();

					if (resultCode == ParseGetEpisodesService.RESULT_CODE_OK) {
						List<Episode> episodesNotDeleted = (List<Episode>) resultData.getSerializable(ParseDeleteEpisodeService.INTENT_OBJECTS_NOT_DELETED);

						mAllEp.clear();
						mAllEp.addAll((List<Episode>) resultData.get(ParseDeleteEpisodeService.RESULT_DATA));
						mAdapter.notifyDataSetChanged();

						if (episodesNotDeleted != null && !episodesNotDeleted.isEmpty()) {
							StringBuilder sb = new StringBuilder();
							sb.append("Erreur durant la suppression pour ");
							if (episodesNotDeleted.size() == 1) {
								sb.append("l'épisode :");
							} else {
								sb.append("les épisodes :");
							}
							for (Episode episode : episodesNotDeleted) {
								sb.append("\n").append(episode.getShowName()).append(" ")
										.append(episode.getSeasonNumber()).append("x").append(episode.getEpisodeNumber());
							}
							Toast.makeText(HomeActivity.this, sb.toString(), Toast.LENGTH_LONG).show();
						} else {
							Toast.makeText(HomeActivity.this, "Suppression réalisée", Toast.LENGTH_LONG).show();
						}
					} else {
						Toast.makeText(HomeActivity.this, "Unable to get result from service", Toast.LENGTH_SHORT).show();
					}
				}
			};
		}
	}
}