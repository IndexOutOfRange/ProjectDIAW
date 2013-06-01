package com.steto.diaw.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import com.steto.diaw.model.Episode;
import com.steto.diaw.view.ListItemViewEpisode;

import java.util.List;

public class ListEpisodeHomeAdapter extends AbstractListDataAdapter<Episode> {

	public ListEpisodeHomeAdapter(Context ctx, List<Episode> all) {
		super(ctx, all);
	}

	@Override
	public long getItemId(int i) {
		return mData.get(i).getId();
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

		return current;
	}
}
