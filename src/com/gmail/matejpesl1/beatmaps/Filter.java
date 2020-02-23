package com.gmail.matejpesl1.beatmaps;

import java.util.ArrayList;

public class Filter {
	public enum BeatmapFilter {DIFFICULTY, MODE};
	public enum OsuMode {TAIKO, STANDARD, MANIA, CATCH};
	public enum DifficultyOperator {LESS_THAN, GREATER_THAN, NONE};
	
	private ArrayList<BeatmapFilter> filters = new ArrayList<>();
	private byte difficulty;
	private OsuMode mode;
	private DifficultyOperator operator;
	
	public Filter() {

	}
	
	public ArrayList<BeatmapFilter> getFilters() {
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
	
	public void addModeFilter(OsuMode mode) {
		addFilter(BeatmapFilter.MODE);
		this.mode = mode;
	}
}
