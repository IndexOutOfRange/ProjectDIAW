package com.steto.diaw.activity;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import roboguice.activity.RoboListActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.google.inject.Inject;
import com.steto.diaw.adapter.ListShowAdapter;
import com.steto.diaw.dao.DatabaseHelper;
import com.steto.diaw.dao.ShowDao;
import com.steto.diaw.model.Show;
import com.steto.projectdiaw.R;

@ContentView(R.layout.activity_list_shows)
public class ListShowsActivity extends RoboListActivity {

	private List<Show> mAllShows = new ArrayList<Show>();
	private ListShowAdapter mAdapter;

	@Inject
	private DatabaseHelper mDatabaseHelper;

	@InjectView(android.R.id.list)
	private ListView mList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		mAdapter = new ListShowAdapter(ListShowsActivity.this, mAllShows);
		mList.setAdapter(mAdapter);
	}

	@Override
	protected void onResume() {
		super.onResume();
		new LoadShowTask().execute();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				onBackPressed();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Intent intentDetail = new Intent(this, ShowDetailActivity.class);
		intentDetail.putExtra(ShowDetailActivity.EXTRA_SHOW, (Show) mAdapter.getItem((int) position));
		startActivity(intentDetail);
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