package com.steto.diaw.activity;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.List;

import roboguice.activity.RoboExpandableListActivity;
import roboguice.inject.ContentView;
import roboguice.util.Ln;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.inject.Inject;
import com.steto.diaw.adapter.SeasonWithEpisodesExpandableAdapter;
import com.steto.diaw.dao.DatabaseHelper;
import com.steto.diaw.dao.ShowDao;
import com.steto.diaw.helper.TranslucideActionBarHelper;
import com.steto.diaw.model.Season;
import com.steto.diaw.model.Show;
import com.steto.diaw.service.BannerService;
import com.steto.diaw.service.ParseGetEpisodesService;
import com.steto.diaw.service.TVDBService;
import com.steto.projectdiaw.R;

@ContentView(R.layout.activity_show_detail)
public class ShowDetailActivity extends RoboExpandableListActivity {

	private static final int SHORT_SUMMARY_LENGHT = 100;
	private static final String HINT_TO_BE_CONTINUED = "...";
	public static final String EXTRA_SHOW = "EXTRA_SHOW";
	public static final String EXTRA_SHOW_NAME = "EXTRA_SHOW_NAME";

	private Show mShow;
	private List<Season> mListSeasons;

	@Inject
	private DatabaseHelper mDatabaseHelper;

	private View mHeaderContainer;
	private TranslucideActionBarHelper mActionBarTranslucideHelper;
	private ResultReceiver mShowResultReceiver = new ShowResultReceiver();
	private ResultReceiver mBannerResultReceiver = new BannerReceiverExtension();

	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		super.onCreate(savedInstanceState);
		mActionBarTranslucideHelper = new TranslucideActionBarHelper(getActionBar());

		if (!processExtras()) {
			finish();
		}

		readDatabase();
		mActionBarTranslucideHelper.initActionBar(this, mShow.getShowName(), "", true, R.drawable.ab_solid_dark_holo);

		processDataInLayout();
		mActionBarTranslucideHelper.setOnScrollChangedListener(getExpandableListView());
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

	private void resolveAmbiguity(List<Show> ambiguous) {
		Intent intent = new Intent(this, AmbiguityShow.class);
		intent.putExtra(AmbiguityShow.INPUT_POTENTIAL_SHOW, (Serializable) ambiguous);
        intent.putExtra(AmbiguityShow.INPUT_AMBIGUOUS_SHOW, (Serializable) mShow);
		startActivity(intent);
		// TODO startActivityForResult seems better
	}

	private boolean processExtras() {
		if (getIntent().getExtras() != null) {
			mShow = (Show) (getIntent().getExtras().get(EXTRA_SHOW));
			if (mShow == null) {
				String showName = getIntent().getExtras().getString(EXTRA_SHOW_NAME);
				try {
					mShow = ((ShowDao) mDatabaseHelper.getDao(Show.class)).queryFromName(showName);
					return true;
				} catch (SQLException e) {
					Ln.e(e);
					return false;
				}
			}
			return true;
		} else {
			return false;
		}
	}

	private void readDatabase() {
		try {
			ShowDao showDao = mDatabaseHelper.getDao(Show.class);
			mListSeasons = showDao.getSeasonsFromShow(mShow);
		} catch (SQLException e) {
			Ln.e(e);
			Toast.makeText(this, "Erreur lors de la récupération des episodes de la serie", Toast.LENGTH_SHORT).show();
		}

		setProgressBarIndeterminateVisibility(true);
		launchSerieService();
		launchBannerService();
	}

	private void launchSerieService() {
		Intent intent = new Intent(this, TVDBService.class);
		intent.putExtra(TVDBService.INPUT_SERIE, mShow);
		intent.putExtra(TVDBService.INPUT_RESULTRECEIVER, mShowResultReceiver);
		startService(intent);
	}

	private void launchBannerService() {
		if (mShow.getBannerURL() != null) {
			Intent intent = new Intent(this, BannerService.class);
			intent.putExtra(BannerService.INPUT_SERIE, mShow);
			intent.putExtra(BannerService.INPUT_RECEIVER, mBannerResultReceiver);
			startService(intent);
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

		if (mShow != null) {
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
			// TVDBContainerData
			final TextView summary = (TextView) mHeaderContainer.findViewById(R.id.activity_show_detail_summary);
			if(mShow.getResume().length() > SHORT_SUMMARY_LENGHT) {
			summary.setText(mShow.getResume().substring(0, SHORT_SUMMARY_LENGHT)+HINT_TO_BE_CONTINUED);
			summary.setOnClickListener(new OnSummaryClickListener(summary));
			} else {
				summary.setText(mShow.getResume());
			}

			if (mShow.getBanner() != null) {
				// TVDBContainerData
				ImageView bannerView = (ImageView) mHeaderContainer.findViewById(R.id.activity_show_detail_image);
				bannerView.setImageBitmap(mShow.getBannerAsBitmap());
			}
		}
	}

	private final class OnSummaryClickListener implements OnClickListener {

		private final TextView summary;

		private OnSummaryClickListener(TextView summary) {
			this.summary = summary;
		}

		@Override
		public void onClick(View v) {
			int nbChar = summary.getText().length();
			if(nbChar == SHORT_SUMMARY_LENGHT + HINT_TO_BE_CONTINUED.length()) {
				summary.setText(mShow.getResume());
			} else {
				summary.setText(mShow.getResume().substring(0, SHORT_SUMMARY_LENGHT)+HINT_TO_BE_CONTINUED);
			}
			
		}
	}

	private final class BannerReceiverExtension extends ResultReceiver {

		private BannerReceiverExtension() {
			super(new Handler());
		}

		@Override
		protected void onReceiveResult(int resultCode, Bundle resultData) {
			super.onReceiveResult(resultCode, resultData);
			Ln.i("onResult");
			setProgressBarIndeterminateVisibility(false);
			if (resultCode == ParseGetEpisodesService.RESULT_CODE_OK) {
				Bitmap banner = (Bitmap) resultData.getParcelable(BannerService.OUTPUT_BITMAP);
				mShow.setBanner(banner);
				refreshLayout();
			} else {
				Toast.makeText(ShowDetailActivity.this, getString(R.string.msg_erreur_reseau), Toast.LENGTH_SHORT).show();
			}
		}
	}

	private final class ShowResultReceiver extends ResultReceiver {

		private ShowResultReceiver() {
			super(new Handler());
		}

		@SuppressWarnings("unchecked")
		@Override
		protected void onReceiveResult(int resultCode, Bundle resultData) {
			super.onReceiveResult(resultCode, resultData);
			Ln.i("onResult");
			setProgressBarIndeterminateVisibility(false);
			if (resultCode == ParseGetEpisodesService.RESULT_CODE_OK) {
				List<Show> response = (List<Show>) resultData.get(TVDBService.OUTPUT_DATA);
				if (response != null && !response.isEmpty()) {
					mShow = response.get(0);
					refreshLayout();
					if (mShow.getBanner() == null) {
						launchBannerService();
					}
				}
			} else if (resultCode == TVDBService.RESULT_CODE_AMBIGUITY) {
                List<Show> response = (List<Show>) resultData.get(TVDBService.OUTPUT_DATA);
                if( response != null && !response.isEmpty() ) {
				    resolveAmbiguity(response);
                } else {
                    Toast.makeText(ShowDetailActivity.this, "Aucune serie ne correspond à ce nom.", Toast.LENGTH_SHORT).show();
                }
			} else {
				Toast.makeText(ShowDetailActivity.this, "Unable to get result from service", Toast.LENGTH_SHORT).show();
			}
		}
	}
}