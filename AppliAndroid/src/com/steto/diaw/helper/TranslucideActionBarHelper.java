package com.steto.diaw.helper;

import android.app.ActionBar;
import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;
import android.widget.AbsListView;

public class TranslucideActionBarHelper {

	private ActionBar mActionBar;
	private Drawable mActionBarBackgroundDrawable;
	private View mHeaderContainer;

	public TranslucideActionBarHelper(ActionBar actionBar) {
		mActionBar = actionBar;
	}

	public void initActionBar(Activity activity, String title, String subtitle, boolean displayHomeAsUp, int ressourceAB) {
		if (!"".equals(title)) {
			mActionBar.setTitle(title);
		}
		if (!"".equals(subtitle)) {
			mActionBar.setSubtitle(subtitle);
		}
		mActionBar.setDisplayHomeAsUpEnabled(displayHomeAsUp);

		mActionBarBackgroundDrawable = activity.getResources().getDrawable(ressourceAB);
		mActionBar.setBackgroundDrawable(mActionBarBackgroundDrawable);

		if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN) {
			mActionBarBackgroundDrawable.setCallback(mDrawableCallback);
		}
		mActionBarBackgroundDrawable.setAlpha(0);
	}

	private Drawable.Callback mDrawableCallback = new Drawable.Callback() {

		@Override
		public void invalidateDrawable(Drawable who) {
			mActionBar.setBackgroundDrawable(who);
		}

		@Override
		public void scheduleDrawable(Drawable who, Runnable what, long when) {}

		@Override
		public void unscheduleDrawable(Drawable who, Runnable what) {}
	};

	public void setHeaderContainer(View headerContainer) {
		this.mHeaderContainer = headerContainer;
	}

	/**
	 * Listener pour les ExpandableListView et les ListView
	 */
	public void setOnScrollChangedListener(AbsListView view) {
		view.setOnScrollListener(mOnScrollChangedListener);

	}

	private AbsListView.OnScrollListener mOnScrollChangedListener = new AbsListView.OnScrollListener() {

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {}

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
			View topChild = view.getChildAt(0);
			if (topChild == null) {
				onNewScroll(0);
			} else if (topChild != mHeaderContainer) {
				onNewScroll(mHeaderContainer.getHeight());
			} else {
				onNewScroll(-topChild.getTop());
			}
		}
	};

	/* TODO faire le meme traitement pour les ScrollView */

	private void onNewScroll(int scrollPosition) {
		if (mActionBar == null) {
			return;
		}
		int headerHeight = mHeaderContainer.getHeight() - mActionBar.getHeight();
		float ratio = (float) Math.min(Math.max(scrollPosition, 0), headerHeight) / headerHeight;
		int newAlpha = (int) (ratio * 255);
		mActionBarBackgroundDrawable.setAlpha(newAlpha);
	}
}
