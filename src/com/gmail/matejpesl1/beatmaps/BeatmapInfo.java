package com.gmail.matejpesl1.beatmaps;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Pattern;
import com.gmail.matejpesl1.beatmaps.Filter.OsuMode;

public class BeatmapInfo {
	protected enum Info {BACKGROUND_IMAGE_NAME, DIFFICULTY, MODE};
	private static final ArrayList<String> regionsToRead =
			new ArrayList<>(Arrays.asList("[Events]", "[General]", "[Difficulty]"));
	private OsuMode mode;
	private float difficulty;
	private String backgroundImageName;
	
	protected BeatmapInfo(float difficulty, OsuMode mode, String backgroundImageName) {
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
	
	protected static BeatmapInfo getBeatmapInfo(Path beatmap, ArrayList<Info> infoToReturn) throws IOException {
		String[] linesWithInfo = getBeatmapInfoLines(beatmap);
		
		String backgroundImgName = linesWithInfo[2].substring(
				linesWithInfo[2].indexOf('\"')+1,
				linesWithInfo[2].lastIndexOf('\"'));
		float difficulty = Float.parseFloat(linesWithInfo[0].replaceAll("[^\\d.]", ""));
		byte modeNum = Byte.parseByte(linesWithInfo[1].replaceAll("[^\\d.]", ""));
		OsuMode mode = null;
		switch (modeNum) {
		case 0: mode = OsuMode.STANDARD; break;
		case 1: mode = OsuMode.CATCH; break;
		case 2: mode = OsuMode.TAIKO; break;
		case 3: mode = OsuMode.MANIA; break;
		}
		
	    return new BeatmapInfo(difficulty, mode, backgroundImgName);
	}
	
	private static String[] getBeatmapInfoLines(Path beatmap) throws IOException {
		Reader reader = null;
		FileInputStream stream = null;
		try {
			stream = new FileInputStream(beatmap.toFile());
			reader = new InputStreamReader(stream, "windows-1250");	
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		BufferedReader br = new BufferedReader(reader);
		
		byte regionsRead = 0;
		Pattern newRegionPattern = Pattern.compile("[.*]");
		Pattern backgroundImgPattern = Pattern.compile(".*\".*.jpg\".*");
		String line = new String();
		String diffLine = new String();
		String modeLine = new String();
		String backgroundImgNameLine = new String();
		
		while ((line = br.readLine()) != null) {
			if (newRegionPattern.matcher(line.trim()).matches()
					|| (modeLine != null && diffLine != null && backgroundImgNameLine != null)) {
				if (regionsRead == regionsToRead.size()) {
					break;
				} else if (regionsToRead.contains(line)) {
					++regionsRead;
				}
			}
			
			if (backgroundImgPattern.matcher(line.trim()).matches()) {
				backgroundImgNameLine = line;
			} else if (line.contains("Mode:")) {
				modeLine = line;
			} else if (line.contains("OverallDifficulty:")) {
				diffLine = line;
			}
		}
		br.close();
	    reader.close();
	    stream.close();
	    return new String[] {diffLine, modeLine, backgroundImgNameLine};
	}
}
