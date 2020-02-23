package com.gmail.matejpesl1.beatmaps;

import java.util.Arrays;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jdt.annotation.Nullable;
import com.gmail.matejpesl1.beatmaps.Filter.DifficultyOperator;
import com.gmail.matejpesl1.beatmaps.Filter.OsuMode;
import com.gmail.matejpesl1.beatmaps.tools.ConsolePrinter;

public class Cleaner extends ConsolePrinter {
	public static final Scanner SC = new Scanner(System.in);
	public static final String[] ABCD_OPTIONS = {"a", "b", "c", "d"};
	private enum CleanerOption {REMOVE_VIDEOS, REMOVE_IMAGES,
		REMOVE_BEATMAPS, REMOVE_SKIN};
	private OsuDir osuDir;
	private CleanerOption cleanerOption;
	private Filter filter;
	
	public Cleaner() {
		
	}
	
	public void showMenu() {
		cleanerOption = obtainCleanerOption();
		filter = obtainCleanerFilter();
		osuDir = new OsuDir(OsuDir.obtainDir());
	}
	
	private Filter obtainCleanerFilter() {
		println(MsgType.ORDINARY, "\nNow select filter:"
				+ "\na - none (apply option to all beatmaps)"
				+ "\nb - by difficulty"
				+ "\nc - by mode"
				+ "\nd - by mode AND difficulty");
		
		Filter filter = new Filter();
		
		switch (getInput(ABCD_OPTIONS)) {
			case "a": break;
			case "b": filter = obtainDifficultyFilter(null); break;
			case "c": filter = obtainModeFilter(null); break;
			case "d": filter = obtainDifficultyFilter(obtainModeFilter(filter)); break;
		}
		System.out.println("filters: " + filter.getFilters());
		return filter;
	}
	
	private Filter obtainModeFilter(@Nullable Filter filter) {
		Filter modifiedFilter = (filter == null ? new Filter() : filter);
		println(MsgType.ORDINARY,
				"choose mode:"
				+ "\na - osu!standard"
				+ "\nb - osu!mania"
				+ "\nc - osu!catch"
				+ "\nd - osu!taiko");
		String input = getInput(ABCD_OPTIONS);
		OsuMode mode = null;
		switch (input) {
		case "a": mode = OsuMode.STANDARD; break;
		case "b": mode = OsuMode.MANIA; break;
		case "c": mode = OsuMode.CATCH; break;
		case "d": mode = OsuMode.TAIKO; break;
		}
		modifiedFilter.addModeFilter(mode);
		return modifiedFilter;
	}
	
	private Filter obtainDifficultyFilter(@Nullable Filter filter) {
		Filter modifiedFilter = (filter == null ? new Filter() : filter);
		println(MsgType.ORDINARY, 
				"choose difficulty:\n[supported operators:]"
				+ "\n\"<\" (less than x) e.g. <5"
				+ "\n\">\" (greater than x) e.g. >5"
				+ "\nnone (matches exactly the input number) - e.g. 2");
		String input = getDifficultyInput();
		if (input.contains(">")) {
			modifiedFilter.addDifficultyFilter(Byte.parseByte(input.replaceAll("\\D+","")), DifficultyOperator.GREATER_THAN);
		} else if (input.contains("<")) {
			modifiedFilter.addDifficultyFilter(Byte.parseByte(input.replaceAll("\\D+","")), DifficultyOperator.LESS_THAN);
		} else {
			modifiedFilter.addDifficultyFilter(Byte.parseByte(input), DifficultyOperator.NONE);
		}
		return modifiedFilter;
	}
	
	private String getDifficultyInput() {
		String diffRange = "[0-9]|1[0-5]";
		Pattern[] options = {Pattern.compile(diffRange),
				Pattern.compile("[<]" + diffRange),
				Pattern.compile("[>]" + diffRange)};
		return getInput(options, true);
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
