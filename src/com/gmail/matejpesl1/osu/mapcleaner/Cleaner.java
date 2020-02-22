package com.gmail.matejpesl1.osu.mapcleaner;

import java.util.Arrays;
import java.util.Scanner;
import org.eclipse.jdt.annotation.Nullable;
import com.gmail.matejpesl1.osu.mapcleaner.tools.ConsolePrinter;

public class Cleaner extends ConsolePrinter {
	public static final Scanner sc = new Scanner(System.in);
	private enum CleanerOption {REMOVE_VIDEOS, REMOVE_IMAGES, REMOVE_BEATMAPS, REMOVE_TAIKO, REMOVE_CATCH, REMOVE_STANDARD, REMOVE_MANIA};
	private OsuDir osuDir;
	private CleanerOption option;
	
	public Cleaner() {
		
	}
	
	public void showMenu() {
		println(MsgType.ORDINARY, "Select option (Option will be executed on beatmaps that meet the filters you'll choose later):" 
				+ "\na - remove background videos"
				+ "\nb - remove background images"
				+ "\nc - remove beatmaps with specified mode (Taiko, Osu! etc.)"
				+ "\nd - remove beatmaps\n");
		option = obtainOption();
	}
	
	private CleanerOption obtainOption() {
		String[] abcdOptions = {"a", "b", "c", "d"};
		switch (getInput(abcdOptions)) {
			case "a": return CleanerOption.REMOVE_VIDEOS;
			case "b": return CleanerOption.REMOVE_IMAGES;
			case "c": return CleanerOption.REMOVE_BEATMAPS;
			case "d": {
				println(MsgType.ORDINARY, "Select mode:"
						+ "a - osu!taiko"						
						+ "b - osu!catch"
						+ "c - osu!mania"
						+ "d - osu!standard (often just osu!)\n");
				switch (getInput(abcdOptions)) {
					case "a": return CleanerOption.REMOVE_TAIKO;
					case "b": return CleanerOption.REMOVE_CATCH;
					case "c": return CleanerOption.REMOVE_MANIA;
					case "d": return CleanerOption.REMOVE_STANDARD;
				}
			}
		}
		return null;
	}
	
	public static String getInput(@Nullable String[] options) {
		while (true) {
			String input = sc.nextLine().trim();
			if (options != null) {
				if (Arrays.stream(options).noneMatch(input::equals)) {
					System.out.println("Incorrect input, try again.");
					continue;
				}
			}
			return input;
		}
	}
}
