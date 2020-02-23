package com.gmail.matejpesl1.beatmaps;

import java.util.Arrays;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.gmail.matejpesl1.beatmaps.tools.ConsolePrinter;

public class Cleaner extends ConsolePrinter {
	public static final Scanner SC = new Scanner(System.in);
	public static final String[] ABCD_OPTIONS = {"a", "b", "c", "d"};
	private enum CleanerOption {REMOVE_VIDEOS, REMOVE_IMAGES,
		REMOVE_BEATMAPS, REMOVE_SKIN};
	
	public Cleaner() {
		
	}
	
	public void showMenu() {
		CleanerOption cleanerOption = obtainCleanerOption();
		Filter filter = Filter.obtainCleanerFilters();
		OsuDir osuDir = new OsuDir(OsuDir.obtainDir());
		cleanSongs(osuDir, filter, cleanerOption);
	}
	
	private void cleanSongs(OsuDir osuDir, Filter  filter, CleanerOption option) {
		
	}

	private CleanerOption obtainCleanerOption() {
		println(MsgType.ORDINARY, "Select Cleaner option"
				+ " (Option will be executed only on beatmaps that"
				+ " meet the filter you'll choose later):" 
				+ "\na - remove background videos"
				+ "\nb - remove background images"
				+ "\nc - remove beatmaps"
				+ "\nd - remove beatmaps' skin");
		
		String[] abcdOptions = {"a", "b", "c", "d"};
		switch (getInput(abcdOptions)) {
			case "a": return CleanerOption.REMOVE_VIDEOS;
			case "b": return CleanerOption.REMOVE_IMAGES;
			case "c": return CleanerOption.REMOVE_BEATMAPS;
			case "d": return CleanerOption.REMOVE_SKIN;
		}
		return null;
	}
	
	public static String getInput(String[] options) {
		while (true) {
			String input = SC.nextLine().trim();
			if (options.length != 0) {
				if (Arrays.stream(options).noneMatch(input::equals)) {
					System.out.println("Incorrect input, please try again.");
					continue;
				}
			}
			return input;
		}
	}
	
	public static String getInput(Pattern[] patterns, boolean removeSpaces) {
		while (true) {
			String input = getInput(new String[]{});
			if (removeSpaces) {
				input = input.replaceAll("\\s","");
			}
			for (Pattern p : patterns) {
				Matcher m = p.matcher(input);
				if (m.matches()) {
					return input;
				}
			}
			System.out.println("Incorrect input, please try again.");
		}
	}
}
