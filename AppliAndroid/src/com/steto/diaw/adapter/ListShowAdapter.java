package com.steto.diaw.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import com.steto.diaw.model.Show;
import com.steto.diaw.view.ListItemViewShow;

import java.util.List;

public class ListShowAdapter extends AbstractListDataAdapter<Show> {

	public ListShowAdapter(Context ctx, List<Show> all) {
		super(ctx, all);
	}

	@Override
	public long getItemId(int i) {
		return mData.get(i).getId();
	}

	@Override
	public View getView(int i, View view, ViewGroup viewGroup) {
		ListItemViewShow current;
		if (view == null || !(view instanceof ListItemViewShow)) {
			current = new ListItemViewShow(mContext);
		} else {
			current = (ListItemViewShow) view;
		}
		current.setData(mData.get(i));
		return current;
	}
}
