package com.nicholasnassar.imbabuilds;

import java.util.Locale;

public enum Race {
	PROTOSS(0xFF1DA6DC, R.drawable.protoss),
	TERRAN(0xFF0142FC, R.drawable.terran),
	ZERG(0xFF6A1E80, R.drawable.zerg);

	private final int color;

	private final int drawable;

	private Race(int color, int drawable){
		this.color = color;

		this.drawable = drawable;
	}

	public static Race getRace(String name){
		for (Race race : values()){
			if (race.name().equalsIgnoreCase(name)){
				return race;
			}
		}

		return null;
	}

	public String toString(){
		String name = name();

		name = name.substring(0, 1).toUpperCase(Locale.US) + name.substring(1).toLowerCase(Locale.US);

		return name;
	}

	public String getFirstLetter(){
		return name().substring(0, 1);
	}

	public int getColor(){
		return color;
	}

	public int getDrawable(){
		return drawable;
	}

	public static Race getRaceFromFirstLetter(String letter){
		for (Race race : values()){
			if (race.getFirstLetter().equalsIgnoreCase(letter)){
				return race;
			}
		}

		return null;
	}

	public static String getMatchup(Race race, Race opponent){
		return (race.getFirstLetter() + "v" + opponent.getFirstLetter()).toLowerCase(Locale.US);
	}

	public static Race[] getRacesFromMatchup(String matchup){
		matchup = matchup.toLowerCase(Locale.US);

		String[] raceStrings = matchup.split("v");

		Race[] races = new Race[2];

		races[0] = getRaceFromFirstLetter(raceStrings[0]);

		races[1] = getRaceFromFirstLetter(raceStrings[1]);

		return races;
	}
}
