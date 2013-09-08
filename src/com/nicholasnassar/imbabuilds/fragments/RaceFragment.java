package com.nicholasnassar.imbabuilds.fragments;

import com.astuetz.viewpager.extensions.PagerSlidingTabStrip;
import com.nicholasnassar.imbabuilds.R;
import com.nicholasnassar.imbabuilds.Race;
import com.nicholasnassar.imbabuilds.adapter.MatchupPagerAdapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class RaceFragment extends Fragment {
	private PagerSlidingTabStrip tabs = null;
	private ViewPager pager;
	private MatchupPagerAdapter adapter;

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_race, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		tabs = (PagerSlidingTabStrip) view.findViewById(R.id.tabs);
		pager = (ViewPager) view.findViewById(R.id.pager);

		final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources()
				.getDisplayMetrics());
		pager.setPageMargin(pageMargin);

		adapter = new MatchupPagerAdapter(getChildFragmentManager(), getRace());

		pager.setAdapter(adapter);

		tabs.setViewPager(pager);

		tabs.setIndicatorColor(getRace().getColor());
	}

	public Race getRace(){
		return Race.values()[getArguments().getInt("race")];
	}

	public static RaceFragment newInstance(Race race){
		RaceFragment fragment = new RaceFragment();

		Bundle bundle = new Bundle();

		bundle.putInt("race", race.ordinal());

		fragment.setArguments(bundle);

		return fragment;
	}
}