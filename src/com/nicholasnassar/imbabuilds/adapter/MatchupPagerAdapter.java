package com.nicholasnassar.imbabuilds.adapter;

import com.nicholasnassar.imbabuilds.Race;
import com.nicholasnassar.imbabuilds.fragments.MatchupSectionFragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class MatchupPagerAdapter extends FragmentPagerAdapter {
	private final Race race;

	private final String[] titles;

	public MatchupPagerAdapter(FragmentManager fragmentManager, Race race) {
		super(fragmentManager);

		this.race = race;

		titles = new String[Race.values().length];

		for (int i = 0; i < Race.values().length; i++){
			titles[i] = race.getFirstLetter() + "v" + Race.values()[i].getFirstLetter();
		}
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return titles[position];
	}

	@Override
	public int getCount() {
		return Race.values().length;
	}

	@Override
	public Fragment getItem(int position) {
		return MatchupSectionFragment.newInstance(race, Race.values()[position]);
	}
}