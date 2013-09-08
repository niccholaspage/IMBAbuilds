package com.nicholasnassar.imbabuilds.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.nicholasnassar.imbabuilds.R;

import java.util.ArrayList;

public class EntryAdapter extends ArrayAdapter<Item> {
	private final ArrayList<Item> items;

	private LayoutInflater viewInflater;

	public EntryAdapter(Context context, ArrayList<Item> items) {
		super(context, 0, items);

		this.items = items;

		viewInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Item item = items.get(position);

		if (item == null) {
			return convertView;
		}

		if (item.isSection()) {
			convertView = viewInflater.inflate(R.layout.list_category, null);

			convertView.setOnClickListener(null);
			convertView.setOnLongClickListener(null);
			convertView.setLongClickable(false);

			final TextView sectionView = (TextView) convertView.findViewById(R.id.list_item_section_text);

			sectionView.setText(item.getTitle());
		} else {
			convertView = viewInflater.inflate(android.R.layout.simple_list_item_1, null);

			((TextView) convertView.findViewById(android.R.id.text1)).setText(item.getTitle());
		}

		return convertView;
	}
}