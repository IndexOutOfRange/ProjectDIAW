package com.steto.diaw.view;

import android.content.Context;
import com.steto.diaw.model.Show;

public class ListItemViewShow extends AbstractListItemView<Show> {

	public ListItemViewShow(Context context) {
		super(context);
	}

	public void setData(Show show) {
		mData = show;
		if (mLeftTV != null) mLeftTV.setText(mData.getShowName());
		if (mRightTV != null) mRightTV.setVisibility(GONE);
	}
}
