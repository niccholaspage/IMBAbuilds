package com.nicholasnassar.imbabuilds.parser;

import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.nicholasnassar.imbabuilds.Race;
import com.nicholasnassar.imbabuilds.adapter.Item;

public class MatchupJSONParser extends JSONParser {
	public MatchupJSONParser parse(JSONArray array, Race race) throws JSONException {
		if (array == null){
			return this;
		}

		for (int i = 0; i < array.length(); i++){
			JSONObject raceCategory = array.getJSONObject(i);

			@SuppressWarnings("unchecked") //Stupid Java
			Iterator<String> raceCategoryIterator = raceCategory.keys();

			while (raceCategoryIterator.hasNext()){
				String key = raceCategoryIterator.next();

				getItems().add(new Item(key, null, -1, true));

				JSONArray buildCategory = raceCategory.getJSONArray(key);

				for (int j = 0; j < buildCategory.length(); j++){
					String title = buildCategory.getJSONObject(j).getString("title");

					String text = buildCategory.getJSONObject(j).getString("text");

					Item item = new Item(title, text, race.getColor(), false);

					getItems().add(item);
				}
			}
		}

		return this;
	}
}
