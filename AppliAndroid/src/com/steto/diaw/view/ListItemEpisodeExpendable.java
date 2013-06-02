package com.steto.diaw.view;

import android.content.Context;
import com.steto.diaw.model.Episode;

public class ListItemEpisodeExpendable extends AbstractListItemView<Episode> {

	public ListItemEpisodeExpendable(Context context) {
		super(context);
	}

	public void setData(Episode episode) {
		mData = episode;
		// TODO utiliser un string des ressources
		String text = "Episode " + String.valueOf(mData.getEpisodeNumber());
		if (mLeftTV != null) mLeftTV.setText(text);
		if (mRightTV != null) mRightTV.setVisibility(GONE);
	}
}
