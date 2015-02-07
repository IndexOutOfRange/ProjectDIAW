package com.steto.diaw.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.steto.diaw.model.Show;
import com.steto.diaw.view.ListItemViewShow;
import com.steto.diaw.view.ListItemViewShowForAmbiguity;

import java.util.List;

public class ListShowAdapterForAmbiguity extends AbstractSelectableItemListAdapter<Show> {


	public ListShowAdapterForAmbiguity(Context ctx, List<Show> all) {
		super(ctx, all);
	}

	@Override
	public View getView(int i, View view, ViewGroup viewGroup) {
		ListItemViewShowForAmbiguity current;
		if (view == null || !(view instanceof ListItemViewShow)) {
			current = new ListItemViewShowForAmbiguity(mContext);
		} else {
			current = (ListItemViewShowForAmbiguity) view;
		}
		current.setData(getItem(i));
		return current;
	}
}
