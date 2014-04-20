package com.steto.diaw.view;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.steto.diaw.model.Episode;
import com.steto.projectdiaw.R;

public class ListItemEpisodeExpendable extends LinearLayout {

	private TextView mNumberTextView;
	private TextView mNameTextView;
	private TextView mInfoTextView;
	private View mSeenView;

	public ListItemEpisodeExpendable(Context context) {
		super(context);

		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.listitem_episode_detail_view, this);
		mNumberTextView = (TextView) findViewById(R.id.listitem_episode_number);
		mNameTextView = (TextView) findViewById(R.id.listitem_episode_name);
		mInfoTextView = (TextView) findViewById(R.id.listitem_episode_info);
		mSeenView = findViewById(R.id.listitem_episode_seen);

	}

	public void setData(Episode episode) {
		mNumberTextView.setText(formatNumberForLayout(episode.getEpisodeNumber()));

		if (!TextUtils.isEmpty(episode.getEpisodeName())) {
			mNameTextView.setText(episode.getEpisodeName());
		}
		if (!TextUtils.isEmpty(episode.getFirstAired())) {
			mInfoTextView.setText(episode.getFirstAired());
		}
		if (episode.isSeen()) {
			mSeenView.setBackgroundColor(getResources().getColor(R.color.holo_green_dark));
		} else {
			mSeenView.setBackgroundColor(getResources().getColor(R.color.holo_red_dark));
		}
	}

	private String formatNumberForLayout(int number) {
		if (number < 10) {
			return "0" + String.valueOf(number);
		}
		return String.valueOf(number);
	}
}
