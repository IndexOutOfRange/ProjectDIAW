package com.steto.diaw.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import com.steto.diaw.model.Episode;
import com.steto.diaw.view.ListItemViewEpisode;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ListEpisodeHomeAdapter extends AbstractListDataAdapter<Episode> {

	private HashSet<Integer> checkedItems;
	private boolean multiMode;

	public ListEpisodeHomeAdapter(Context ctx, List<Episode> all) {
		super(ctx, all);
		checkedItems = new HashSet<Integer>();
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

		if (checkedItems.contains(Integer.valueOf(i))) {
			// if this item is checked - set checked state
			current.setLayoutBackgroundState(new int[] { android.R.attr.state_checked });
		} else {
			// if this item is unchecked - set unchecked state (notice the minus)
			current.setLayoutBackgroundState(new int[] { -android.R.attr.state_checked });
		}

		return current;
	}

	public void enterMultiMode() {
		multiMode = true;
		notifyDataSetChanged();
	}

	public void exitMultiMode() {
		checkedItems.clear();
		multiMode = false;
		notifyDataSetChanged();
	}

	public void setChecked(int pos, boolean checked) {
		if (checked) {
			checkedItems.add(Integer.valueOf(pos));
		} else {
			checkedItems.remove(Integer.valueOf(pos));
		}
		if (multiMode) {
			notifyDataSetChanged();
		}
	}

	public boolean isChecked(int pos) {
		return checkedItems.contains(Integer.valueOf(pos));
	}

	public void toggleChecked(int pos) {
		final Integer v = Integer.valueOf(pos);
		if (checkedItems.contains(v)) {
			checkedItems.remove(v);
		} else {
			checkedItems.add(v);
		}
		this.notifyDataSetChanged();
	}

	public int getCheckedItemCount() {
		return checkedItems.size();
	}

	public Episode getFirstCheckedItem() {
		for (Integer i : checkedItems) {
			return mData.get(i.intValue());
		}
		return null;
	}

	public Set<Integer> getCheckedItems() {
		return checkedItems;
	}
}
