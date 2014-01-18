package com.steto.diaw.view;

import android.content.Context;

import com.steto.diaw.model.Episode;

public class ListItemViewEpisode extends AbstractListItemView<Episode> {

	public ListItemViewEpisode(Context context) {
		super(context);
	}

	public void setData(Episode ep) {
		mData = ep;
		// TODO utiliser un string des ressources
		String sub = "Saison " + String.valueOf(mData.getSeasonNumber()) + " Episode " + String.valueOf(mData.getEpisodeNumber());
		if (mLeftTV != null)
			mLeftTV.setText(ep.getShowName());
		if (mRightTV != null)
			mRightTV.setText(sub);
	}

	public void setLayoutBackgroundState(int[] state) {
		mLinearLayout.getBackground().setState(state);
	}
}
