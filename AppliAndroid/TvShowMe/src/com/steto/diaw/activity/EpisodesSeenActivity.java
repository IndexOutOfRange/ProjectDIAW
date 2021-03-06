package com.steto.diaw.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.inject.Inject;
import com.steto.diaw.SyncUtils;
import com.steto.diaw.actionbarcallback.ActionModeCallback;
import com.steto.diaw.activity.model.DrawerActivity;
import com.steto.diaw.adapter.ListEpisodeAdapter;
import com.steto.diaw.dao.DatabaseHelper;
import com.steto.diaw.dao.EpisodeDao;
import com.steto.diaw.model.Episode;
import com.steto.diaw.service.ParseDeleteEpisodeService;
import com.steto.diaw.service.ParseGetEpisodeService;
import com.steto.diaw.service.ParseUpdateEpisodeService;
import com.steto.diaw.service.model.AbstractIntentService.ServiceResponseCode;
import com.steto.diaw.tools.Tools;
import com.steto.projectdiaw.R;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import roboguice.util.Ln;

public class EpisodesSeenActivity extends DrawerActivity {

	public static final String INTENT_LIST_EPISODE = "INTENT_LIST_EPISODE";

	private boolean mUpdateInProgress;
	private List<Episode> mAllEp = new ArrayList<Episode>();
	private ListEpisodeAdapter mAdapter;

	@Inject
	private DatabaseHelper mDatabaseHelper;
	@Inject
	private SharedPreferences mSharedPreferences;

	private ListView mList;

	private ResultReceiver mShowResultReceiver = new ShowResultReceiver();
	private ResultReceiver mDeleteEpisodeResultReceiver = new DeleteEpisodeResultReceiver();
	private ResultReceiver mRenameEpisodeResultReceiver = new RenameResultReceiver();
	private ActionModeCallback mActionMode;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		super.onCreate(savedInstanceState);

		mList = (ListView) mContentView.findViewById(R.id.list);
		mAdapter = new ListEpisodeAdapter(this, mAllEp);
		initializeListView();
		mActionMode = new ActionModeCallback(this, mAdapter, new OnRenameEpisodeClickListener(), new OnDeleteEpisodeClickListener());

