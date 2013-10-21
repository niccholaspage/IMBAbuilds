package com.nicholasnassar.imbabuilds;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.nicholasnassar.imbabuilds.adapter.Item;

import android.app.Application;

public class MainApplication extends Application {
	private DataRetrieverTask currentTask = null;

	private ArrayList<Item> latestBuilds;

	private Map<String, ArrayList<Item>> raceBuilds;

	@Override
	public void onCreate(){
		super.onCreate();

		latestBuilds = new ArrayList<Item>();

		raceBuilds = new HashMap<String, ArrayList<Item>>();

		for (Race race : Race.values()){
			for (Race opponent : Race.values()){
				raceBuilds.put((race.getFirstLetter() + "v" + opponent.getFirstLetter()).toLowerCase(Locale.US), new ArrayList<Item>());
			}
		}
	}

	public ArrayList<Item> getLatestBuilds(){
		return latestBuilds;
	}

	public ArrayList<Item> getBuilds(String matchup){
		return raceBuilds.get(matchup.toLowerCase(Locale.US));
	}

	public void setCurrentTask(DataRetrieverTask currentTask){
		if (this.currentTask != null){
			this.currentTask.cancel(true);
		}

		this.currentTask = currentTask;
	}

	public DataRetrieverTask getCurrentTask(){
		return currentTask;
	}
}
