package com.steto.diaw.activity;

import android.os.Bundle;
import android.widget.TextView;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.steto.diaw.model.Show;
import com.steto.diaw.dao.DatabaseHelper;
import com.steto.projectdiaw.R;

import java.sql.SQLException;

public class ShowDetailActivity extends SherlockActivity {
	public static final String SHOW_ID = "SHOW_ID";

	private int mId;
	private Show mShow = new Show();

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_show_detail);

		processExtras();
		readDatabase();

		getSupportActionBar().setTitle(mShow.getShowName());
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		processDataInLayout();
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

	/* Traitement */

	private void processExtras() {
		if (getIntent().getExtras() != null) {
			mId = getIntent().getExtras().getInt(SHOW_ID);
		} else {
			finish();
		}
	}

	private void readDatabase() {
		try {
			mShow = DatabaseHelper.getInstance(this).getShowDao().queryForId(mId);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void processDataInLayout() {
		TextView title = (TextView) findViewById(R.id.activity_show_detail_title);
		title.setText(mShow.getShowName());

		// TODO

	}
}