		String login = mSharedPreferences.getString(Tools.SHARED_PREF_LOGIN, "");
		SyncUtils.CreateSyncAccount(this, login);
	}

	@Override
	protected void onResume() {
		super.onResume();
		invalidateOptionsMenu();

		new LoadEpisodesFromDatabaseTask().execute();
	}

	@Override
	protected void onPause() {
		super.onPause();
		mAdapter.exitMultiMode();
		if (mActionMode != null && mActionMode.getActionMode() != null) {
			mActionMode.getActionMode().invalidate();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.episode_list, menu);

		menu.getItem(0).setVisible(!mUpdateInProgress);
		setProgressBarIndeterminateVisibility(mUpdateInProgress);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menu_update:
				startUpdateListEpisodes();
				return true;
			case R.id.menu_database:
				startActivity(new Intent(this, DatabaseActivity.class));
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected int getLayoutContentFrame() {
		return R.layout.activity_episodes_seen;
	}

	@Override
	protected int getSelectedItem() {
		return DRAWER_DERNIERS_EPISODES;
	}

	@Override
	protected void manageIconsInActionBar(Menu menu, boolean drawerOpen) {
		if (!drawerOpen) {
			menu.findItem(R.id.menu_update).setVisible(!mUpdateInProgress);
			setProgressBarIndeterminateVisibility(mUpdateInProgress);
		} else {
			menu.findItem(R.id.menu_update).setVisible(false);
			setProgressBarIndeterminateVisibility(false);
		}
		menu.findItem(R.id.menu_database).setVisible(!drawerOpen);
	}

	@Override
	protected void manageDrawerItemClick(int position) {
		if (position == getSelectedItem()) {
			return;
		} else if (position == DRAWER_MES_SERIES) {
			startActivity(new Intent(this, ListShowsActivity.class));
			overridePendingTransition(0, 0);
		} else if (position == DRAWER_PLANNING_SERIES) {
			startActivity(new Intent(this, PlanningActivity.class));
			overridePendingTransition(0, 0);
		}	}

	private void initializeListView() {
		mList.setAdapter(mAdapter);
		mList.setOnItemLongClickListener(new OnEpisodeItemLongClickListener());
		mList.setOnItemClickListener(new OnEpisodeItemClickListener());
	}

	private void startUpdateListEpisodes() {
		mUpdateInProgress = true;
		invalidateOptionsMenu();
		getShowsFromNetwork();
	}

	private void getShowsFromNetwork() {
		String login = mSharedPreferences.getString(Tools.SHARED_PREF_LOGIN, "");
		Intent intent = new Intent(this, ParseGetEpisodeService.class);
		intent.putExtra(ParseGetEpisodeService.EXTRA_INPUT_LOGIN, login);
		intent.putExtra(ParseGetEpisodeService.EXTRA_INPUT_FORCE_UPDATE, true);
		intent.putExtra(ParseGetEpisodeService.EXTRA_INPUT_RESULT_RECEIVER, mShowResultReceiver);
		startService(intent);
	}

	private void processUpdateListEpisodes() {
		mUpdateInProgress = false;
		invalidateOptionsMenu();
		mAdapter.notifyDataSetChanged();
	}

	public void deleteEpisodes() {
		List<Episode> episodesToDelete = mAdapter.getCheckedItems();

		mUpdateInProgress = true;
		invalidateOptionsMenu();

		Intent intent = new Intent(this, ParseDeleteEpisodeService.class);
		intent.putExtra(ParseDeleteEpisodeService.EXTRA_INPUT_OBJECTS_TO_DELETE, (Serializable) episodesToDelete);
		intent.putExtra(ParseDeleteEpisodeService.EXTRA_INPUT_RESULT_RECEIVER, mDeleteEpisodeResultReceiver);
		startService(intent);
	}

	public void renameEpisode(final Episode episode) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		View viewInDialog = getLayoutInflater().inflate(R.layout.dialog_rename, null);
		final EditText nameShowEditText = (EditText) viewInDialog.findViewById(R.id.dialog_rename_episode_name);
		nameShowEditText.setText(episode.getShowName());

		builder.setView(viewInDialog)
				// Add action buttons
				.setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int id) {
						mUpdateInProgress = true;
						invalidateOptionsMenu();

						// service
						String newName = nameShowEditText.getText().toString();
						Intent intent = new Intent(EpisodesSeenActivity.this, ParseUpdateEpisodeService.class);
						List<Episode> episodeToRename = new ArrayList<Episode>();
						episodeToRename.add(episode);
						intent.putExtra(ParseUpdateEpisodeService.EXTRA_INPUT_EPISODES_TO_UPDATE, (Serializable) episodeToRename);
						intent.putExtra(ParseUpdateEpisodeService.EXTRA_INPUT_KEY, Episode.COLUMN_SHOWNAME);
						intent.putExtra(ParseUpdateEpisodeService.EXTRA_INPUT_VALUE, newName);
						intent.putExtra(ParseUpdateEpisodeService.EXTRA_INPUT_RESULT_RECEIVER, mRenameEpisodeResultReceiver);
						startService(intent);
					}
				})
				.setNegativeButton(R.string.btn_annuler, new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int id) {
						dialog.dismiss();
					}
				})
				.setTitle(R.string.rename_episode);
		AlertDialog dialog = builder.create();
		dialog.show();
	}

	private final class RenameResultReceiver extends ResultReceiver {

		private RenameResultReceiver() {
			super(new Handler());
		}

		@SuppressWarnings("unchecked")
		@Override
		protected void onReceiveResult(int resultCode, Bundle resultData) {
			super.onReceiveResult(resultCode, resultData);
			mUpdateInProgress = false;
			invalidateOptionsMenu();

			if (resultCode == ServiceResponseCode.OK.value) {
				mAllEp.clear();
				mAllEp.addAll((List<Episode>) resultData.get(ParseUpdateEpisodeService.EXTRA_OUTPUT_RESULT_DATA));
				mAdapter.notifyDataSetChanged();
				Toast.makeText(EpisodesSeenActivity.this, "Renommage réalisée", Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(EpisodesSeenActivity.this, "Unable to get result from service " + resultCode, Toast.LENGTH_SHORT).show();
			}
		}
	}

	private final class ShowResultReceiver extends ResultReceiver {

		private ShowResultReceiver() {
			super(new Handler());
		}

		@SuppressWarnings("unchecked")
		@Override
		protected void onReceiveResult(int resultCode, Bundle resultData) {
			super.onReceiveResult(resultCode, resultData);
			Ln.i("onResult");
			if (resultCode == ServiceResponseCode.OK.value) {
				List<Episode> episodes = (List<Episode>) resultData.get(ParseGetEpisodeService.EXTRA_OUTPUT_EPISODE_LIST);
				if (episodes.size() != 0) {
					mAllEp.clear();
					mAllEp.addAll(episodes);
				}
				processUpdateListEpisodes();
			} else if (resultCode == ParseGetEpisodeService.RESULT_CODE_IN_PROGRESS) {
				Toast.makeText(EpisodesSeenActivity.this, getString(R.string.msg_work_in_progress), Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(EpisodesSeenActivity.this, getString(R.string.msg_erreur_reseau), Toast.LENGTH_SHORT).show();
			}
		}
	}

	private final class DeleteEpisodeResultReceiver extends ResultReceiver {

		private DeleteEpisodeResultReceiver() {
			super(new Handler());
		}

		@SuppressWarnings("unchecked")
		@Override
		protected void onReceiveResult(int resultCode, Bundle resultData) {
			super.onReceiveResult(resultCode, resultData);
			mUpdateInProgress = false;
			invalidateOptionsMenu();

			if (resultCode == ServiceResponseCode.OK.value) {
				List<Episode> episodesNotDeleted = (List<Episode>) resultData.getSerializable(ParseDeleteEpisodeService.EXTRA_OUTPUT_OBJECTS_NOT_DELETED);

				mAllEp.clear();
				mAllEp.addAll((List<Episode>) resultData.get(ParseDeleteEpisodeService.EXTRA_OUTPUT_RESULT_DATA));
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
					Toast.makeText(EpisodesSeenActivity.this, sb.toString(), Toast.LENGTH_LONG).show();
				} else {
					Toast.makeText(EpisodesSeenActivity.this, "Suppression réalisée", Toast.LENGTH_LONG).show();
				}
			} else {
				Toast.makeText(EpisodesSeenActivity.this, "Unable to get result from service " + resultCode, Toast.LENGTH_SHORT).show();
			}
		}
	}

	private class OnRenameEpisodeClickListener implements ActionModeCallback.OnRenameClickListener {

		@Override
		public void onRenameClicked() {
			Episode ep = mAdapter.getFirstCheckedItem();
			renameEpisode(ep);
		}
	}

	private class OnDeleteEpisodeClickListener implements ActionModeCallback.OnDeleteClickListener {

		@Override
		public void onDeleteClicked() {
			deleteEpisodes();
		}
	}

	private class OnEpisodeItemLongClickListener implements AdapterView.OnItemLongClickListener {

		@Override
		public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			if (mActionMode.getActionMode() != null) {
				// if already in action mode - do nothing
				return false;
			}
			// set checked selected item and enter multi selection mode
			mAdapter.setChecked(arg2, true);
			EpisodesSeenActivity.this.startActionMode(mActionMode);
			mActionMode.getActionMode().invalidate();
			return true;
		}
	}

	private class OnEpisodeItemClickListener implements AdapterView.OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			if (mActionMode.getActionMode() != null) {
				// if action mode, toggle checked state of item
				mAdapter.toggleChecked(arg2);
				mActionMode.getActionMode().invalidate();
			} else {
				Intent intent = new Intent(EpisodesSeenActivity.this, ShowDetailActivity.class);
				intent.putExtra(ShowDetailActivity.EXTRA_SHOW_NAME, mAllEp.get(arg2).getShowName());
				startActivity(intent);
			}
		}
	}

	private class LoadEpisodesFromDatabaseTask extends AsyncTask<Void, Integer, Void> {

		private List<Episode> mEpisodeListFromDataBase;

		@Override
		protected void onPreExecute() {
			mUpdateInProgress = true;
			invalidateOptionsMenu();
		}

		@Override
		protected Void doInBackground(Void... voids) {
			mEpisodeListFromDataBase = new ArrayList<Episode>();
			try {
				mEpisodeListFromDataBase = ((EpisodeDao) mDatabaseHelper.getDao(Episode.class)).queryForAllSeen();
			} catch (SQLException e) {
				Ln.e(e);
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			mAllEp.clear();
			mAllEp.addAll(mEpisodeListFromDataBase);
			mAdapter.notifyDataSetChanged();
			mUpdateInProgress = false;
			invalidateOptionsMenu();
		}
	}
}