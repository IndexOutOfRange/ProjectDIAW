package com.steto.diaw.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import com.actionbarsherlock.app.SherlockListActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.steto.diaw.adapter.ListShowAdapter;
import com.steto.diaw.helper.DatabaseHelper;
import com.steto.diaw.model.Show;
import com.steto.projectdiaw.R;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ListShowsActivity extends SherlockListActivity {

	private static final String TAG = "ListShowsActivity";

	private List<Show> mAllShows = new ArrayList<Show>();
	private ListView mList;
	private ListShowAdapter mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list_shows);

		getSupportActionBar().setTitle(R.string.activity_title_list_show);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		readDatabase();

		mList = getListView();
		mAdapter = new ListShowAdapter(this, mAllShows);
		mList.setAdapter(mAdapter);
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
		goToDetail.putExtra(ShowDetailActivity.SHOW_ID, (int) id);

		startActivity(goToDetail);
	}



	/* Traitement */

	private void readDatabase() {
		try {
			mAllShows.clear();
			mAllShows.addAll(DatabaseHelper.getInstance(ListShowsActivity.this).getShowDao().queryForAll());
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}