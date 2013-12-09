package com.steto.diaw.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import com.steto.diaw.model.Show;
import com.steto.diaw.view.ListItemViewShow;

import java.util.List;

public class ListShowAdapter extends AbstractSelectableItemListAdapter<Show> {

	public ListShowAdapter(Context ctx, List<Show> all) {
		super(ctx, all);
	}

	@Override
	public View getView(int i, View view, ViewGroup viewGroup) {
		ListItemViewShow current;
		if (view == null || !(view instanceof ListItemViewShow)) {
			current = new ListItemViewShow(mContext);
		} else {
			current = (ListItemViewShow) view;
		}
		current.setData(getItem(i));

        if (mCheckedItems.contains(Integer.valueOf(i))) {
            // if this item is checked - set checked state
            current.setLayoutBackgroundState(new int[]{android.R.attr.state_checked});
        } else {
            // if this item is unchecked - set unchecked state (notice the minus)
            current.setLayoutBackgroundState(new int[]{-android.R.attr.state_checked});
        }
		return current;
	}
}
