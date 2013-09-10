package com.nicholasnassar.imbabuilds.fragments;

import com.nicholasnassar.imbabuilds.R;
import com.nicholasnassar.imbabuilds.adapter.Item;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebView;

public class BuildFragment extends Fragment implements TitledFragment, SharedPreferences.OnSharedPreferenceChangeListener {
	private String title;

	private String text;

	private WebView webView;

	public BuildFragment(){
		super();

		webView = null;
	}

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);

		if (savedInstanceState != null){
			title = savedInstanceState.getString("title");

			text = savedInstanceState.getString("text");
		}

		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

		preferences.registerOnSharedPreferenceChangeListener(this);

		if (preferences.getBoolean("display_on_build", false)){
			getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_build, container, false);

		webView = (WebView) rootView.findViewById(R.id.web_view);

		title = getArguments().getString("title");

		text = getArguments().getString("text");

		if (savedInstanceState == null){
			text = parseText(text);

			webView.loadData(text, "text/html", null);
		}else {
			webView.restoreState(savedInstanceState);
		}

		return rootView;
	}

	@Override
	public void onSaveInstanceState(Bundle outState){
		super.onSaveInstanceState(outState);

		outState.putString("title", title);

		outState.putString("text", text);

		if (webView != null){
			webView.saveState(outState);
		}
	}

	public String getTitle(){
		return title;
	}

	public boolean disableDrawerIndicator(){
		return true;
	}

	public boolean isLowerLevel(){
		return true;
	}

	public static BuildFragment newInstance(Item item, boolean overview){
		BuildFragment fragment = new BuildFragment();

		Bundle args = new Bundle();

		args.putString("title", item.getTitle());

		args.putString("text", item.getText());

		args.putBoolean("overview", overview);

		fragment.setArguments(args);

		return fragment;
	}

	private String parseText(String text){
		//General Overview beginning
		if (getArguments().getBoolean("overview")){
			text = "<h3>General Overview</h3>" + text;
		}

		//Category start and ends - h3 tags
		text = text.replace("%cs", "<h3>").replace("%ce", "</h3>");

		//Build order tag - h3 tags
		text = text.replace("%bs", "<h3>Build Order (").replace("%be", ")</h3>");

		//Build Placement - h3 tags
		text = text.replace("%bp", "<h3>Building Placement</h3>");

		//Build Order tag without parenthesis - h3 tags
		text = text.replace("%b", "<h3>Build Order</h3>");

		//Scout tag - h3 tags
		text = text.replace("%s", "<h3>Scouting</h3>");

		//Tips - h3 tags
		text = text.replace("%ti", "<h3>Tips</h3>");

		//Transitions - h3 tags
		text = text.replace("%t", "<h3>Transitions</h3>");

		//Pros and Cons - h3 tags
		text = text.replace("%pac", "<h3>Pros and Cons</h3>");

		//Favorable Maps - h3 tags
		text = text.replace("%fm", "<h3>Favorable Maps</h3>");

		//Replays - h3 tags
		text = text.replace("%r", "<h3>Replays</h3>");

		//Execution - h3 tags
		text = text.replace("%e", "<h3>Execution</h3>");

		//Ideal Game - h3 tags
		text = text.replace("%ig", "<h3>Ideal Game</h3>");

		//Notice - h3 tags
		text = text.replace("%n", "<h3>Notice</h3>");

		//New line characters
		text = text.replace("\n", "<br />");

		return text;
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences preferences, String key) {
		if (preferences.getBoolean("display_on_build", false)){
			getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		}else {
			getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		}
	}
}
