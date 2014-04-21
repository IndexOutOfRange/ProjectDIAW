package com.steto.diaw.activity;

import java.util.List;

import roboguice.activity.RoboListActivity;
import roboguice.inject.ContentView;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.steto.diaw.adapter.ListShowAdapter;
import com.steto.diaw.model.Show;
import com.steto.diaw.service.TVDBService;
import com.steto.diaw.service.model.AbstractIntentService.ServiceResponseCode;
import com.steto.projectdiaw.R;

@ContentView(R.layout.activity_ambiguity_show)
public class AmbiguityShow extends RoboListActivity {

	public static final String INPUT_POTENTIAL_SHOW = "INPUT_POTENTIAL_SHOW";
	public static final String INPUT_AMBIGUOUS_SHOW = "INPUT_AMBIGUOUS_SHOW";

	private Show mShow;
	private List<Show> mAllSuggestedShow;
	private ListShowAdapter mAdapter;

	private ListView mList;
	private ResultReceiver mSerieServiceResultReceiver;

	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setProgressBarIndeterminateVisibility(false);
		super.onCreate(savedInstanceState);
		List<Show> allSuggestedShow = (List<Show>) getIntent().getExtras().get(INPUT_POTENTIAL_SHOW);
		mShow = (Show) getIntent().getExtras().get(INPUT_AMBIGUOUS_SHOW);
		mList = (ListView) findViewById(android.R.id.list);
		
		getActionBar().setTitle(String.format(getString(R.string.activity_ambiguity_format_s), mShow.getShowName()));
		
		setAllSuggestedShow(allSuggestedShow);
		// initIMDBReceiver();
		initSerieServiceReceiver();
		// getIMDBSuggestion();
		mList.setOnItemClickListener(new ListItemClickedListener());
	}

	private void launchSerieService(Show show, ResultReceiver resultReceiver) {
		Intent intent = new Intent(this, TVDBService.class);
		intent.putExtra(TVDBService.EXTRA_INPUT_SHOW, show);
		intent.putExtra(TVDBService.EXTRA_INPUT_RESULT_RECEIVER, resultReceiver);
		startService(intent);
	}

	private void initSerieServiceReceiver() {
		if (mSerieServiceResultReceiver == null) {
			mSerieServiceResultReceiver = new SerieServiceResultReceiver();
		}
	}

	public List<Show> getAllSuggestedShow() {
		return mAllSuggestedShow;
	}

	public void setAllSuggestedShow(List<Show> allSuggestedShow) {
		if (mAllSuggestedShow == null) {
			mAllSuggestedShow = allSuggestedShow;
			mAdapter = new ListShowAdapter(this, mAllSuggestedShow, true);
			mList.setAdapter(mAdapter);
		} else {
			mAllSuggestedShow.clear();
			mAllSuggestedShow.addAll(allSuggestedShow);
			mAdapter.notifyDataSetChanged();
		}

	}

	private class SerieServiceResultReceiver extends ResultReceiver {

		public SerieServiceResultReceiver() {
			super(new Handler());
		}

		@SuppressWarnings("unchecked")
		@Override
		protected void onReceiveResult(int resultCode, Bundle resultData) {
			super.onReceiveResult(resultCode, resultData);
			setProgressBarIndeterminateVisibility(false);
			if (resultCode == ServiceResponseCode.OK.value) {
				List<Show> response = (List<Show>) resultData.get(TVDBService.EXTRA_OUTPUT_DATA);
				if (response != null && !response.isEmpty()) {
					Show show = response.get(0);
					Intent data = new Intent();
					data.putExtra(ShowDetailActivity.EXTRA_SHOW_NAME, show.getShowName());
					setResult(RESULT_OK, data);
					finish();
				}
			} else {
				Toast.makeText(AmbiguityShow.this, "unable to get response from server", Toast.LENGTH_SHORT).show();
			}
		}
	}

	private class ListItemClickedListener implements AdapterView.OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
			setProgressBarIndeterminateVisibility(true);
			Show clicked = mAllSuggestedShow.get(position);
			clicked.setId(mShow.getId());
			clicked.setTVDBConnected(false);
			clicked.setShowName(mShow.getShowName());
			launchSerieService(clicked, mSerieServiceResultReceiver);
		}
	}
}
