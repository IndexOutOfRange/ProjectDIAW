package com.steto.diaw.activity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockExpandableListActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.steto.diaw.adapter.SeasonWithEpisodesExpandableAdapter;
import com.steto.diaw.helper.TranslucideActionBarHelper;
import com.steto.diaw.model.Episode;
import com.steto.diaw.model.Season;
import com.steto.diaw.model.Show;
import com.steto.diaw.dao.DatabaseHelper;
import com.steto.projectdiaw.R;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ShowDetailActivity extends SherlockExpandableListActivity {
	public static final String SHOW_ID = "SHOW_ID";
	private static final String TAG = "ShowDetailActivity";

	private Show mShow;
	private List<Season> mListSeasons;

	private View mHeaderContainer;
	private ActionBar mActionBar;

	private Drawable mActionBarBackgroundDrawable;
	private TranslucideActionBarHelper mActionBarTranslucideHelper;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mActionBarTranslucideHelper = new TranslucideActionBarHelper(getSupportActionBar());
		setContentView(R.layout.activity_show_detail);

		int id = processExtras();
		if (id == -1) {
			finish();
		}
		readDatabase(id);

		mActionBarTranslucideHelper.initActionBar(this, mShow.getShowName(), "", true, com.actionbarsherlock.R.drawable.abs__ab_solid_dark_holo);

		processDataInLayout();
		mActionBarTranslucideHelper.setOnScrollChangedListener(getExpandableListView());
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

	private int processExtras() {
		if (getIntent().getExtras() != null) {
			return getIntent().getExtras().getInt(SHOW_ID);
		} else {
			return -1;
		}
	}

	private void readDatabase(int id) {
		mShow = new Show();
		List<Episode> episodes = new ArrayList<Episode>();
		try {
			mShow = DatabaseHelper.getInstance(this).getShowDao().queryForId(id);
			episodes = DatabaseHelper.getInstance(this).getEpisodeDao().queryForEq(Episode.SHOWNAME, mShow.getShowName());
		} catch (SQLException e) {
			e.printStackTrace();
		}
		mListSeasons = Season.getListSeason(episodes);
	}

	private void processDataInLayout() {
		// List
		SeasonWithEpisodesExpandableAdapter adapter = new SeasonWithEpisodesExpandableAdapter(this, mListSeasons);
		View headerContainer = getLayoutInflater().inflate(R.layout.header_show, null);
		getExpandableListView().addHeaderView(headerContainer);
		setListAdapter(adapter);

		// ActionBar
		mActionBarTranslucideHelper.setHeaderContainer(headerContainer);

		// Data
		TextView title = (TextView) headerContainer.findViewById(R.id.activity_show_detail_title);
		title.setText(mShow.getShowName());
	}
}