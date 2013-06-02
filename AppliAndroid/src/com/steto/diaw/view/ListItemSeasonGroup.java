package com.steto.diaw.view;

import android.content.Context;
import com.steto.diaw.model.Season;
import com.steto.projectdiaw.R;

public class ListItemSeasonGroup extends AbstractListItemView<Season> {

	public ListItemSeasonGroup(Context context) {
		super(context);
	}

	public void setData(Season season) {
		mData = season;
		// TODO utiliser un string des ressources
		String text = "Saison " + String.valueOf(mData.getNumber());
		if (mLeftTV != null) {
			mLeftTV.setText(text);
			mLeftTV.setPadding(getResources().getDimensionPixelSize(R.dimen.small), mLeftTV.getPaddingTop(), mLeftTV.getPaddingRight(), mLeftTV.getPaddingBottom());
		}
		if (mRightTV != null) mRightTV.setVisibility(GONE);
	}
}
