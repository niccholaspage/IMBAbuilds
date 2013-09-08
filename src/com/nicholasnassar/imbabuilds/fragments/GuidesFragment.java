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

public class GuidesFragment extends ListFragment {
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
	public void onViewCreated(View view, Bundle savedInstanceState) {
		updateListView();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_build_list, container, false);
	}

	public void updateListView(){
		items = Race.getGuideBuilds();

		if (!items.isEmpty()){
			((View) getView().findViewById(R.id.progressBar)).setVisibility(View.INVISIBLE);
		}

		setListAdapter(new EntryAdapter(getActivity(), items));

		getListView().setTextFilterEnabled(true);
	}

	public static GuidesFragment newInstance(){
		GuidesFragment fragment = new GuidesFragment();

		return fragment;
	}
}