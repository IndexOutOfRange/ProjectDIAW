package com.steto.diaw.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.steto.diaw.model.Season;
import com.steto.projectdiaw.R;

public class ListItemSeasonGroup extends LinearLayout {

	private TextView mTextView;

	public ListItemSeasonGroup(Context context) {
		super(context);

		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.listitem_season_view, this);
		mTextView = (TextView) findViewById(R.id.listitem_season_tv);
	}

	public void setData(Season season) {
		String text = String.format(getContext().getString(R.string.season_text_format), season.getNumber(), season.getEpisodes().size());
		mTextView.setText(text);
	}
}
