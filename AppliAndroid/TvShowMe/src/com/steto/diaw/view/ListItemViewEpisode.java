package com.steto.diaw.view;

import android.content.Context;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.steto.diaw.model.Episode;
import com.steto.projectdiaw.R;

public class ListItemViewEpisode extends LinearLayout {

	private LinearLayout mLinearLayout;
	private TextView mNumberTextView;
	private TextView mNameTextView;
	private TextView mInfoTextView;

	public ListItemViewEpisode(Context context) {
		super(context);

		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.listitem_episodes_view, this);

		mLinearLayout = (LinearLayout) findViewById(R.id.listitem_episode_layout);
		mNumberTextView = (TextView) findViewById(R.id.listitem_episode_number);
		mNameTextView = (TextView) findViewById(R.id.listitem_episode_name);
		mInfoTextView = (TextView) findViewById(R.id.listitem_episode_info);
	}

	public void setData(Episode episode) {
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
