package com.nicholasnassar.imbabuilds.fragments;

import com.nicholasnassar.imbabuilds.R;

import android.os.Bundle;
import android.preference.PreferenceFragment;

public class SettingsFragment extends PreferenceFragment {
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);

		addPreferencesFromResource(R.xml.preferences);
		
		getPreferenceScreen().removePreference(findPreference("night_mode"));
	}
}
