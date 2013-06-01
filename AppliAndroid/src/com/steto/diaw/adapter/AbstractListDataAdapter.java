package com.steto.diaw.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

public abstract class AbstractListDataAdapter<T> extends BaseAdapter {

	protected List<T> mData;
	protected Context mContext;

	public AbstractListDataAdapter(Context ctx, List<T> all) {
		mContext = ctx;
		mData = all;
	}

	@Override
	public int getCount() {
		return mData.size();
	}

	@Override
	public Object getItem(int i) {
		return mData.get(i);
	}

	@Override
	public abstract long getItemId(int i);

	@Override
	public abstract View getView(int i, View view, ViewGroup viewGroup);
}
