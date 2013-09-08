package com.nicholasnassar.imbabuilds.parser;

import org.json.JSONArray;
import org.json.JSONException;

import com.nicholasnassar.imbabuilds.Race;
import com.nicholasnassar.imbabuilds.adapter.Item;

public class LatestBuildsParser extends JSONParser {
	@Override
	public LatestBuildsParser parse(JSONArray array) throws JSONException {
		if (array == null){
			return this;
		}
		
		getItems().add(new Item("Latest Builds", null, -1, true));

		for (int i = 0; i < array.length(); i++){
			String item = array.getString(i);

			Race race = Race.getRaceFromFirstLetter(item.substring(0, 1));

			getItems().add(new Item(item, item.split(" ")[0], race.getColor(), false));
		}
		
		return this;
	}
}
