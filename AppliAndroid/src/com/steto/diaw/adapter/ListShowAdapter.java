package com.steto.diaw.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.steto.diaw.model.Show;
import com.steto.diaw.view.ListItemViewShow;

public class ListShowAdapter extends AbstractSelectableItemListAdapter<Show> {

	private boolean showSummary;

	public ListShowAdapter(Context ctx, List<Show> all) {
		this(ctx, all, false);
	}
	public ListShowAdapter(Context ctx, List<Show> all, boolean showSummary) {
		super(ctx, all);
		this.showSummary = showSummary;
	}
	
	@Override
	public View getView(int i, View view, ViewGroup viewGroup) {
		ListItemViewShow current;
		if (view == null || !(view instanceof ListItemViewShow)) {
			current = new ListItemViewShow(mContext);
		} else {
			current = (ListItemViewShow) view;
		}
		current.setData(getItem(i), showSummary);

		if (mCheckedItems.contains(Integer.valueOf(i))) {
			// if this item is checked - set checked state
			current.setLayoutBackgroundState(new int[] { android.R.attr.state_checked });
		} else {
			// if this item is unchecked - set unchecked state (notice the minus)
			current.setLayoutBackgroundState(new int[] { -android.R.attr.state_checked });
		}
		return current;
	}
}
