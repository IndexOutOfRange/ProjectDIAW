package com.steto.diaw.view;

import android.content.Context;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.steto.diaw.model.Episode;
import com.steto.projectdiaw.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class ListItemViewPlanning extends LinearLayout {

	private LinearLayout mLinearLayout;
	private TextView mNumberTextView;
	private TextView mNameTextView;
	private TextView mInfoTextView;
	private LinearLayout mDateLinearLayout;
	private TextView mDateTextView;
	private String mToday;
	private String mYesterday;
	private String mTomorrow;

	public ListItemViewPlanning(Context context) {
		super(context);

		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.listitem_planning_view, this);

		mLinearLayout = (LinearLayout) findViewById(R.id.listitem_planning_episode_layout);
		mDateLinearLayout = (LinearLayout) findViewById(R.id.listitem_planning_date_layout);
		mNumberTextView = (TextView) findViewById(R.id.listitem_planning_episode_number);
		mNameTextView = (TextView) findViewById(R.id.listitem_planning_episode_name);
		mInfoTextView = (TextView) findViewById(R.id.listitem_planning_episode_info);
		mDateTextView = (TextView) findViewById(R.id.listitem_planning_date_tv);
		
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
		mToday = sdf.format(calendar.getTime());
		calendar.add(Calendar.DATE, -1);
		mYesterday = sdf.format(calendar.getTime());		
		calendar.add(Calendar.DATE, 2);
		mTomorrow = sdf.format(calendar.getTime());
	}

	public void setData(Episode episode, boolean showDate) {
		if(showDate) {
			mDateLinearLayout.setVisibility(VISIBLE);			
			if(mToday.equals(episode.getFirstAired())) {
				mDateTextView.setText(R.string.today);
			} else if(mYesterday.equals(episode.getFirstAired())) {
				mDateTextView.setText(R.string.yesterday);
			} else if(mTomorrow.equals(episode.getFirstAired())) {
				mDateTextView.setText(R.string.tomorrow);
			}else {
				mDateTextView.setText(episode.getFirstAired());
			}
		} else {
			mDateLinearLayout.setVisibility(GONE);
		}
		
		mNumberTextView.setText(formatNumberForLayout(episode.getSeasonNumber())
				+ "x" + formatNumberForLayout(episode.getEpisodeNumber()));

		if (!TextUtils.isEmpty(episode.getShowName())) {
			mNameTextView.setText(episode.getShowName());
		}
		mInfoTextView.setText("");
		boolean episodeName = false;
		if (!TextUtils.isEmpty(episode.getEpisodeName())) {
			mInfoTextView.append(episode.getEpisodeName());
			episodeName = true;
		}
		if (episode.getUpdatedAt() != null) {
			if (episodeName) {
				mInfoTextView.append(" - ");
			}
			mInfoTextView.append(DateFormat.format("yyyy-MM-dd", episode.getUpdatedAt()));
		}
	}

	private String formatNumberForLayout(int number) {
		if (number < 10) {
			return "0" + String.valueOf(number);
		}
		return String.valueOf(number);
	}

	public void setLayoutBackgroundState(int[] state) {
		mLinearLayout.getBackground().setState(state);
	}
}
