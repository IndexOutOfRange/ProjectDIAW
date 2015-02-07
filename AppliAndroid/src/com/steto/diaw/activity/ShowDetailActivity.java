package com.steto.diaw.activity;

import java.io.Serializable;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;

import roboguice.activity.RoboExpandableListActivity;
import roboguice.inject.ContentView;
import roboguice.util.Ln;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.inject.Inject;
import com.steto.diaw.adapter.SeasonWithEpisodesExpandableAdapter;
import com.steto.diaw.dao.DatabaseHelper;
import com.steto.diaw.dao.ShowDao;
import com.steto.diaw.helper.TranslucideActionBarHelper;
import com.steto.diaw.model.Episode;
import com.steto.diaw.model.Season;
import com.steto.diaw.model.Show;
import com.steto.diaw.service.BannerService;
import com.steto.diaw.service.TVDBService;
import com.steto.diaw.service.model.AbstractIntentService;
import com.steto.diaw.service.model.AbstractIntentService.ServiceResponseCode;
import com.steto.projectdiaw.R;

@ContentView(R.layout.activity_show_detail)
public class ShowDetailActivity extends RoboExpandableListActivity {

	private static final int SHORT_SUMMARY_LENGHT = 200;
	private static final String HINT_TO_BE_CONTINUED = "...";
	public static final String EXTRA_SHOW = "EXTRA_SHOW";
	public static final String EXTRA_SHOW_NAME = "EXTRA_SHOW_NAME";
	private static final int REQUEST_RESOLVE_AMBIGUITY = 1000;

	private Show mShow;
	private List<Season> mListSeasons;
	private boolean mBannerIsDownloading = false;

	@Inject
	private DatabaseHelper mDatabaseHelper;

	private View mHeaderContainer;
	private TranslucideActionBarHelper mActionBarTranslucideHelper;
	private ResultReceiver mShowResultReceiver = new ShowResultReceiver();
	private ResultReceiver mBannerResultReceiver = new BannerReceiverExtension();
	private SeasonWithEpisodesExpandableAdapter mAdapter;

	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		super.onCreate(savedInstanceState);
		mActionBarTranslucideHelper = new TranslucideActionBarHelper(getActionBar());

		if (!processExtras()) {
			finish();
			return;
		}

		readDatabase();
		mActionBarTranslucideHelper.initActionBar(this, mShow.getShowName(), "", true, R.drawable.ab_solid_dark_holo);

