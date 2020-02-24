package com.gmail.matejpesl1.beatmaps;

import java.io.IOException;

import com.gmail.matejpesl1.utils.ioutils.ConsolePrinter;
import com.gmail.matejpesl1.utils.ioutils.ConsoleReader;

public class Cleaner extends ConsolePrinter {
	public static final String[] ABCD_OPTIONS = {"a", "b", "c", "d"};
	private enum CleanerOption {REMOVE_VIDEOS, REMOVE_IMAGES,
		REMOVE_BEATMAPS, REMOVE_SKIN};
	
	public Cleaner() {
		
	}
	
	public void showMenu() {
		CleanerOption cleanerOption = obtainCleanerOption();
		Filter filter = Filter.obtainCleanerFilters();
		OsuDir osuDir = new OsuDir(OsuDir.obtainDir());
		println(MsgType.INFO, "Press any key to start the process");
		waitForKeyPress();
		cleanSongs(osuDir, filter, cleanerOption);
	}
	
	private void waitForKeyPress() {
		try {
			ConsoleReader.waitForKeyPress();
		} catch (InterruptedException e) {
			println(MsgType.ERROR, "Program interrupted while waiting for user input."
					+ "\nProgram will start the process.", e);
		} catch (IOException e) {
			println(MsgType.ERROR, "An error while waiting for user input."
					+ "\nProgram will start the process", e);
		} catch (SecurityException e) {
			println(MsgType.ERROR, "Antivirus probably blocked waiting for user input."
					+ "\nProgram will start the process", e);
		}
	}
	
	private void cleanSongs(OsuDir osuDir, Filter  filter, CleanerOption option) {
		println(MsgType.INFO, "starting proces...");
		
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
		switch (ConsoleReader.getInput(abcdOptions)) {
			case "a": return CleanerOption.REMOVE_VIDEOS;
			case "b": return CleanerOption.REMOVE_IMAGES;
			case "c": return CleanerOption.REMOVE_BEATMAPS;
			case "d": return CleanerOption.REMOVE_SKIN;
		}
		return null;
	}
}
