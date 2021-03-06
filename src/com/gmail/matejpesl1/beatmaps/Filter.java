package com.gmail.matejpesl1.beatmaps;

import java.util.ArrayList;
import java.util.regex.Pattern;

import org.eclipse.jdt.annotation.Nullable;

import com.gmail.matejpesl1.utils.IOUtils;
import com.gmail.matejpesl1.utils.IOUtils.MsgType;

public class Filter extends Cleaner {
	public enum BeatmapFilter {DIFFICULTY, MODE};
	public enum OsuMode {TAIKO, STANDARD, MANIA, CATCH};
	public enum DifficultyOperator {LESS_THAN, GREATER_THAN, NONE};
	private static final IOUtils io = new IOUtils();
	
	private ArrayList<BeatmapFilter> filters = new ArrayList<>();
	private byte difficulty;
	private OsuMode mode;
	private DifficultyOperator operator;
	
	public Filter() {

	}
	
	public static Filter obtainCleanerFilters() {
		io.println(MsgType.ORDINARY, "\nNow select filter:"
				+ "\na - none (apply option to all beatmaps)"
				+ "\nb - by difficulty"
				+ "\nc - by mode"
				+ "\nd - by mode AND difficulty (both conditions MUST be true)");
		
		Filter filter = new Filter();
		
		switch (io.getInput(ABCD_OPTIONS)) {
			case "a": break;
			case "b": filter = obtainDifficultyFilter(null); break;
			case "c": filter = obtainModeFilter(null); break;
			case "d": filter = obtainDifficultyFilter(obtainModeFilter(filter)); break;
		}
		io.println(MsgType.DEBUG, "filters: " + filter.getBeatmapFilters());
		io.println(MsgType.ORDINARY, "\n");
		return filter;
	}
	
	private static Filter obtainModeFilter(@Nullable Filter filter) {
		Filter modifiedFilter = (filter == null ? new Filter() : filter);
		io.println(MsgType.ORDINARY,
				"choose mode:"
				+ "\na - osu!standard"
				+ "\nb - osu!mania"
				+ "\nc - osu!catch"
				+ "\nd - osu!taiko");
		String input = io.getInput(ABCD_OPTIONS);
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
	
	private static Filter obtainDifficultyFilter(@Nullable Filter filter) {
		Filter modifiedFilter = (filter == null ? new Filter() : filter);
		io.println(MsgType.ORDINARY, 
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
	
	private static String getDifficultyInput() {
		String diffRange = "[0-9]|1[0-9]";
		Pattern[] options = {Pattern.compile(diffRange),
				Pattern.compile("[<]" + diffRange),
				Pattern.compile("[>]" + diffRange)};
		return io.getInput(options, true);
	}
	
	public ArrayList<BeatmapFilter> getBeatmapFilters() {
		return filters;
	}
	
	private void addFilter(BeatmapFilter filter) {
		if (!filters.contains(filter)) {
			filters.add(filter);
		}
	}
	
	public void addDifficultyFilter(byte difficulty, DifficultyOperator operator) {
		addFilter(BeatmapFilter.DIFFICULTY);
		this.difficulty = difficulty;
		this.operator = operator;
	}
	
	public byte getDifficulty() {
		return difficulty;
	}
	public DifficultyOperator getDifficultyOperator() {
		return operator;
	}
	
	public OsuMode getMode() {
		return mode;
	}
	
	public void addModeFilter(OsuMode mode) {
		addFilter(BeatmapFilter.MODE);
		this.mode = mode;
	}
}
