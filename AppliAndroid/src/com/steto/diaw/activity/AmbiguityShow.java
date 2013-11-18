package com.steto.diaw.activity;

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
import com.steto.diaw.service.IMDBService;
import com.steto.diaw.service.ParseGetEpisodesService;
import com.steto.diaw.service.TVDBService;
import com.steto.projectdiaw.R;

import java.util.List;

import roboguice.activity.RoboListActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

@ContentView(R.layout.activity_ambiguity_show)
public class AmbiguityShow extends RoboListActivity {

    public static final String INPUT_POTENTIAL_SHOW = "INPUT_POTENTIAL_SHOW";
    public static final String INPUT_AMBIGUOUS_SHOW = "INPUT_AMBIGUOUS_SHOW";

	private Show mShow;
	private List<Show> mAllSuggestedShow;
	private ResultReceiver mIMDBReceiver = null;
	private ListShowAdapter mAdapter;

    private ListView mList;
    private ResultReceiver mSerieServiceResultReceiver;

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setProgressBarIndeterminateVisibility(false);
		super.onCreate(savedInstanceState);
        List<Show> allSuggestedShow = (List<Show>) getIntent().getExtras().get(INPUT_POTENTIAL_SHOW);
        mShow = (Show) getIntent().getExtras().get(INPUT_AMBIGUOUS_SHOW);
        mList = (ListView) findViewById(android.R.id.list);
        setAllSuggestedShow(allSuggestedShow);
//        initIMDBReceiver();
        initSerieServiceReceiver();
//		getIMDBSuggestion();
        mList.setOnItemClickListener(new ListItemClickedListener());
	}


    private void launchSerieService(Show show, ResultReceiver resultReceiver) {
        Intent intent = new Intent(this, TVDBService.class);
        intent.putExtra(TVDBService.INPUT_SERIE, show);
        intent.putExtra(TVDBService.INPUT_RESULTRECEIVER, resultReceiver);
        startService(intent);
    }
	private void getIMDBSuggestion() {
		setProgressBarIndeterminateVisibility(true);
		Intent in = new Intent(this, IMDBService.class);
		in.putExtra(IMDBService.SERVICE_INPUT_TITLE, mShow.getShowName());
		in.putExtra(IMDBService.SERVICE_INPUT_RECEIVER, mIMDBReceiver);
		startService(in);
	}

    private void initSerieServiceReceiver() {
        if( mSerieServiceResultReceiver == null ) {
            mSerieServiceResultReceiver = new SerieServiceResultReceiver();
        }
    }
    private void initIMDBReceiver() {
		if (mIMDBReceiver == null) {
			mIMDBReceiver = new IMDBReceiver();
		}
	}

	public List<Show> getAllSuggestedShow() {
		return mAllSuggestedShow;
	}

	public void setAllSuggestedShow(List<Show> allSuggestedShow) {
		if (mAllSuggestedShow == null) {
			mAllSuggestedShow = allSuggestedShow;
			mAdapter = new ListShowAdapter(this, mAllSuggestedShow);
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

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            super.onReceiveResult(resultCode, resultData);
            setProgressBarIndeterminateVisibility(false);
            if( resultCode == TVDBService.RESULT_CODE_OK ) {
                List<Show> response = (List<Show>) resultData.get(TVDBService.OUTPUT_DATA);
                if (response != null && !response.isEmpty()) {
                    Show show = response.get(0);
                    Intent intent = new Intent(AmbiguityShow.this, ShowDetailActivity.class);
                    intent.putExtra(ShowDetailActivity.EXTRA_SHOW_NAME, show.getShowName());
                    startActivity(intent);
                }
            } else {
                Toast.makeText(AmbiguityShow.this, "unable to get response from server", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class IMDBReceiver extends ResultReceiver {

        public IMDBReceiver() {
            super(new Handler());
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            super.onReceiveResult(resultCode, resultData);
            if (resultCode == IMDBService.RESULT_CODE_OK ) {

                setAllSuggestedShow((List<Show>) resultData.get(IMDBService.SERVICE_OUTPUT_DATA));

                setProgressBarIndeterminateVisibility(false);
            }
        }
    }

    private class ListItemClickedListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            setProgressBarIndeterminateVisibility(true);
            Show clicked = mAllSuggestedShow.get(i);
            clicked.setId(mShow.getId());
            clicked.setTVDBConnected(false);
            clicked.setShowName(mShow.getShowName());
            launchSerieService(clicked, mSerieServiceResultReceiver);
        }
    }
}
