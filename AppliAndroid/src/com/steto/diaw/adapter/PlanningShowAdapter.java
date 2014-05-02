package com.steto.diaw.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.steto.diaw.model.Episode;
import com.steto.diaw.view.ListItemViewPlanning;
import com.steto.diaw.view.ListItemViewShow;

public class PlanningShowAdapter extends BaseAdapter {

	private List<Episode> mEpisodeList = new ArrayList<Episode>();
	private Context mContext;

	public PlanningShowAdapter(Context context) {
		mContext = context;
	}

	@Override
	public int getCount() {
		return getEpisodeList().size();
	}

	@Override
	public Episode getItem(int position) {
		return getEpisodeList().get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View view, ViewGroup viewGroup) {
		ListItemViewPlanning current;
		if (view == null || !(view instanceof ListItemViewShow)) {
			current = new ListItemViewPlanning(mContext);
		} else {
			current = (ListItemViewPlanning) view;
		}
		current.setData(getItem(position), isFirstEpisodeForDate(position));
		return current;
	}

	public List<Episode> getEpisodeList() {
		return mEpisodeList;
	}

	public void setEpisodeList(List<Episode> episodeList) {
		if (episodeList != null) {
			mEpisodeList.clear();
			mEpisodeList.addAll(episodeList);
			notifyDataSetChanged();
		}
	}
	
	private boolean isFirstEpisodeForDate(int position) {
		if (position == 0) {
			return true;
		}
		
		Episode currentItem = getItem(position);
		Episode previousItem = getItem(position - 1);
		
		return currentItem.getFirstAired().compareTo(previousItem.getFirstAired()) == 0 ? false : true;
	}

}
