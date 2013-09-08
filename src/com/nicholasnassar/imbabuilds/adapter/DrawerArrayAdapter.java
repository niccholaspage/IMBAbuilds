package com.nicholasnassar.imbabuilds.adapter;

import com.nicholasnassar.imbabuilds.R;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class DrawerArrayAdapter extends ArrayAdapter<String> {
	private final Context context;

	private final Typeface typeFace;

	public DrawerArrayAdapter(Context context, int id, String[] options){
		super(context, id, options);

		this.context = context;

		typeFace = Typeface.create("sans-serif-light", Typeface.NORMAL);
	}

	public View getView(int pos, View convertView, ViewGroup parent){
		if(convertView == null){
			LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			convertView = vi.inflate(R.layout.drawer_list_item, null);
		}

		TextView tv = (TextView) convertView.findViewById(android.R.id.text1);

		tv.setText(getItem(pos));

		tv.setTypeface(typeFace);

		return convertView;
	}
}
