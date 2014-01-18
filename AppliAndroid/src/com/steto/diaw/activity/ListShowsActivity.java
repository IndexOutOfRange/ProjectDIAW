package com.steto.diaw.activity;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.google.inject.Inject;
import com.steto.diaw.activity.model.DrawerActivity;
import com.steto.diaw.adapter.ListShowAdapter;
import com.steto.diaw.dao.DatabaseHelper;
import com.steto.diaw.dao.ShowDao;
import com.steto.diaw.model.Show;
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		mAdapter = new ListShowAdapter(ListShowsActivity.this, mShowsVisible);

		mTvSummary = (TextView) mContentView.findViewById(R.id.summary);
		mList = (ListView) mContentView.findViewById(android.R.id.list);
		mList.setAdapter(mAdapter);
		mList.setOnItemClickListener(new OnShowClickListener());

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
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(0, 0);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
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
		// No menu > nothing
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

	private final class OnShowClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> view, View v, int position, long id) {
			Intent intentDetail = new Intent(ListShowsActivity.this, ShowDetailActivity.class);
			intentDetail.putExtra(ShowDetailActivity.EXTRA_SHOW, (Show) mAdapter.getItem((int) position));
			startActivity(intentDetail);
		}
	}

	private final class LoadShowTask extends AsyncTask<Void, Integer, Void> {

		@Override
		protected Void doInBackground(Void... voids) {
			try {
				mAllShows.clear();
				mShowsVisible.clear();

				mAllShows.addAll(((ShowDao) mDatabaseHelper.getDao(Show.class)).queryForAll());
				if (TextUtils.isEmpty(mQuery)) {
					mShowsVisible.addAll(mAllShows);
				} else {
					for (Show show : mAllShows) {
						if (show.getShowName().toLowerCase(Locale.getDefault()).contains(mQuery.toLowerCase(Locale.getDefault()))) {
							mShowsVisible.add(show);
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
			mAdapter.notifyDataSetChanged();
			if (TextUtils.isEmpty(mQuery)) {
				mTvSummary.setText("> " + mAllShows.size() + " séries suivies");
			} else {
				mTvSummary.setText("> " + mShowsVisible.size() + " séries trouvées pour la recherche \"" + mQuery + "\" X");
				mTvSummary.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						invalidateOptionsMenu();
						mQuery = null;
						startActivity(new Intent(ListShowsActivity.this, ListShowsActivity.class));
					}
				});
			}
		}
	}
}