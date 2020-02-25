package com.gmail.matejpesl1.beatmaps;

import java.io.IOException;

import com.gmail.matejpesl1.beatmaps.BeatmapVisitor.Result;
import com.gmail.matejpesl1.utils.IOUtils;
import com.gmail.matejpesl1.utils.IOUtils.MsgType;

public class Cleaner {
	public static final String[] ABCD_OPTIONS = {"a", "b", "c", "d"};
	private static final IOUtils io = new IOUtils();
	public enum CleanerOption {REMOVE_BACKGROUNDS, REMOVE_BEATMAPS,
		REMOVE_SKIN, REMOVE_STORYBOARDS, REMOVE_SOUNDS};
		
	public Cleaner() {
		
	}
	
	public void showMenu() {
		CleanerOption cleanerOption = obtainCleanerOption();
		Filter filter = Filter.obtainCleanerFilters();
		OsuDir osuDir = new OsuDir(OsuDir.obtainDir());
		
		io.println(MsgType.INFO, "Press any key to start the process");
		
		waitForKeyPress();
		BeatmapVisitor visitor = new BeatmapVisitor(osuDir);
		Result result = visitor.cleanSongs(filter, cleanerOption);
		System.out.println("\n");
		io.println(MsgType.ORDINARY, "Process finished. Results: "
				+ "\n Processed beatmaps: " + result.processedBeatmaps
				+ "\n Visiting errors: " + result.visitingErrorCounter
				+ "\n General errors: " + result.totalErrorCounter);
		switch (cleanerOption) {
		case REMOVE_BACKGROUNDS: io.println(MsgType.ORDINARY, "\n removed backgrounds: " + result.deletedBackgrounds); break;
		case REMOVE_BEATMAPS: io.println(MsgType.ORDINARY, "\n removed beatmaps: " + result.deletedBeatmaps); break;
		case REMOVE_SKIN: io.println(MsgType.ORDINARY, "\n removed skin elements: " + result.deletedSkins); break;
		case REMOVE_SOUNDS: io.println(MsgType.ORDINARY, "\n removed sounds: " + result.deletedSounds); break;
		case REMOVE_STORYBOARDS: io.println(MsgType.ORDINARY, "\n removed storyboards: " + result.deletedStoryboards); break;
		}
	}
	
	private void waitForKeyPress() {
		try {
			io.waitForKeyPress();
		} catch (InterruptedException e) {
			io.println(MsgType.ERROR, "Program interrupted while waiting for user input."
					+ "\nProgram will start the process.", e);
		} catch (IOException e) {
			io.println(MsgType.ERROR, "An error while waiting for user input."
					+ "\nProgram will start the process", e);
		} catch (SecurityException e) {
			io.println(MsgType.ERROR, "Antivirus probably blocked waiting for user input."
					+ "\nProgram will start the process", e);
		}
	}

	private CleanerOption obtainCleanerOption() {
		io.println(MsgType.ORDINARY, "Select Cleaner option"
				+ " (Option will be executed only on beatmaps that"
				+ " meet the filter you'll choose later):"
				+ "\na - remove background videos"
				+ "\nb - remove background images"
				+ "\nc - remove beatmaps"
				+ "\nd - remove beatmaps' skin"
				+ "\ne - remove beatmaps' sound effects"
				+ "\nf - remove beatmaps' storyboard");
		
		String[] options = {"a", "b", "c", "d", "e"};
		switch (io.getInput(options)) {
			case "a": return CleanerOption.REMOVE_STORYBOARDS;
			case "b": return CleanerOption.REMOVE_BACKGROUNDS;
			case "c": return CleanerOption.REMOVE_BEATMAPS;
			case "d": return CleanerOption.REMOVE_SKIN;
			case "e": return CleanerOption.REMOVE_SOUNDS;
		}
		return null;
	}
}
