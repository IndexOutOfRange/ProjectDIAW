package com.steto.diaw.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

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
	public T getItem(int i) {
		if (i < getCount()) {
			return mData.get(i);
		} else {
			return null;
		}
	}

	// see
	// http://grepcode.com/file/repository.grepcode.com/java/ext/com.google.android/android/4.2.2_r1/android/widget/ArrayAdapter.java#ArrayAdapter.getItemId%28int%29
	@Override
	public long getItemId(int i) {
		return i;
	}

	@Override
	public abstract View getView(int i, View view, ViewGroup viewGroup);
}
