package com.steto.diaw.activity;

import java.util.List;

import roboguice.activity.RoboListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.view.Window;

import com.steto.diaw.adapter.ListShowAdapter;
import com.steto.diaw.model.Show;
import com.steto.diaw.service.IMDBService;
import com.steto.projectdiaw.R;

/**
 * Created by Stephane on 09/06/13.
 */
public class AmbiguityShow extends RoboListActivity {

	public static String INPUT_SHOW = "INPUT_SHOW";

	private Show mShow;
	private List<Show> mAllSuggestedShow;
	private ResultReceiver mIMDBReceiver = null;
	private ListShowAdapter mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_ambiguity_show);

		initIMDBReceiver();
		mShow = (Show) getIntent().getExtras().get(INPUT_SHOW);
		getIMDBSuggestion();


	}

	private void getIMDBSuggestion() {
		setProgressBarIndeterminateVisibility(true);
		Intent in = new Intent(this, IMDBService.class);
		in.putExtra(IMDBService.SERVICE_INPUT_TITLE, mShow.getShowName());
		in.putExtra(IMDBService.SERVICE_INPUT_RECEIVER, mIMDBReceiver);
		startService(in);
	}

	private void initIMDBReceiver() {
		if (mIMDBReceiver == null) {
			mIMDBReceiver = new ResultReceiver(new Handler()) {

				@SuppressWarnings("unchecked")
				@Override
				protected void onReceiveResult(int resultCode, Bundle resultData) {
					super.onReceiveResult(resultCode, resultData);
					if (resultCode == IMDBService.RESULT_CODE_OK) {

						setAllSuggestedShow((List<Show>) resultData.get(IMDBService.SERVICE_OUTPUT_DATA));

						setProgressBarIndeterminateVisibility(false);
					}
				}
			};
		}
	}

	public List<Show> getAllSuggestedShow() {
		return mAllSuggestedShow;
	}

	public void setAllSuggestedShow(List<Show> allSuggestedShow) {
		if (mAllSuggestedShow == null) {
			mAllSuggestedShow = allSuggestedShow;
			mAdapter = new ListShowAdapter(this, mAllSuggestedShow);
			getListView().setAdapter(mAdapter);
		} else {
			mAllSuggestedShow.clear();
			mAllSuggestedShow.addAll(allSuggestedShow);
			mAdapter.notifyDataSetChanged();
		}

	}
}
