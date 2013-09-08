package com.nicholasnassar.imbabuilds.parser;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;

import com.nicholasnassar.imbabuilds.adapter.Item;

public class JSONParser {
	private final ArrayList<Item> items;
	
	public JSONParser(){
		items = new ArrayList<Item>();
	}

	public JSONParser parse(JSONArray array) throws JSONException {
		return this;
	}

	public ArrayList<Item> getItems(){
		return items;
	}
}
