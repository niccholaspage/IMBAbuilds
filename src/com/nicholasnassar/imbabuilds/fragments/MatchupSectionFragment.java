package com.nicholasnassar.imbabuilds.fragments;

import java.util.ArrayList;

import com.nicholasnassar.imbabuilds.MainActivity;
import com.nicholasnassar.imbabuilds.R;
import com.nicholasnassar.imbabuilds.Race;
import com.nicholasnassar.imbabuilds.adapter.EntryAdapter;
import com.nicholasnassar.imbabuilds.adapter.Item;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class MatchupSectionFragment extends ListFragment {
	public static final String ARG_SECTION_RACE = "section_race";

	public static final String ARG_SECTION_OPPONENT = "section_opponent";

	private ArrayList<Item> items;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onListItemClick(ListView listView, View view, int position, long id){
		((MainActivity) getActivity()).showBuild(items.get(position));
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_build_list, container, false);
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		updateListView();
	}

	public static MatchupSectionFragment newInstance(Race race, Race opponent){
		MatchupSectionFragment fragment = new MatchupSectionFragment();

		Bundle bundle = new Bundle();

		bundle.putInt("race", race.ordinal());

		bundle.putInt("opponent", opponent.ordinal());

		fragment.setArguments(bundle);

		return fragment;
	}

	public Race getRace(){
		return Race.values()[getArguments().getInt("race")];
	}

	public Race getOpponent(){
		return Race.values()[getArguments().getInt("opponent")];
	}

	public void updateListView(){
		items = Race.getItems(getRace(), getOpponent());
		
		if (!items.isEmpty()){
			((View) getView().findViewById(R.id.progressBar)).setVisibility(View.INVISIBLE);
		}

		setListAdapter(new EntryAdapter(getActivity(), items));
		
		getListView().setTextFilterEnabled(true);
	}
}