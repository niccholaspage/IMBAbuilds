package com.nicholasnassar.imbabuilds.fragments;

import java.util.ArrayList;

import com.nicholasnassar.imbabuilds.MainActivity;
import com.nicholasnassar.imbabuilds.R;
import com.nicholasnassar.imbabuilds.Race;
import com.nicholasnassar.imbabuilds.adapter.EntryAdapter;
import com.nicholasnassar.imbabuilds.adapter.Item;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class HomeFragment extends Fragment {
	private ListView listView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_home, container, false);

		setButtonClick(Race.PROTOSS, rootView.findViewById(R.id.protoss_button));
		setButtonClick(Race.TERRAN, rootView.findViewById(R.id.terran_button));
		setButtonClick(Race.ZERG, rootView.findViewById(R.id.zerg_button));

		listView = (ListView) rootView.findViewById(R.id.latest_builds);

		return rootView;
	}

	public void onViewCreated(View view, Bundle savedInstanceState) {
		updateListView();
	}

	private void setButtonClick(final Race race, View button){
		button.setOnClickListener(new OnClickListener(){
			public void onClick(View view) {
				MainActivity activity = ((MainActivity) getActivity());

				int position = race.ordinal() + 1;

				activity.setLastItem(position);

				activity.selectItem(position);
			}
		});
	}

	public void updateListView(){
		final ArrayList<Item> items = Race.getLatestBuilds();
		
		if (!items.isEmpty()){
			((View) getView().findViewById(R.id.progressBar)).setVisibility(View.INVISIBLE);
		}

		listView.setAdapter(new EntryAdapter(getActivity(), items));

		listView.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
				Item item = items.get(position);

				Race[] races = Race.getRacesFromMatchup(item.getText());

				ArrayList<Item> items = Race.getItems(races[0], races[1]);

				String itemName = item.getTitle().substring(item.getTitle().indexOf(" ") + 1);

				for (Item raceItem : items){
					if (raceItem.getTitle().equals(itemName)){
						item = raceItem;
					}
				}

				((MainActivity) getActivity()).showBuild(item);
			}
		});
	}
}