package com.steto.diaw.activity;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import roboguice.activity.RoboListActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.steto.diaw.adapter.ListShowAdapter;
import com.steto.diaw.dao.DatabaseHelper;
import com.steto.diaw.model.Show;
import com.steto.projectdiaw.R;

public class ListShowsActivity extends RoboListActivity {

	private List<Show> mAllShows = new ArrayList<Show>();
	private ListView mList;
	private ListShowAdapter mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list_shows);

		getActionBar().setTitle(R.string.activity_title_list_show);
		getActionBar().setDisplayHomeAsUpEnabled(true);

		mList = getListView();
	}

	@Override
	protected void onResume() {
		super.onResume();
		new LoadShowTask().execute();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		/*MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.episode_list, menu);*/

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				onBackPressed(); // TODO
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Intent goToDetail = new Intent(ListShowsActivity.this, ShowDetailActivity.class);
		goToDetail.putExtra(ShowDetailActivity.EXTRA_SHOW, (Show) mAdapter.getItem((int) position));

		startActivity(goToDetail);
	}



	/* Traitement */

	public class LoadShowTask extends AsyncTask<Void, Integer, Void> {


		@Override
		protected void onPostExecute(Void aVoid) {
			super.onPostExecute(aVoid);
			if (mAdapter == null) {

				mAdapter = new ListShowAdapter(ListShowsActivity.this, mAllShows);
				mList.setAdapter(mAdapter);
			} else {
				mAdapter.notifyDataSetChanged();
			}
		}

		@Override
		protected Void doInBackground(Void... voids) {
			try {
				mAllShows.clear();
				mAllShows.addAll(DatabaseHelper.getInstance(ListShowsActivity.this).getShowDao().queryForAll());
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return null;
		}
	}
}