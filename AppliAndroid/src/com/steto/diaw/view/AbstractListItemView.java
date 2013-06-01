package com.steto.diaw.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.steto.projectdiaw.R;

public abstract class AbstractListItemView<T> extends LinearLayout {

	protected Context mContext;
	protected T mData;
	protected TextView mLeftTV;
	protected TextView mRightTV;


	public AbstractListItemView(Context context) {
		super(context);
		mContext = context;

		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.listitem_view, this);

		mLeftTV = (TextView) findViewById(R.id.listitem_left_tv);
		mRightTV = (TextView) findViewById(R.id.listitem_right_tv);
	}

	public abstract void setData(T data);
}
