package com.gmail.matejpesl1.beatmaps;

import java.io.File;

import com.gmail.matejpesl1.utils.FileUtils;
import com.gmail.matejpesl1.utils.IOUtils;

public class OsuDir {
	public static final File DEFAULT_OSU_DIR = new File(FileUtils.ROOT + "\\AppData\\Local\\osu!");
	private static final IOUtils io = new IOUtils();
	private final File dir;
	private final File songsDir;
	
	public OsuDir(File osuDir) {
		this.dir = osuDir;
		songsDir = new File(osuDir.getPath() + "\\Songs");
	}
	
	public static File obtainDir() {
		if (DEFAULT_OSU_DIR.exists()) {
			System.out.println("Path to osu! directory was found:\n" + DEFAULT_OSU_DIR +
					"\n\nWould you like to change it? (y/n)");
			
			String[] options = {"y", "yes", "n", "no"};
			String input = io.getInput(options);
			
			if (input.equals(options[2]) || input.equals(options[3])) {
				return DEFAULT_OSU_DIR;
			}
		}
		return askForCustomDir();
	}
	
	private static File askForCustomDir() {
		System.out.println("Please specify osu!'s directory:");
		String beginning = FileUtils.ROOT.replace("\\", "/") + "/";
		
		while (true) {
			System.out.print(beginning);
			String inputPath = io.getInput().replace("/", "\\");
			File inputOsuDir = new File(beginning + inputPath);
			
			if (!inputOsuDir.exists()) {
				System.out.println("\nNon-existent path. Please try again.");
				continue;
			}
			return inputOsuDir;	
		}
	}
	
	public File getDir() {
		return dir;
	}
	
	public File getSongsDir() {
		return songsDir;
	}
}
