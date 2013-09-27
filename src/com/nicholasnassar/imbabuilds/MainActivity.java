package com.nicholasnassar.imbabuilds;

import org.json.JSONObject;

import com.nicholasnassar.imbabuilds.adapter.DrawerArrayAdapter;
import com.nicholasnassar.imbabuilds.adapter.Item;
import com.nicholasnassar.imbabuilds.fragments.BlankFragment;
import com.nicholasnassar.imbabuilds.fragments.BuildFragment;
import com.nicholasnassar.imbabuilds.fragments.GuidesFragment;
import com.nicholasnassar.imbabuilds.fragments.HomeFragment;
import com.nicholasnassar.imbabuilds.fragments.RaceFragment;
import com.nicholasnassar.imbabuilds.fragments.TitledFragment;
import com.nicholasnassar.imbabuilds.fragments.UpdatableListFragment;

import android.app.ActionBar;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.os.Handler;
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

	private JSONObject jsonData;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);

		final ActionBar actionBar = getActionBar();

		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);

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

				invalidateOptionsMenu();

				if (fragment instanceof BlankFragment){
					replaceFragment();
				}

				if (!isInLowerLevelFragment()){
					lastItem = mDrawer.getCheckedItemPosition();
				}
			}

			public void onDrawerOpened(View drawerView) {
				getActionBar().setTitle(mDrawerTitle);

				invalidateOptionsMenu();

				if (!isInLowerLevelFragment() && mDrawer.getCheckedItemPosition() != -1){
					lastItem = mDrawer.getCheckedItemPosition();
				}

				mDrawerToggle.setDrawerIndicatorEnabled(true);
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);

		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);

		if (savedInstanceState == null) {
			selectItem(0);

			if (jsonData == null){
				new DataRetrieverTask(this).execute();
			}
		}else {
			lastItem = savedInstanceState.getInt("last_item");

			currentColor = savedInstanceState.getInt("currentColor");

			changeColor(currentColor);

			setTitle(savedInstanceState.getCharSequence("title"));
		}

		Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.content_frame);

		if (fragment instanceof TitledFragment && ((TitledFragment) fragment).isLowerLevel()){
			mDrawerToggle.setDrawerIndicatorEnabled(false);
		}else {
			mDrawerToggle.setDrawerIndicatorEnabled(true);
		}
	}

	private boolean isInLowerLevelFragment(){
		Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.content_frame);

		return (fragment instanceof TitledFragment) && ((TitledFragment) fragment).isLowerLevel();
	}

	private void changeColor(int newColor) {
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

	public JSONObject getJSONData(){
		return jsonData;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		outState.putInt("chosen_option", mDrawer.getCheckedItemPosition());

		outState.putInt("last_item", lastItem);

		outState.putInt("currentColor", currentColor);

		outState.putCharSequence("title", mTitle);
	}

	private Drawable.Callback drawableCallback = new Drawable.Callback() {
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
			/*case R.id.action_settings:
			Intent intent = new Intent(this, SettingsActivity.class);

            startActivity(intent);

			return true;*/
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

		calculateColor(position);

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
			fragment = GuidesFragment.newInstance();
		} else {
			fragment = RaceFragment.newInstance(getRace());
		}

		getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit();

		return fragment;
	}

	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;

		getActionBar().setTitle(title);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);

		mDrawerToggle.syncState();

		if (mDrawerLayout.isDrawerOpen(mDrawer)){
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
	
	public void updateListViews(){
		Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.content_frame);
		
		if (fragment instanceof UpdatableListFragment){
			((UpdatableListFragment) fragment).updateListView();
		}
	}
}
