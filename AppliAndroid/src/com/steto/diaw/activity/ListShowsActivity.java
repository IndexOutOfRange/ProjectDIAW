package com.steto.diaw.activity;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.google.inject.Inject;
import com.steto.diaw.activity.model.DrawerActivity;
import com.steto.diaw.adapter.ListShowAdapter;
import com.steto.diaw.dao.DatabaseHelper;
import com.steto.diaw.dao.ShowDao;
import com.steto.diaw.model.Show;
import com.steto.projectdiaw.R;

public class ListShowsActivity extends DrawerActivity {

	private List<Show> mAllShows = new ArrayList<Show>();
	private ListShowAdapter mAdapter;

	@Inject
	private DatabaseHelper mDatabaseHelper;

	private ListView mList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		mAdapter = new ListShowAdapter(ListShowsActivity.this, mAllShows);

		mList = (ListView) mContentView.findViewById(android.R.id.list);
		mList.setAdapter(mAdapter);
		mList.setOnItemClickListener(new OnShowClickListener());
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
				mAllShows.addAll(((ShowDao) mDatabaseHelper.getDao(Show.class)).queryForAll());
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			mAdapter.notifyDataSetChanged();
		}
	}
}