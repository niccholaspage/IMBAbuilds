package com.nicholasnassar.imbabuilds.parser;

import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.nicholasnassar.imbabuilds.adapter.Item;

public class GuidesParser extends JSONParser {
	@Override
	public GuidesParser parse(JSONArray array) throws JSONException {
		for (int i = 0; i < array.length(); i++){
			JSONObject guideCategory = array.getJSONObject(i);
			
			@SuppressWarnings("unchecked")
			Iterator<String> iterator = guideCategory.keys();

			while (iterator.hasNext()){
				String key = iterator.next();

				getItems().add(new Item(key, null, -1, true));

				JSONArray buildCategory = guideCategory.getJSONArray(key);

				for (int j = 0; j < buildCategory.length(); j++){
					String title = buildCategory.getJSONObject(j).getString("title");

					String text = buildCategory.getJSONObject(j).getString("text");

					Item item = new Item(title, text, 0xFF96AA39, false);

					getItems().add(item);
				}
			}
		}

		return this;
	}
}
