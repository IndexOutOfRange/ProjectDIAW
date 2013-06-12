package com.steto.diaw.actionbarcallback;

import android.app.Activity;
import com.actionbarsherlock.app.SherlockListActivity;
import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import com.steto.diaw.activity.HomeActivity;
import com.steto.diaw.adapter.ListEpisodeHomeAdapter;
import com.steto.diaw.model.Episode;
import com.steto.projectdiaw.R;

/**
 * Created by Benjamin on 09/06/13.
 */
public class ActionModeCallbackEpisode implements ActionMode.Callback {

	private final String TAG = "ActionModeCallbackEpisode";
	private Activity activity;
	private ActionMode actionMode;
	private ListEpisodeHomeAdapter mAdapter;

	public ActionModeCallbackEpisode(Activity activity, ListEpisodeHomeAdapter mAdapter) {
		this.activity = activity;
		this.mAdapter = mAdapter;
	}

	@Override
	public boolean onCreateActionMode(ActionMode mode, Menu menu) {
		mAdapter.enterMultiMode();
		// save global action mode
		actionMode = mode;
		return true;
	}

	@Override
	public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
		// remove previous items
		menu.clear();
		final int checked = mAdapter.getCheckedItemCount();
		// update title with number of checked items
		mode.setTitle(checked + " " + activity.getResources().getQuantityString(R.plurals.nb_items_selected, checked));
		switch (checked) {
			case 0:
				// if nothing checked - exit action mode
				mode.finish();
				return true;
			case 1:
				// all items - rename + delete
				((SherlockListActivity) activity).getSupportMenuInflater().inflate(R.menu.context_menu, menu);
				return true;
			default:
				((SherlockListActivity) activity).getSupportMenuInflater().inflate(R.menu.context_menu, menu);
				// remove rename option - because we have more than one selected
				menu.removeItem(R.id.context_menu_rename);
				return true;
		}
	}

	@Override
	public boolean onActionItemClicked(ActionMode mode, com.actionbarsherlock.view.MenuItem item) {
		switch (item.getItemId()) {
			case R.id.context_menu_rename:
				((HomeActivity) activity).renameEpisode((Episode) mAdapter.getFirstCheckedItem());
				mode.finish();
				return true;

			case R.id.context_menu_delete:
				((HomeActivity) activity).deleteEpisodes();
				mode.finish();
				return true;
			default:
				return false;
		}
	}

	@Override
	public void onDestroyActionMode(ActionMode mode) {
		mAdapter.exitMultiMode();
		actionMode = null;
	}

	public ActionMode getActionMode() {
		return actionMode;
	}
}
