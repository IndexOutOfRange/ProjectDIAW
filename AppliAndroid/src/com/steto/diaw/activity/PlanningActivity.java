package com.steto.diaw.activity;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import roboguice.util.Ln;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.Window;
import android.widget.ListView;

import com.google.inject.Inject;
import com.steto.diaw.activity.model.DrawerActivity;
import com.steto.diaw.adapter.PlanningShowAdapter;
import com.steto.diaw.dao.DatabaseHelper;
import com.steto.diaw.dao.EpisodeDao;
import com.steto.diaw.model.Episode;
import com.steto.projectdiaw.R;

public class PlanningActivity extends DrawerActivity {

	private PlanningShowAdapter mAdapter;
	@Inject
	private DatabaseHelper mDatabaseHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setProgressBarIndeterminateVisibility(false);
		super.onCreate(savedInstanceState);

		mAdapter = new PlanningShowAdapter(this);
		ListView listview = (ListView) mContentView.findViewById(android.R.id.list);
		listview.setAdapter(mAdapter);
	}

	@Override
	protected void onResume() {
		super.onResume();
		new LoadPlanningEpisodeTask().execute();
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(0, 0);
	}

	@Override
	protected int getLayoutContentFrame() {
		return R.layout.activity_planning;
	}

	@Override
	protected int getSelectedItem() {
		return DRAWER_PLANNING_SERIES;
	}

	@Override
	protected void manageIconsInActionBar(Menu menu, boolean drawerOpen) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void manageDrawerItemClick(int position) {
		if (position == getSelectedItem()) {
			return;
		} else if (position == DRAWER_DERNIERS_EPISODES) {
			startActivity(new Intent(this, EpisodesSeenActivity.class));
			overridePendingTransition(0, 0);
		} else if (position == DRAWER_MES_SERIES) {
			startActivity(new Intent(this, ListShowsActivity.class));
			overridePendingTransition(0, 0);
		}
	}

	private final class LoadPlanningEpisodeTask extends AsyncTask<Void, Integer, Void> {

		private List<Episode> planningEpisodeAsync;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			setProgressBarIndeterminateVisibility(true);
			planningEpisodeAsync = new ArrayList<Episode>();
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			try {
				EpisodeDao dao = mDatabaseHelper.getDao(Episode.class);
				planningEpisodeAsync = dao.queryForPlanning();
			} catch (SQLException e) {
				Ln.e(e);
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			setProgressBarIndeterminateVisibility(false);
			mAdapter.setEpisodeList(planningEpisodeAsync);
		}

	}
}
