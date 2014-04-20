package com.steto.diaw.activity;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.inject.Inject;
import com.steto.diaw.actionbarcallback.ActionModeCallback;
import com.steto.diaw.activity.model.DrawerActivity;
import com.steto.diaw.adapter.ListShowAdapter;
import com.steto.diaw.dao.DatabaseHelper;
import com.steto.diaw.dao.EpisodeDao;
import com.steto.diaw.dao.ShowDao;
import com.steto.diaw.model.Episode;
import com.steto.diaw.model.Show;
import com.steto.diaw.service.ParseUpdateEpisodeService;
import com.steto.diaw.service.model.AbstractIntentService.ServiceResponseCode;
import com.steto.projectdiaw.R;

public class ListShowsActivity extends DrawerActivity {

	private List<Show> mAllShows = new ArrayList<Show>();
	private List<Show> mShowsVisible = new ArrayList<Show>();
	private ListShowAdapter mAdapter;

	@Inject
	private DatabaseHelper mDatabaseHelper;

	private ListView mList;
	private TextView mTvSummary;
	private String mQuery;

	private ActionModeCallback mActionMode;
	private ResultReceiver mRenameEpisodeResultReceiver;
	private boolean mUpdateInProgress = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		super.onCreate(savedInstanceState);
		setProgressBarIndeterminateVisibility(mUpdateInProgress);
		getActionBar().setDisplayHomeAsUpEnabled(true);

		mAdapter = new ListShowAdapter(ListShowsActivity.this, mShowsVisible);

		mTvSummary = (TextView) mContentView.findViewById(R.id.summary);
		initializeListView();
		setActionModeCallbackOnList();
		initializeRenameResultReceiver();

