package com.steto.diaw.view;

import android.content.Context;

import com.steto.diaw.model.Show;

public class ListItemViewShow extends AbstractListItemView<Show> {

	private boolean ambiguity;

	public ListItemViewShow(Context context) {
		super(context);
	}

	public void setData(Show show, boolean ambiguity) {
		this.ambiguity = ambiguity;
		setData(show);
	}

	public void setData(Show show) {
		if (show != null) {
			mData = show;
			mLeftTV.setText(mData.getShowName());
			if (ambiguity) {
				mRightTV.setVisibility(GONE);
				mCenterTV.setVisibility(VISIBLE);
				mCenterTV.setText(mData.getResume());
			} else {
				int nbEpisodes = show.getNumberEpisodes();
				if (nbEpisodes != 0) {
					mRightTV.setVisibility(VISIBLE);
					mRightTV.setText(String.valueOf(nbEpisodes));
				} else {
					mRightTV.setVisibility(GONE);
				}
				mCenterTV.setVisibility(GONE);
			}
		}
	}

	public void setLayoutBackgroundState(int[] state) {
		mLinearLayout.getBackground().setState(state);
	}
}