		initializeLayoutList();
		mActionBarTranslucideHelper.setOnScrollChangedListener(getExpandableListView());

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == REQUEST_RESOLVE_AMBIGUITY && resultCode == RESULT_OK) {
			if (!manageResultResolveAmbiguity(data)) {
				finish();
			}
		}
	}

    @Override
    protected void onResume() {
        super.onResume();
        if (mShow.isTVDBConnected()) {
            setProgressBarIndeterminateVisibility(false);
            initializeData();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.show_detail, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_update_show_data:
                launchSerieService(true);
                return true;
        }
        return false;
    }

    private void resolveAmbiguity(List<Show> ambiguous) {
		Intent intent = new Intent(ShowDetailActivity.this, AmbiguityShow.class);
		intent.putExtra(AmbiguityShow.INPUT_POTENTIAL_SHOW, (Serializable) ambiguous);
		intent.putExtra(AmbiguityShow.INPUT_AMBIGUOUS_SHOW, (Serializable) mShow);
		startActivityForResult(intent, REQUEST_RESOLVE_AMBIGUITY);
	}

	private boolean manageResultResolveAmbiguity(Intent data) {
		String showName = data.getExtras().getString(ShowDetailActivity.EXTRA_SHOW_NAME);
		try {
			mShow = ((ShowDao) mDatabaseHelper.getDao(Show.class)).queryFromName(showName);
			setProgressBarIndeterminateVisibility(true);
			refreshLayout();
			launchBannerService();
			return true;
		} catch (SQLException e) {
			Ln.e(e);
			return false;
		}
	}

	private boolean processExtras() {
		if (getIntent().getExtras() != null) {
			mShow = (Show) (getIntent().getExtras().get(EXTRA_SHOW));
			if (mShow != null) {
				return true;
			} else {
				String showName = getIntent().getExtras().getString(ShowDetailActivity.EXTRA_SHOW_NAME);
				try {
					mShow = ((ShowDao) mDatabaseHelper.getDao(Show.class)).queryFromName(showName);
					return true;
				} catch (SQLException e) {
					Ln.e(e);
					return false;
				}
			}
		}
		return false;
	}

	private void readDatabase() {
		try {
			ShowDao showDao = mDatabaseHelper.getDao(Show.class);
			mListSeasons = showDao.getSeasonsFromShow(mShow);
		} catch (SQLException e) {
			Ln.e(e);
			Toast.makeText(this, "Erreur lors de la récupération des episodes de la serie", Toast.LENGTH_SHORT).show();
		}
		launchSerieService(false);
		launchBannerService();
	}

	private void launchSerieService(boolean forceRefresh) {
		if (!mShow.isTVDBConnected() || forceRefresh) {
            setProgressBarIndeterminateVisibility(true);
			Intent intent = new Intent(this, TVDBService.class);
			intent.putExtra(TVDBService.EXTRA_INPUT_SHOW, mShow);
            intent.putExtra(TVDBService.EXTRA_INPUT_FORCE_REFRESH, forceRefresh);
			intent.putExtra(TVDBService.EXTRA_INPUT_RESULT_RECEIVER, mShowResultReceiver);
			startService(intent);
		}
	}

	private void launchBannerService() {
		if (mShow.getBannerURL() != null && mShow.getBanner() == null && !mBannerIsDownloading) {
			mBannerIsDownloading = true;
            setProgressBarIndeterminateVisibility(true);
			Intent intent = new Intent(this, BannerService.class);
			intent.putExtra(BannerService.EXTRA_INPUT_SHOW, mShow);
			intent.putExtra(BannerService.EXTRA_INPUT_RESULT_RECEIVER, mBannerResultReceiver);
			startService(intent);
		}
	}

	private void initializeData() {
		try {
			ShowDao showDao = mDatabaseHelper.getDao(Show.class);
			List<Season> list = showDao.getSeasonsFromShow(mShow);
			if (list != null && !list.isEmpty()) {
				mListSeasons.clear();
				mListSeasons.addAll(list);
				mAdapter.notifyDataSetChanged();

				if (mShow.getNumberSeasons() == 0) {
					int nbSeason = mListSeasons.size();
					if (mListSeasons.get(0).getNumber() == 0) {
						nbSeason--;
					}
					mShow.setNumberSeasons(nbSeason);
					showDao.update(mShow);
				}
			}
		} catch (SQLException e) {
			Ln.e(e);
			Toast.makeText(getApplicationContext(), "Erreur lors de la récupération des episodes de la serie", Toast.LENGTH_SHORT).show();
		}

		refreshLayout();
		launchBannerService();
	}

	private void initializeLayoutList() {
		// List
		mAdapter = new SeasonWithEpisodesExpandableAdapter(this, mListSeasons);
		mHeaderContainer = getLayoutInflater().inflate(R.layout.header_show, null);
		getExpandableListView().addHeaderView(mHeaderContainer);
		setListAdapter(mAdapter);

		// ActionBar
		mActionBarTranslucideHelper.setHeaderContainer(mHeaderContainer);
	}

	private void refreshLayout() {
		if (mShow != null) {
			findViewById(R.id.activity_show_detail_info_layout).setVisibility(View.VISIBLE);

			TextView title = (TextView) mHeaderContainer.findViewById(R.id.activity_show_detail_title);
			title.setText(mShow.getShowName());
			TextView genre = (TextView) mHeaderContainer.findViewById(R.id.activity_show_detail_genre);
			genre.setText(mShow.getGenre());
			TextView onAir = (TextView) mHeaderContainer.findViewById(R.id.activity_show_detail_on_air);
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
			onAir.setText(mShow.getDateDebut() != null ? sdf.format(mShow.getDateDebut()) : "loading");
			TextView statut = (TextView) mHeaderContainer.findViewById(R.id.activity_show_detail_statut);
			statut.setText(mShow.getStatus());
			TextView nbSeasons = (TextView) mHeaderContainer.findViewById(R.id.activity_show_nb_seasons);
			nbSeasons.setText(String.valueOf(mShow.getNumberSeasons()));
			TextView nbEpisodes = (TextView) mHeaderContainer.findViewById(R.id.activity_show_nb_episodes);
			nbEpisodes.setText(String.valueOf(mShow.getNumberEpisodes()));

			final TextView summary = (TextView) mHeaderContainer.findViewById(R.id.activity_show_detail_summary);
			if (mShow.getResume() != null && mShow.getResume().length() > SHORT_SUMMARY_LENGHT) {
				summary.setText(mShow.getResume().substring(0, SHORT_SUMMARY_LENGHT) + HINT_TO_BE_CONTINUED);
				summary.setOnClickListener(new OnSummaryClickListener(summary));
			} else {
				summary.setText(mShow.getResume());
			}

			if (mShow.getBanner() != null) {
				ImageView bannerView = (ImageView) mHeaderContainer.findViewById(R.id.activity_show_detail_image);
				bannerView.setImageBitmap(mShow.getBannerAsBitmap());
			}
		}
	}

	private static class OnRenameCancelClickListener implements DialogInterface.OnClickListener {

		public void onClick(DialogInterface dialog, int id) {
			dialog.dismiss();
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
			if (nbChar == SHORT_SUMMARY_LENGHT + HINT_TO_BE_CONTINUED.length()) {
				summary.setText(mShow.getResume());
			} else {
				summary.setText(mShow.getResume().substring(0, SHORT_SUMMARY_LENGHT) + HINT_TO_BE_CONTINUED);
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
			mBannerIsDownloading = false;
			if (resultCode == ServiceResponseCode.OK.value) {
				Bitmap banner = (Bitmap) resultData.getParcelable(BannerService.EXTRA_OUTPUT_BITMAP);
				if (banner != null) {
					mShow.setBanner(banner);
					refreshLayout();
				}
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
			if (resultCode == ServiceResponseCode.OK.value) {
				boolean ambiguity = resultData.getBoolean(TVDBService.EXTRA_OUTPUT_AMBIGUITY);
				if (ambiguity) {
					List<Show> response = (List<Show>) resultData.get(TVDBService.EXTRA_OUTPUT_DATA);
					resolveAmbiguity(response);
				} else {
					List<Show> response = (List<Show>) resultData.get(TVDBService.EXTRA_OUTPUT_DATA);
					mShow = response.get(0);
					initializeData();
				}
			} else {
				int detailedResultCode = resultData.getInt(AbstractIntentService.EXTRA_OUTPUT_DETAILED_RESULT_CODE);
				if (detailedResultCode == AbstractIntentService.PARSING_ERROR) {
					Toast.makeText(ShowDetailActivity.this, R.string.no_result_for_showname, Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(ShowDetailActivity.this, "Unable to get result from service", Toast.LENGTH_SHORT).show();
				}
			}
		}
	}

}