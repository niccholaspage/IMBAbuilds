package com.nicholasnassar.imbabuilds;

import com.nicholasnassar.imbabuilds.fragments.SettingsFragment;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.MenuItem;

public class SettingsActivity extends PreferenceActivity {
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);

		getActionBar().setDisplayHomeAsUpEnabled(true);

		getFragmentManager().beginTransaction().replace(android.R.id.content, new SettingsFragment()).commit();
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
