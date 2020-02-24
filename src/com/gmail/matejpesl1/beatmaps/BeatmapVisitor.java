package com.gmail.matejpesl1.beatmaps;

import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.EnumSet;
import com.gmail.matejpesl1.beatmaps.Cleaner.CleanerOption;
import com.gmail.matejpesl1.utils.ioutils.ConsolePrinter;

public class BeatmapVisitor extends ConsolePrinter implements FileVisitor<Path> {
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
