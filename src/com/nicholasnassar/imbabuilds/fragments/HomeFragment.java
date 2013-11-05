package com.nicholasnassar.imbabuilds.fragments;

import java.util.ArrayList;

import com.nicholasnassar.imbabuilds.MainActivity;
import com.nicholasnassar.imbabuilds.MainApplication;
import com.nicholasnassar.imbabuilds.R;
import com.nicholasnassar.imbabuilds.Race;
import com.nicholasnassar.imbabuilds.adapter.EntryAdapter;
import com.nicholasnassar.imbabuilds.adapter.Item;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

public class HomeFragment extends Fragment implements UpdatableListFragment {
	private ListView listView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_home, container, false);

		ImageButton protossButton = (ImageButton) rootView.findViewById(R.id.protoss_button);
		final ImageButton terranButton = (ImageButton) rootView.findViewById(R.id.terran_button);
		final ImageButton zergButton = (ImageButton) rootView.findViewById(R.id.zerg_button);

		setButtonClick(Race.PROTOSS, protossButton);
		setButtonClick(Race.TERRAN, terranButton);
		setButtonClick(Race.ZERG, zergButton);

		protossButton.setOnLongClickListener(new OnLongClickListener(){
			public boolean onLongClick(View view){
				if (!terranButton.isPressed() || !zergButton.isPressed()){
					return true;
				}

				SharedPreferences preferences = getActivity().getPreferences(Context.MODE_PRIVATE);

				boolean pro = preferences.getBoolean("freeproversion", false);

				String message;

				SharedPreferences.Editor editor = preferences.edit();

				if (pro){
					editor.remove("freeproversion");

					message = "You've found the easter egg again. Disabling pro version.";
				}else {
					editor.putBoolean("freeproversion", true);

					message = "You've found the easter egg. Have the pro version!";
				}

				editor.commit();

				if (pro){
					((MainActivity) getActivity()).showAds();
				}else {
					((MainActivity) getActivity()).removeAds();
				}

				Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();

				return true;
			}
		});

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
		final ArrayList<Item> items = ((MainApplication) getActivity().getApplication()).getLatestBuilds();

		if (!items.isEmpty()){
			((View) getView().findViewById(R.id.progressBar)).setVisibility(View.INVISIBLE);
		}

		listView.setAdapter(new EntryAdapter(getActivity(), items));

		listView.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
				Item item = items.get(position);

				ArrayList<Item> items = ((MainApplication) getActivity().getApplication()).getBuilds(item.getText());

				String itemName = item.getTitle().substring(item.getTitle().indexOf(" ") + 1);

				for (Item raceItem : items){
					if (raceItem.getTitle().equals(itemName)){
						item = raceItem;
					}
				}

				((MainActivity) getActivity()).showBuild(item, true);
			}
		});
	}
}