package com.nicholasnassar.imbabuilds.adapter;

public class Item {
	private final String title;

	private final String text;

	private final int color;

	private final boolean section;

	public Item(String title, String text, int color, boolean section){
		this.title = title;

		this.text = text;

		this.color = color;

		this.section = section;
	}

	public String getTitle(){
		return title;
	}

	public boolean isSection(){
		return section;
	}

	public int getColor(){
		return color;
	}

	public String getText(){
		return text;
	}
}
