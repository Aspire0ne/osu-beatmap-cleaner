package com.gmail.matejpesl1.beatmaps;

import java.io.File;
import java.io.IOException;

import com.gmail.matejpesl1.beatmaps.BeatmapVisitor.Result;
import com.gmail.matejpesl1.utils.FileUtils;
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
		
		File dirToMoveTo = obtainRemoveOrMove();
		io.println(MsgType.INFO, "Press any key to start the process");
		
		waitForKeyPress();
		BeatmapVisitor visitor = new BeatmapVisitor(osuDir);
		Result result = visitor.cleanSongs(filter, cleanerOption, dirToMoveTo);
		io.println(MsgType.ORDINARY, "\n\nProcess finished. Results: "
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
	
	private File obtainRemoveOrMove() {
		io.println(MsgType.ORDINARY, "Remove or move to a specified folder?"
				+ "\na - remove"
				+ "\nb - move");
		String[] options = {"a", "b"};
		switch (io.getInput(options)) {
			case "a": return null;
			case "b": return obtainMoveDir();
		}
		return null;
	}
	
	private File obtainMoveDir() {
			io.println(MsgType.ORDINARY, "Please specify folder to move the files to:");
			String beginning = FileUtils.ROOT.replace("\\", "/") + "/";
			
			while (true) {
				io.print(MsgType.ORDINARY, beginning);
				String inputPath = io.getInput().replace("/", "\\");
				File file = new File(beginning + inputPath);
				
				if (!file.exists()) {
					io.println(MsgType.ORDINARY, "\nNon-existent path. Please try again.");
					continue;
				}
				
				return file;	
			}
	}

	private CleanerOption obtainCleanerOption() {
		io.println(MsgType.ORDINARY, "Select Cleaner option"
				+ " (Option will be executed only on beatmaps that"
				+ " meet the filter you'll choose later):"
				+ "\na - remove/move storyboards"
				+ "\nb - remove/move background images"
				+ "\nc - remove/move beatmaps"
				+ "\nd - remove/move beatmaps' skin"
				+ "\ne - remove/move beatmaps' sound effects");
		
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
