package com.steto.diaw.view;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.steto.diaw.model.Show;
import com.steto.projectdiaw.R;

public class ListItemViewShow extends LinearLayout {

	private LinearLayout mLinearLayout;
	private TextView mNameTextView;
	private TextView mInfoTextView;

	private boolean ambiguity;

	public ListItemViewShow(Context context) {
		super(context);

		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.listitem_show_view, this);

		mLinearLayout = (LinearLayout) findViewById(R.id.listitem_show_layout);
		mNameTextView = (TextView) findViewById(R.id.listitem_show_name_tv);
		mInfoTextView = (TextView) findViewById(R.id.listitem_show_info_tv);

	}

	public void setData(Show show, boolean ambiguity) {
		this.ambiguity = ambiguity;
		setData(show);
	}

	public void setData(Show show) {
		if (show == null) {
			return;
		}
		if (!TextUtils.isEmpty(show.getShowName())) {
			mNameTextView.setText(show.getShowName());
		}

		if (ambiguity) {
			mInfoTextView.setText(show.getResume());
		} else {
			if(show.getNumberEpisodes() == 0) {
				mInfoTextView.setText(String.format(getResources().getString(R.string.show_without_data_format_d), show.getNumberEpisodesSaw()));			
			} else {
				mInfoTextView.setText(String.format(getResources().getString(R.string.episode_seen_total_format_d_d), show.getNumberEpisodesSaw(), show.getNumberEpisodes()));				
			}
		}
	}

	public void setLayoutBackgroundState(int[] state) {
		mLinearLayout.getBackground().setState(state);
	}
}