		handleIntent(getIntent());
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		handleIntent(intent);
	}

	@Override
	protected void onResume() {
		super.onResume();
		new LoadShowTask().execute();
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
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(0, 0);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		setProgressBarIndeterminateVisibility(mUpdateInProgress);

		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.shows_list, menu);

		// Associate searchable configuration with the SearchView
		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
		searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	protected int getLayoutContentFrame() {
		return R.layout.activity_list_shows;
	}

	@Override
	protected int getSelectedItem() {
		return DRAWER_MES_SERIES;
	}

	@Override
	protected void manageIconsInActionBar(Menu menu, boolean drawerOpen) {
		menu.findItem(R.id.menu_search).setVisible(!drawerOpen);
	}

	@Override
	protected void manageDrawerItemClick(int position) {
		if (position == getSelectedItem()) {
			return;
		}
		if (position == DRAWER_DERNIERS_EPISODES) {
			startActivity(new Intent(this, EpisodesSeenActivity.class));
			overridePendingTransition(0, 0);
		}
	}

	private void handleIntent(Intent intent) {
		mQuery = null;
		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			mQuery = intent.getStringExtra(SearchManager.QUERY);
		}
	}

	private void initializeListView() {
		mList = (ListView) mContentView.findViewById(android.R.id.list);
		mList.setAdapter(mAdapter);
		mList.setOnItemClickListener(new OnShowClickListener());
		mList.setOnItemLongClickListener(new OnShowItemLongClickListener());
	}

	private void initializeRenameResultReceiver() {
		if (mRenameEpisodeResultReceiver == null) {
			mRenameEpisodeResultReceiver = new ResultReceiver(new Handler()) {

				@Override
				protected void onReceiveResult(int resultCode, Bundle resultData) {
					super.onReceiveResult(resultCode, resultData);
					mUpdateInProgress = false;
					invalidateOptionsMenu();

					if (resultCode == ServiceResponseCode.OK.value) {
						new LoadShowTask().execute();
						Toast.makeText(ListShowsActivity.this, "Renommage réalisée", Toast.LENGTH_LONG).show();
					} else {
						Toast.makeText(ListShowsActivity.this, "Unable to get result from service " + resultCode, Toast.LENGTH_SHORT).show();
					}
				}
			};
		}
	}

	public void renameShow(final Show show) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		View viewInDialog = getLayoutInflater().inflate(R.layout.dialog_rename, null);
		final EditText nameShowEditText = (EditText) viewInDialog.findViewById(R.id.dialog_rename_episode_name);
		nameShowEditText.setText(show.getShowName());

		builder.setView(viewInDialog)
				// Add action buttons
				.setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int id) {
						mUpdateInProgress = true;
						invalidateOptionsMenu();

						try {
							ShowDao showDao = mDatabaseHelper.getDao(Show.class);
							List<Episode> allEpisode = showDao.getEpisodeFromShow(show);
							// service
							String newName = nameShowEditText.getText().toString();
							Intent intent = new Intent(ListShowsActivity.this, ParseUpdateEpisodeService.class);
							intent.putExtra(ParseUpdateEpisodeService.EXTRA_INPUT_EPISODES_TO_UPDATE, (Serializable) allEpisode);
							intent.putExtra(ParseUpdateEpisodeService.EXTRA_INPUT_KEY, Episode.COLUMN_SHOWNAME);
							intent.putExtra(ParseUpdateEpisodeService.EXTRA_INPUT_VALUE, newName);
							intent.putExtra(ParseUpdateEpisodeService.EXTRA_INPUT_RESULT_RECEIVER, mRenameEpisodeResultReceiver);
							startService(intent);
						} catch (SQLException e) {
							e.printStackTrace();
						}

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

	private void setActionModeCallbackOnList() {
		mActionMode = new ActionModeCallback(this, mAdapter, new OnRenameShowClickListener(), new OnDeleteShowClickListener());
	}

	private final class OnShowClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> view, View v, int position, long id) {
			if (mActionMode.getActionMode() != null) {
				// if action mode, toggle checked state of item
				mAdapter.toggleChecked(position);
				mActionMode.getActionMode().invalidate();
			} else {
				Intent intentDetail = new Intent(ListShowsActivity.this, ShowDetailActivity.class);
				intentDetail.putExtra(ShowDetailActivity.EXTRA_SHOW, (Show) mAdapter.getItem((int) position));
				startActivity(intentDetail);
			}
		}
	}

	private class OnShowItemLongClickListener implements AdapterView.OnItemLongClickListener {

		@Override
		public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			if (mActionMode.getActionMode() != null) {
				// if already in action mode - do nothing
				return false;
			}
			// set checked selected item and enter multi selection mode
			mAdapter.setChecked(arg2, true);
			ListShowsActivity.this.startActionMode(mActionMode);
			mActionMode.getActionMode().invalidate();
			return true;
		}
	}

	private class OnRenameShowClickListener implements ActionModeCallback.OnRenameClickListener {

		@Override
		public void onRenameClicked() {
			Show show = mAdapter.getFirstCheckedItem();
			renameShow(show);
		}
	}

	private class OnDeleteShowClickListener implements ActionModeCallback.OnDeleteClickListener {

		@Override
		public void onDeleteClicked() {
			// TODO a implémenter
			Toast.makeText(ListShowsActivity.this, "fonction pas encore implémentée", Toast.LENGTH_SHORT).show();
		}
	}

	private final class LoadShowTask extends AsyncTask<Void, Integer, Void> {
		private List<Show> showAsyncAll;
		private List<Show> showAsyncVisibile;
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			showAsyncAll = new ArrayList<Show>();
			showAsyncVisibile = new ArrayList<Show>();
		}

		@Override
		protected Void doInBackground(Void... voids) {
			try {
				showAsyncAll.addAll(((ShowDao) mDatabaseHelper.getDao(Show.class)).queryForAll());
				if (TextUtils.isEmpty(mQuery)) {
					for (Show show : showAsyncAll) {
						show.setNumberEpisodesSaw(((EpisodeDao) mDatabaseHelper.getDao(Episode.class)).countAllEpisodeFromShowNameSeen(show.getShowName()));
					}
					showAsyncVisibile.addAll(showAsyncAll);
				} else {
					for (Show show : showAsyncAll) {
						show.setNumberEpisodesSaw(((EpisodeDao) mDatabaseHelper.getDao(Episode.class)).countAllEpisodeFromShowNameSeen(show.getShowName()));
						if (show.getShowName().toLowerCase(Locale.getDefault()).contains(mQuery.toLowerCase(Locale.getDefault()))) {
							showAsyncVisibile.add(show);
						}
					}
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			mAllShows.clear();
			mShowsVisible.clear();
			mAllShows.addAll(showAsyncAll);
			mShowsVisible.addAll(showAsyncVisibile);
			mAdapter.notifyDataSetChanged();
			if (TextUtils.isEmpty(mQuery)) {
				mTvSummary.setText("> " + mAllShows.size() + " séries suivies");
			} else {
				mTvSummary.setText("> " + mShowsVisible.size() + " séries trouvées pour la recherche \"" + mQuery + "\" X");
				mTvSummary.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						invalidateOptionsMenu();
						mQuery = null;
						startActivity(new Intent(ListShowsActivity.this, ListShowsActivity.class));
					}
				});
			}
		}
	}
}