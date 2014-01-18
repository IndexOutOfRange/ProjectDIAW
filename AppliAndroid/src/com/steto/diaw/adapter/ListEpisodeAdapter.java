package com.steto.diaw.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.steto.diaw.model.Episode;
import com.steto.diaw.view.ListItemViewEpisode;

public class ListEpisodeAdapter extends AbstractSelectableItemListAdapter<Episode> {

	public ListEpisodeAdapter(Context ctx, List<Episode> all) {
		super(ctx, all);
	}

	@Override
	public View getView(int i, View view, ViewGroup viewGroup) {
		ListItemViewEpisode current;
		if (view == null || !(view instanceof ListItemViewEpisode)) {
			current = new ListItemViewEpisode(mContext);
		} else {
			current = (ListItemViewEpisode) view;
		}
		current.setData(mData.get(i));

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
