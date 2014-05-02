package com.steto.diaw.activity.model;

import roboguice.activity.RoboActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.google.inject.Inject;
import com.steto.projectdiaw.R;

@ContentView(R.layout.activity_main)
public abstract class DrawerActivity extends RoboActivity {

	protected static final int DRAWER_DERNIERS_EPISODES = 0;
	protected static final int DRAWER_MES_SERIES = 1;
	protected static final int DRAWER_PLANNING_SERIES = 2;

	@InjectView(R.id.drawer_layout)
	private DrawerLayout mDrawerLayout;
	@InjectView(R.id.left_drawer)
	private ListView mDrawerList;
	@InjectView(R.id.content_frame)
	private FrameLayout mContentFrame;

	@Inject
	private LayoutInflater mLayoutInflater;
	private ActionBarDrawerToggle mDrawerToggle;

	private String[] mDrawerTitles;
	private CharSequence mDrawerTitle;
	private CharSequence mTitle;

	protected View mContentView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		manageDrawer();
		mContentView = mLayoutInflater.inflate(getLayoutContentFrame(), mContentFrame);
	}

	@Override
	protected void onResume() {
		super.onResume();
		selectItem(getSelectedItem());
	}

	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		getActionBar().setTitle(mTitle);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		manageIconsInActionBar(menu, isDrawerOpen());
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	protected abstract int getLayoutContentFrame();

	protected abstract int getSelectedItem();

	protected abstract void manageIconsInActionBar(Menu menu, boolean drawerOpen);

	protected abstract void manageDrawerItemClick(int position);

	private void manageDrawer() {
		mTitle = mDrawerTitle = getTitle();
		mDrawerTitles = getResources().getStringArray(R.array.drawer_array);

		// set a custom shadow that overlays the main content when the drawer opens
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
		// set up the drawer's list view with items and click listener
		mDrawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item, mDrawerTitles));
		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);

		// ActionBarDrawerToggle ties together the the proper interactions
		// between the sliding drawer and the action bar app icon
		mDrawerToggle = new ActionBarDrawerToggle(
				this, /* host Activity */
				mDrawerLayout, /* DrawerLayout object */
				R.drawable.ic_drawer, /* nav drawer image to replace 'Up' caret */
				R.string.drawer_open, /* "open drawer" description for accessibility */
				R.string.drawer_close /* "close drawer" description for accessibility */
				) {

					public void onDrawerClosed(View view) {
						getActionBar().setTitle(mTitle);
						invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
					}

					public void onDrawerOpened(View drawerView) {
						getActionBar().setTitle(mDrawerTitle);
						invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
					}
				};
		mDrawerLayout.setDrawerListener(mDrawerToggle);
	}

	private boolean isDrawerOpen() {
		return mDrawerLayout.isDrawerOpen(mDrawerList);
	}

	private void selectItem(int position) {
		mDrawerList.setItemChecked(position, true);
		setTitle(mDrawerTitles[position]);
		mDrawerLayout.closeDrawer(mDrawerList);
	}

	private void onDrawerItemClick(int position) {
		selectItem(position);
		manageDrawerItemClick(position);
	}

	private class DrawerItemClickListener implements ListView.OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			onDrawerItemClick(position);
		}
	}

	/*
	 * When using the ActionBarDrawerToggle, we must call it during onPostCreate() and onConfigurationChanged()...
	 */
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggles
		mDrawerToggle.onConfigurationChanged(newConfig);
	}
}
