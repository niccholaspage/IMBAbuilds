package com.nicholasnassar.imbabuilds;

import com.nicholasnassar.imbabuilds.fragments.SettingsFragment;

import android.annotation.TargetApi;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.view.MenuItem;

public class SettingsActivity extends PreferenceActivity {
	@SuppressWarnings("deprecation")
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public void onCreate(Bundle savedInstanceState){
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

		if (preferences.getBoolean("night_mode", false)){
			//setTheme(android.R.style.Theme_Holo);
		}else {
			//setTheme(android.R.style.Theme_Holo_Light_DarkActionBar);
		}

		super.onCreate(savedInstanceState);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
			getActionBar().setDisplayHomeAsUpEnabled(true);

			getFragmentManager().beginTransaction().replace(android.R.id.content, new SettingsFragment()).commit();
		}else {
			addPreferencesFromResource(R.xml.preferences);

			getPreferenceScreen().removePreference(findPreference("night_mode"));
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		switch(item.getItemId()) {
		case android.R.id.home:
			onBackPressed();

			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}
