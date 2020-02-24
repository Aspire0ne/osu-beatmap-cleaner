package com.gmail.matejpesl1.beatmaps;

import com.gmail.matejpesl1.beatmaps.Filter.OsuMode;

public class BeatmapInfo {
	protected enum Info {BACKGROUND_IMAGE_NAME, DIFFICULTY, MODE};
	private OsuMode mode;
	private float difficulty;
	private String backgroundImageName;
	
	protected BeatmapInfo(String backgroundImageName, OsuMode mode, float difficulty) {
		this.backgroundImageName = backgroundImageName;
		this.mode = mode;
		this.difficulty = difficulty;
	}
	
	protected float getDifficulty() {
		return difficulty;
	}
	
	protected OsuMode getMode() {
		return mode;
	}
	
	protected String getBackgroundImageName() {
		return backgroundImageName;
	}
}
