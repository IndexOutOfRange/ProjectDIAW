package com.steto.diaw.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.actionbarsherlock.app.SherlockExpandableListActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;
import com.steto.diaw.adapter.SeasonWithEpisodesExpandableAdapter;
import com.steto.diaw.dao.DatabaseHelper;
import com.steto.diaw.dao.ShowDao;
import com.steto.diaw.helper.TranslucideActionBarHelper;
import com.steto.diaw.model.Episode;
import com.steto.diaw.model.Season;
import com.steto.diaw.model.Show;
import com.steto.diaw.service.BannerService;
import com.steto.diaw.service.TVDBService;
import com.steto.diaw.service.ParseService;
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
	private TranslucideActionBarHelper mActionBarTranslucideHelper;
    private ResultReceiver mShowResultReceiver;
    private ResultReceiver mBannerResultReceiver;

    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		mActionBarTranslucideHelper = new TranslucideActionBarHelper(getSupportActionBar());
		setContentView(R.layout.activity_show_detail);

        initShowResultReceiver();
        initBannerResultReceiver();
		int id = processExtras();
		if (id == -1) {
			finish();
		}
        readDatabase();
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

    private void initShowResultReceiver() {
        if (mShowResultReceiver == null) {

            mShowResultReceiver = new ResultReceiver(new Handler()) {

                @Override
                protected void onReceiveResult(int resultCode, Bundle resultData) {
                    super.onReceiveResult(resultCode, resultData);
                    Log.i(TAG, "onResult");
                    setSupportProgressBarIndeterminateVisibility(false);
                    if (resultCode == ParseService.RESULT_CODE_OK) {
                        List<Show> response = (List<Show>)resultData.get(TVDBService.OUTPUT_DATA);
                        if( response != null && !response.isEmpty()) {
                            mShow = response.get(0);
                            refreshLayout();
                            if( mShow.getBanner() == null ) {
                                launchBannerService();
                            }
                        }
                    } else {
                        Toast.makeText(ShowDetailActivity.this, getString(R.string.msg_erreur_reseau), Toast.LENGTH_SHORT).show();
                    }
                }
            };
        }
    }

    private void initBannerResultReceiver() {
        if (mBannerResultReceiver == null) {

            mBannerResultReceiver  = new ResultReceiver(new Handler()) {

                @Override
                protected void onReceiveResult(int resultCode, Bundle resultData) {
                    super.onReceiveResult(resultCode, resultData);
                    Log.i(TAG, "onResult");
                    setSupportProgressBarIndeterminateVisibility(false);
                    if (resultCode == ParseService.RESULT_CODE_OK) {
                        Bitmap banner = (Bitmap)resultData.getParcelable(BannerService.OUTPUT_BITMAP);
                        mShow.setBanner(banner);
                        refreshLayout();
                    } else {
                        Toast.makeText(ShowDetailActivity.this, getString(R.string.msg_erreur_reseau), Toast.LENGTH_SHORT).show();
                    }
                }
            };
        }
    }

	private int processExtras() {
		if (getIntent().getExtras() != null) {
            mShow = (Show)(getIntent().getExtras().get(SHOW_ID));
			return 0;
		} else {
			return -1;
		}
	}

	private void readDatabase() {
		List<Episode> episodes = new ArrayList<Episode>();
		try {
            ShowDao myBDD =  DatabaseHelper.getInstance(this).getShowDao();
            mListSeasons = myBDD.getSeasonsFromShow(mShow);
		} catch (SQLException e) {
            Toast.makeText(this, "erreur lors de la recupération des episodes de la serie",Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}

        setSupportProgressBarIndeterminateVisibility(true);
        launchSerieService();
        launchBannerService();
    }

    private void launchSerieService() {
        Intent in = new Intent(this, TVDBService.class);
        in.putExtra(TVDBService.INPUT_SERIE, mShow);
        in.putExtra(TVDBService.INPUT_RESULTRECEIVER, mShowResultReceiver);
        startService(in);
    }

    private void launchBannerService() {
        if( mShow.getBannerURL() != null) {
            Intent ban= new Intent(this, BannerService.class);
            ban.putExtra(BannerService.INPUT_SERIE, mShow);
            ban.putExtra(BannerService.INPUT_RECEIVER, mBannerResultReceiver);
            startService(ban);
        }
    }

    private void processDataInLayout() {
		// List
		SeasonWithEpisodesExpandableAdapter adapter = new SeasonWithEpisodesExpandableAdapter(this, mListSeasons);
        mHeaderContainer = getLayoutInflater().inflate(R.layout.header_show, null);
		getExpandableListView().addHeaderView(mHeaderContainer);
		setListAdapter(adapter);

		// ActionBar
		mActionBarTranslucideHelper.setHeaderContainer(mHeaderContainer);


	}

    private void refreshLayout() {

        if( mShow != null ) {
	        findViewById(R.id.activity_show_detail_info_layout).setVisibility(View.VISIBLE);
            // TVDBContainerData
            TextView title = (TextView) mHeaderContainer.findViewById(R.id.activity_show_detail_title);
            title.setText(mShow.getShowName());

            // TVDBContainerData
            TextView genre = (TextView) mHeaderContainer.findViewById(R.id.activity_show_detail_genre);
            genre.setText(mShow.getGenre());
            // TVDBContainerData
            TextView onAir = (TextView) mHeaderContainer.findViewById(R.id.activity_show_detail_on_air);
            onAir.setText(mShow.getDateDebut() != null ? mShow.getDateDebut().toString() : "loading");
            // TVDBContainerData
            TextView statut = (TextView) mHeaderContainer.findViewById(R.id.activity_show_detail_statut);
            statut.setText(mShow.getStatus());

            if( mShow.getBanner() != null ) {
                // TVDBContainerData
                ImageView bannerView = (ImageView) mHeaderContainer.findViewById(R.id.activity_show_detail_image);
                bannerView.setImageBitmap(mShow.getBannerAsBitmap());
            }
        }
    }
}