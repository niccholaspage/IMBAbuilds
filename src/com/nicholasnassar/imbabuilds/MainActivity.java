package com.nicholasnassar.imbabuilds;

import com.nicholasnassar.imbabuilds.adapter.DrawerArrayAdapter;
import com.nicholasnassar.imbabuilds.adapter.Item;
import com.nicholasnassar.imbabuilds.fragments.BlankFragment;
import com.nicholasnassar.imbabuilds.fragments.BuildFragment;
import com.nicholasnassar.imbabuilds.fragments.HomeFragment;
import com.nicholasnassar.imbabuilds.fragments.RaceFragment;
import com.nicholasnassar.imbabuilds.fragments.TitledFragment;
import com.nicholasnassar.imbabuilds.fragments.UpdatableListFragment;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

public class MainActivity extends FragmentActivity {
	private int lastItem;

	private String[] options;

	private ListView mDrawer;
	private DrawerLayout mDrawerLayout;
	private ActionBarDrawerToggle mDrawerToggle;

	private CharSequence mTitle;
	private CharSequence mDrawerTitle;

	private final Handler handler = new Handler();

	private Drawable oldBackground = null;
	private int currentColor = 0xFF666666;

	private int status = OK;

	private static final int OK = 0;

	private static final int ERROR = 1;

	private AlertDialog dialog = null;

	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

		if (preferences.getBoolean("night_mode", false)){
			//setTheme(android.R.style.Theme_Holo);
		}else {
			//setTheme(android.R.style.Theme_Holo_Light_DarkActionBar);
		}

		setContentView(R.layout.activity_main);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			final ActionBar actionBar = getActionBar();

			actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		}

		mTitle = mDrawerTitle = getString(R.string.app_name);

		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

		mDrawer = (ListView) findViewById(R.id.left_drawer);

		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

		lastItem = 0;

		Race[] races = Race.values();

		options = new String[races.length + 1];

		options[0] = "Home";

		int i = 0;

		for (i = 0; i < races.length; i++){
			options[i + 1] = races[i].toString();
		}

		//TODO: Implement Guides:
		//options[i + 1] = "Guides";

		mDrawer.setAdapter(new DrawerArrayAdapter(this, R.layout.drawer_list_item, options));

		mDrawer.setOnItemClickListener(new DrawerItemClickListener());

		mDrawerToggle = new ActionBarDrawerToggle(this,
				mDrawerLayout,
				R.drawable.ic_navigation_drawer,
				R.string.drawer_open,
				R.string.drawer_close
				) {
			public void onDrawerClosed(View view) {
				Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.content_frame);

				int position = mDrawer.getCheckedItemPosition();

				if (fragment instanceof TitledFragment){
					setTitle(((TitledFragment) fragment).getTitle());

					if ((((TitledFragment) fragment).isLowerLevel())){
						mDrawerToggle.setDrawerIndicatorEnabled(false);
					}
				}else {
					setTitle(options[position]);
				}

				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
					invalidateOptionsMenu();
				}

				if (fragment instanceof BlankFragment){
					replaceFragment();
				}

				if (!isInLowerLevelFragment()){
					lastItem = mDrawer.getCheckedItemPosition();
				}
			}

			public void onDrawerOpened(View drawerView) {
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
					getActionBar().setTitle(mDrawerTitle);

					invalidateOptionsMenu();
				}

				if (!isInLowerLevelFragment() && mDrawer.getCheckedItemPosition() != -1){
					lastItem = mDrawer.getCheckedItemPosition();
				}

				mDrawerToggle.setDrawerIndicatorEnabled(true);
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);


		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH){
			getActionBar().setHomeButtonEnabled(true);
		}

		if (savedInstanceState == null) {
			selectItem(0);

			refresh();
		}else {
			lastItem = savedInstanceState.getInt("last_item");

			currentColor = savedInstanceState.getInt("currentColor");

			changeColor(currentColor);

			status = savedInstanceState.getInt("status");

			setTitle(savedInstanceState.getCharSequence("title"));
		}

		Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.content_frame);

		if (fragment instanceof TitledFragment && ((TitledFragment) fragment).isLowerLevel()){
			mDrawerToggle.setDrawerIndicatorEnabled(false);
		}else {
			mDrawerToggle.setDrawerIndicatorEnabled(true);
		}
	}

	public void showAds(){
		LinearLayout contentView = (LinearLayout) findViewById(R.id.content);

		if (!isPro()){
			contentView.findViewById(R.id.adView).setVisibility(View.VISIBLE);
		}
	}

	public void removeAds(){
		LinearLayout contentView = (LinearLayout) findViewById(R.id.content);

		contentView.findViewById(R.id.adView).setVisibility(View.GONE);
	}

	private boolean isPro(){
		SharedPreferences preferences = getPreferences(MODE_PRIVATE);

		if (preferences.getBoolean("freeproversion", false)){
			return true;
		}

		return getPackageManager().checkSignatures(getPackageName(), "com.nicholasnassar.imbabuildsprounlocker") == PackageManager.SIGNATURE_MATCH;
	}

	@Override
	protected void onResume(){
		super.onResume();

		DataRetrieverTask task = ((MainApplication) getApplication()).getCurrentTask();

		if (task != null){
			task.setActivity(this);
		}

		if (status == ERROR){
			showAlert();
		}

		if (!isPro()){
			showAds();
		}else {
			removeAds();
		}
	}

	@Override
	protected void onPause(){
		super.onPause();

		if (dialog != null){
			dialog.dismiss();
		}
	}

	private boolean isInLowerLevelFragment(){
		Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.content_frame);

		return (fragment instanceof TitledFragment) && ((TitledFragment) fragment).isLowerLevel();
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void changeColor(int newColor) {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
			return;
		}

		Drawable colorDrawable = new ColorDrawable(newColor);

		Drawable bottomDrawable = getResources().getDrawable(R.drawable.actionbar_bottom);

		LayerDrawable ld = new LayerDrawable(new Drawable[] { colorDrawable, bottomDrawable });

		if (oldBackground == null) {

			if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
				ld.setCallback(drawableCallback);
			} else {
				getActionBar().setBackgroundDrawable(ld);
			}

		} else {

			TransitionDrawable td = new TransitionDrawable(new Drawable[] { oldBackground, ld });

			if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
				td.setCallback(drawableCallback);
			} else {
				getActionBar().setBackgroundDrawable(td);
			}

			td.startTransition(200);

		}

		oldBackground = ld;

		getActionBar().setDisplayShowTitleEnabled(false);
		getActionBar().setDisplayShowTitleEnabled(true);

		currentColor = newColor;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		outState.putInt("chosen_option", mDrawer.getCheckedItemPosition());

		outState.putInt("last_item", lastItem);

		outState.putInt("currentColor", currentColor);

		outState.putInt("status", status);

		outState.putCharSequence("title", mTitle);
	}

	private Drawable.Callback drawableCallback = new Drawable.Callback() {
		@TargetApi(Build.VERSION_CODES.HONEYCOMB)
		@Override
		public void invalidateDrawable(Drawable who) {
			getActionBar().setBackgroundDrawable(who);
		}

		@Override
		public void scheduleDrawable(Drawable who, Runnable what, long when) {
			handler.postAtTime(what, when);
		}

		@Override
		public void unscheduleDrawable(Drawable who, Runnable what) {
			handler.removeCallbacks(what);
		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();

		inflater.inflate(R.menu.main, menu);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		/*boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawer);

		menu.findItem(R.id.action_search).setVisible(!drawerOpen);*/

		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}

		switch(item.getItemId()) {
		/*case R.id.action_search:
			return true;*/
		case android.R.id.home:
			lowerLevelFragmentBack();

			return true;
		case R.id.action_settings:
			Intent intent = new Intent(this, SettingsActivity.class);

			startActivity(intent);

			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void lowerLevelFragmentBack(){
		mDrawer.setItemChecked(lastItem, true);

		getSupportFragmentManager().popBackStack();

		int position = mDrawer.getCheckedItemPosition();

		calculateColor(position);

		mDrawerToggle.setDrawerIndicatorEnabled(true);

		setTitle(options[position]);

		getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	}

	private void calculateColor(int position){
		if (position == 0){
			changeColor(0xFF666666);
		}else if (position == 4){
			changeColor(0xFF96AA39);
		}else {
			changeColor(getRace().getColor());
		}
	}

	@Override
	public void onBackPressed(){
		if (mDrawerLayout.isDrawerOpen(mDrawer)){
			mDrawerLayout.closeDrawer(mDrawer);
		}else if (isInLowerLevelFragment()){
			lowerLevelFragmentBack();
		}else if (mDrawer.getCheckedItemPosition() != 0){
			lastItem = 0;

			selectItem(0);
		}else {
			finish();
		}
	}

	private class DrawerItemClickListener implements ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			selectItem(position, false);
		}
	}

	public void selectItem(int position){
		selectItem(position, true);
	}

	public void selectItem(int position, boolean boot) {
		mDrawer.setItemChecked(position, true);
		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			calculateColor(position);
		}

		if (boot){
			replaceFragment();
		}else if (lastItem != position || isInLowerLevelFragment()) {
			getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new BlankFragment()).commit();
		}

		setTitle(options[position]);

		if (lastItem == -1){
			lastItem = mDrawer.getCheckedItemPosition();
		}

		mDrawerToggle.setDrawerIndicatorEnabled(true);

		mDrawerLayout.closeDrawer(mDrawer);
	}

	private Fragment replaceFragment(){
		Fragment fragment;

		if (mDrawer.getCheckedItemPosition() == 0){
			fragment = new HomeFragment();
		}else if (mDrawer.getCheckedItemPosition() == 4){
			//fragment = GuidesFragment.newInstance();
			//TODO: Guides
			fragment = new BlankFragment();
		} else {
			fragment = RaceFragment.newInstance(getRace());
		}

		getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit();

		showAds();

		return fragment;
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;

		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
			return;
		}

		getActionBar().setTitle(title);
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);

		mDrawerToggle.syncState();

		if (mDrawerLayout.isDrawerOpen(mDrawer) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
			getActionBar().setTitle(mDrawerTitle);
		}
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);

		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	public Race getRace(){
		return Race.values()[mDrawer.getCheckedItemPosition() - 1];
	}

	public void showBuild(Item item, boolean overview){
		setTitle(item.getTitle());

		changeColor(item.getColor());

		mDrawerToggle.setDrawerIndicatorEnabled(false);

		mDrawer.setItemChecked(mDrawer.getCheckedItemPosition(), false);

		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

		transaction.replace(R.id.content_frame, BuildFragment.newInstance(item, overview));

		transaction.addToBackStack(null);

		transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);

		transaction.commit();
	}

	public void setLastItem(int lastItem){
		this.lastItem = lastItem;
	}

	public void updateBuilds(){
		Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.content_frame);

		if (fragment instanceof UpdatableListFragment){
			((UpdatableListFragment) fragment).updateListView();
		}

		if (((MainApplication) getApplication()).getLatestBuilds().isEmpty()){
			status = ERROR;

			showAlert();
		}else {
			status = OK;
		}
	}

	private void refresh(){
		if (((MainApplication) getApplication()).getCurrentTask() != null){
			return;
		}

		DataRetrieverTask task = new DataRetrieverTask(this);

		((MainApplication) getApplication()).setCurrentTask(task);

		task.execute();
	}

	public void showAlert(){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder.setTitle(getString(R.string.retrieve_builds_dialog_title));

		builder.setMessage(getString(R.string.retrieve_builds_dialog_text));

		builder.setPositiveButton(getString(R.string.retrieve_builds_dialog_positive_button), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				DataRetrieverTask task = new DataRetrieverTask(MainActivity.this);

				((MainApplication) getApplication()).setCurrentTask(task);

				task.execute();
			} 
		});

		builder.setNegativeButton(getString(R.string.retrieve_builds_dialog_negative_button), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				finish();
			} 
		});

		builder.setCancelable(false);

		AlertDialog alert = builder.create();

		dialog = alert;

		alert.show();
	}
}
