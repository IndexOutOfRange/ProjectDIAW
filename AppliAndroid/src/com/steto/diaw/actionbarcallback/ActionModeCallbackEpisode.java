package com.steto.diaw.actionbarcallback;

import android.app.Activity;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;

import com.steto.diaw.activity.EpisodesSeenActivity;
import com.steto.diaw.adapter.ListEpisodeAdapter;
import com.steto.diaw.model.Episode;
import com.steto.projectdiaw.R;

/**
 * Created by Benjamin on 09/06/13.
 */
public class ActionModeCallbackEpisode implements ActionMode.Callback {

	private Activity mActivity;
	private ActionMode mActionMode;
	private ListEpisodeAdapter mAdapter;

	private ActionModeCallbackEpisode(Activity activity, ListEpisodeAdapter adapter) {
		this.mActivity = activity;
		this.mAdapter = adapter;
	}

	private static ActionModeCallbackEpisode instance;

	public static ActionModeCallbackEpisode getInstance(Activity act, ListEpisodeAdapter adap) {
		if(instance == null || instance.mActivity != act ||instance.mAdapter != adap ) {
			instance = new ActionModeCallbackEpisode(act, adap);
		} return instance;
	}

	@Override
	public boolean onCreateActionMode(ActionMode mode, Menu menu) {
		mAdapter.enterMultiMode();
		// save global action mode
		mActionMode = mode;
		return true;
	}

	@Override
	public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
		// remove previous items
		menu.clear();
		final int checked = mAdapter.getCheckedItemCount();
		// update title with number of checked items
		mode.setTitle(checked + " " + mActivity.getResources().getQuantityString(R.plurals.nb_items_selected, checked));
		switch (checked) {
			case 0:
				// if nothing checked - exit action mode
				mode.finish();
				return true;
			case 1:
				// all items - rename + delete
				mActivity.getMenuInflater().inflate(R.menu.context_menu, menu);
				return true;
			default:
				mActivity.getMenuInflater().inflate(R.menu.context_menu, menu);
				// remove rename option - because we have more than one selected
				menu.removeItem(R.id.context_menu_rename);
				return true;
		}
	}

	@Override
	public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
		switch (item.getItemId()) {
			case R.id.context_menu_rename:
				((EpisodesSeenActivity) mActivity).renameEpisode((Episode) mAdapter.getFirstCheckedItem());
				mode.finish();
				return true;

			case R.id.context_menu_delete:
				((EpisodesSeenActivity) mActivity).deleteEpisodes();
				mode.finish();
				return true;
			default:
				return false;
		}
	}

	@Override
	public void onDestroyActionMode(ActionMode mode) {
		mAdapter.exitMultiMode();
		mActionMode = null;
	}

	public ActionMode getActionMode() {
		return mActionMode;
	}
}
