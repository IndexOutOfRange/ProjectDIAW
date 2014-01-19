package com.steto.diaw.view;

import android.content.Context;

import com.steto.diaw.model.Show;

public class ListItemViewShow extends AbstractListItemView<Show> {

	private boolean showSummary;

	public ListItemViewShow(Context context) {
		super(context);
	}

	public void setData(Show show, boolean showSummary) {
		this.showSummary = showSummary;
		setData(show);
	}

	public void setData(Show show) {
		if (show != null) {
			mData = show;
			mLeftTV.setText(mData.getShowName());
			mRightTV.setVisibility(GONE);
			if (showSummary) {
				mCenterTV.setVisibility(VISIBLE);
				mCenterTV.setText(mData.getResume());
			} else {
				mCenterTV.setVisibility(GONE);
			}
		}
	}

	public void setLayoutBackgroundState(int[] state) {
		mLinearLayout.getBackground().setState(state);
	}
}
