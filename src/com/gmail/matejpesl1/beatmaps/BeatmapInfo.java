package com.gmail.matejpesl1.beatmaps;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
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
	private static final Pattern newRegionPattern = Pattern.compile("[.*]");
	private static final Pattern backgroundImgPattern = Pattern.compile(".*\".*.jpg\".*");
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
	
	protected static BeatmapInfo getBeatmapInfo(Path beatmap) throws UncompleteBeatmapInfoException, IOException {
		String[] linesWithInfo = getBeatmapInfoLines(beatmap.toFile());
		String backgroundImgName = new String();
		
		backgroundImgName = linesWithInfo[2].substring(
				linesWithInfo[2].indexOf('"')+1,
				linesWithInfo[2].lastIndexOf('"'));
		
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
	
	@SuppressWarnings("serial")
	public static class UncompleteBeatmapInfoException extends Exception { 
	    public UncompleteBeatmapInfoException(String errorMessage, Throwable err) {
	        super(errorMessage, err);
	    }
	}
	
	private static String[] getBeatmapInfoLines(File beatmap) throws UncompleteBeatmapInfoException, IOException {
		String backgroundImgNameLine = new String();
		String diffLine = new String();
		String modeLine = new String();
		
		String currLine = new String();
		byte regionsRead = 0;
		BufferedReader br = getBeatmapInfoReader(beatmap);
		
		while ((currLine = br.readLine()) != null) {
			if (newRegionPattern.matcher(currLine).matches()) {
				if (regionsRead == regionsToRead.size()) {
					break;
				} else if (regionsToRead.contains(currLine)) {
					++regionsRead;
				}
			}
			
			if (backgroundImgPattern.matcher(currLine).matches()) {
				backgroundImgNameLine = currLine;
			} else if (currLine.contains("Mode:")) {
				modeLine = currLine;
			} else if (currLine.contains("OverallDifficulty:")) {
				diffLine = currLine;
			}
		}
		br.close();
		
		String[] gottenInfo = {diffLine, modeLine, backgroundImgNameLine};
	    for (String info : gottenInfo) {
	    	if (info.isEmpty())
	    		throw new UncompleteBeatmapInfoException("Couldn't find necessary info in .osu file", new Throwable()); System.out.println(info);
	    }
	    return gottenInfo;
	}
	
	private static BufferedReader getBeatmapInfoReader(File beatmap) throws FileNotFoundException {
		Reader reader = null;
			FileInputStream stream = new FileInputStream(beatmap);
			try {
				reader = new InputStreamReader(stream, "windows-1250");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				System.exit(0);
			}	
				
		return new BufferedReader(reader);
	}
	
	public static ArrayList<String> getBackgroundImgNames(File dir) throws IOException, UncompleteBeatmapInfoException {
		ArrayList<String> names = new ArrayList<>();
		
		for (File beatmap : getBeatmapsInDir(dir)) {
			names.add(getBeatmapInfo(beatmap.toPath()).getBackgroundImageName());
		}
		
		return names;
	}
	
	private static File[] getBeatmapsInDir(File dir) {
		return dir.listFiles(new FilenameFilter() {
			@Override
		    public boolean accept(File dir, String name) {
		        return name.endsWith(".osu");
		    }
		});
	}
}
