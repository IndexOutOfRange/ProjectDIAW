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
import com.actionbarsherlock.view.*;
import com.steto.diaw.adapter.ListEpisodeHomeAdapter;
import com.steto.diaw.model.Episode;
import com.steto.diaw.service.ParseService;
import com.steto.diaw.tools.Tools;
import com.steto.projectdiaw.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class HomeActivity extends SherlockListActivity {

	public static final String INTENT_LIST_EPISODE = "INTENT_LIST_EPISODE";
	private static final String TAG = "HomeActivity";
	private static final String INTENT_UPDATE = "IntentUpdate";
	private boolean sUpdateInProgress;
	private List<Episode> mAllEp = new ArrayList<Episode>();
	private ListView mList;
	private ListEpisodeHomeAdapter mAdapter;
	private ResultReceiver mShowResultReceiver;
	private ActionMode actionMode;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_home);
		mAllEp = (List<Episode>) getIntent().getExtras().get(INTENT_LIST_EPISODE);

		mList = getListView();

		mList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				if (actionMode != null) {
					// if already in action mode - do nothing
					return false;
				}
				// set checked selected item and enter multi selection mode
				mAdapter.setChecked(arg2, true);
				HomeActivity.this.startActionMode(new ActionModeCallback());
				actionMode.invalidate();
				return true;
			}
		});

		mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
			{
				if (actionMode != null) {
					// if action mode, toggle checked state of item
					mAdapter.toggleChecked(arg2);
					actionMode.invalidate();
				} else {
					// do whatever you should on item click
				}
			}
		});

		mAdapter = new ListEpisodeHomeAdapter(this, mAllEp);
		mList.setAdapter(mAdapter);
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
		Intent in = new Intent(this, ParseService.class);
		in.putExtra(ParseService.INTENT_LOGIN, login);
		in.putExtra(ParseService.INTENT_FORCE_UPDATE, true);
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
		sUpdateInProgress = false;
		invalidateOptionsMenu();
		mAdapter.notifyDataSetChanged();
	}

	private final class ActionModeCallback implements ActionMode.Callback {

		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			mAdapter.enterMultiMode();
			// save global action mode
			actionMode = mode;
			return true;
		}

		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			// remove previous items
			menu.clear();
			final int checked = mAdapter.getCheckedItemCount();
			// update title with number of checked items
			mode.setTitle(checked + " " + getResources().getQuantityString(R.plurals.nb_items_selected, checked));
			switch (checked) {
				case 0:
					// if nothing checked - exit action mode
					mode.finish();
					return true;
				case 1:
					// all items - rename + delete
					HomeActivity.this.getSupportMenuInflater().inflate(R.menu.context_menu, menu);
					return true;
				default:
					HomeActivity.this.getSupportMenuInflater().inflate(R.menu.context_menu, menu);
					// remove rename option - because we have more than one selected
					menu.removeItem(R.id.context_menu_rename);
					return true;
			}
		}

		@Override
		public boolean onActionItemClicked(ActionMode mode, com.actionbarsherlock.view.MenuItem item) {
			switch (item.getItemId()) {
				case R.id.context_menu_rename:
					// TODO proposer dialog pour renommer l'episode
					Log.d(TAG, "A renommer : " + mAdapter.getFirstCheckedItem().getMCustomId());
					return true;

				case R.id.context_menu_delete:
					Set<Integer> checked = mAdapter.getCheckedItems();
					// iterate through selected items and delete them
					for (Integer ci : checked) {
						// TODO appel WS pour supprimer les donnees
						Log.d(TAG, "A supprimer : " + mAllEp.get(ci).getMCustomId());
					}
					return true;
				default:
					return false;
			}
		}

		@Override
		public void onDestroyActionMode(ActionMode mode) {
			mAdapter.exitMultiMode();
			actionMode = null;
		}
	}
}