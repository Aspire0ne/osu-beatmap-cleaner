package com.gmail.matejpesl1.beatmaps;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.gmail.matejpesl1.beatmaps.BeatmapInfo.Info;
import com.gmail.matejpesl1.beatmaps.Cleaner.CleanerOption;
import com.gmail.matejpesl1.beatmaps.Filter.OsuMode;
import com.gmail.matejpesl1.utils.ioutils.ConsolePrinter;

public class BeatmapVisitor extends ConsolePrinter implements FileVisitor<Path> {
	private static final ArrayList<String> regionsToRead =
			new ArrayList<>(Arrays.asList("[Events]", "[General]", "[Difficulty]"));
	private final OsuDir osuDir;
	Filter filter;
	CleanerOption option;
	
	int visitingErrorCounter;
	int totalErrorCounter;
	
	protected BeatmapVisitor(OsuDir osuDir) {
		this.osuDir = osuDir;
	}
	protected void cleanSongs(Filter filter, CleanerOption option) {
		this.filter = filter;
		this.option = option;
		
		println(MsgType.INFO, "Processing beatmaps...");
		try {
			//TODO: Test maxDepth and potentially change it to 2
			Files.walkFileTree(osuDir.getSongsDir().toPath(), EnumSet.noneOf(FileVisitOption.class), 1 , this);
		} catch (IOException e) {
			++totalErrorCounter;
		}
	}
	
	@Override
	public FileVisitResult preVisitDirectory(Path arg0, BasicFileAttributes arg1) throws IOException {
		println(MsgType.INFO, "Visiting: " + arg0.getFileName());
		
		if (filter.getBeatmapFilters().isEmpty() && option == CleanerOption.REMOVE_BEATMAPS) {
			Files.delete(arg0);
			return FileVisitResult.SKIP_SUBTREE;
		}
		return FileVisitResult.CONTINUE;
	}
	
	@Override
	public FileVisitResult visitFile(Path arg0, BasicFileAttributes arg1) throws IOException {
		if (arg0.endsWith(".osu")) {
			
		}
		if (!filter.getBeatmapFilters().isEmpty()) {
			if (arg0.endsWith(".osu")) {
				
			}
		} else {
			switch (option) {
				case REMOVE_BACKGROUNDS:
					if (arg0.endsWith(".jpg"))
						Files.delete(arg0);
					break;
				case REMOVE_STORYBOARDS:
					if (Files.isDirectory(arg0) && arg0.getFileName().toString().equals("Storyboard"))
						Files.delete(arg0);
					break;
			/*if there is no filter and option is delete beatmap,
			the entire folder is deleted in preVisitDirectory, which
			means that this case will never be true */
			case REMOVE_BEATMAPS:
				Files.delete(arg0); break;
			case REMOVE_SKIN:
				break;
			case REMOVE_SOUNDS:
				break;
			default:
				break;
			}
		}
	}
	
	private BeatmapInfo getBeatmapInfo(Path beatmap, ArrayList<Info> infoToReturn) throws IOException {
		FileInputStream stream = new FileInputStream(beatmap.toFile());
		Reader reader = new InputStreamReader(stream, "windows-1250");
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
	    
		float difficulty = Float.parseFloat(diffLine.replaceAll("[^\\d.]", ""));
		byte modeNum = Byte.parseByte(modeLine.replaceAll("[^\\d.]", ""));
		OsuMode mode = null;
		String backgroundImgName = 
				backgroundImgNameLine.substring(
						backgroundImgNameLine.indexOf('\"')+1,
						backgroundImgNameLine.lastIndexOf('\"'));
		switch (modeNum) {
		case 0: mode = OsuMode.STANDARD; break;
		case 1: mode = OsuMode.CATCH; break;
		case 2: mode = OsuMode.TAIKO; break;
		case 3: mode = OsuMode.MANIA; break;
		}
		
	    return new BeatmapInfo(backgroundImgName, mode, difficulty);
	}
	
	@Override
	public FileVisitResult postVisitDirectory(Path arg0, IOException arg1) throws IOException {
		//TODO: check if all .osu files were deleted from a song folder and if yes, delete the folder
		return FileVisitResult.CONTINUE;
	}

	@Override
	public FileVisitResult visitFileFailed(Path arg0, IOException arg1) throws IOException {
		println(MsgType.ERROR, "Failed visiting " + arg0.getFileName());
		++visitingErrorCounter;
		return FileVisitResult.CONTINUE;
	}
}
