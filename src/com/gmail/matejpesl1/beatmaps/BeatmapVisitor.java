package com.gmail.matejpesl1.beatmaps;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.EnumSet;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import com.gmail.matejpesl1.beatmaps.Cleaner.CleanerOption;
import com.gmail.matejpesl1.beatmaps.Filter.BeatmapFilter;
import com.gmail.matejpesl1.utils.IOUtils;
import com.gmail.matejpesl1.utils.IOUtils.MsgType;

public class BeatmapVisitor implements FileVisitor<Path> {
	ArrayList<String> currentBeatmapBackgroundImgNames = new ArrayList<>();
	private static final IOUtils io = new IOUtils();
	private final OsuDir osuDir;
	CleanerOption option;
	Filter filter;
	
	int visitingErrorCounter;
	int totalErrorCounter;
	
	protected BeatmapVisitor(OsuDir osuDir) {
		this.osuDir = osuDir;
	}
	
	protected void cleanSongs(Filter filter, CleanerOption option) {
		io.println(MsgType.INFO, "Processing beatmaps...");
		this.filter = filter;
		this.option = option;
		try {
			//TODO: Test maxDepth and potentially change it to 2
			Files.walkFileTree(osuDir.getSongsDir().toPath(), EnumSet.noneOf(FileVisitOption.class), 1 , this);
		} catch (IOException e) {
			++totalErrorCounter;
		}
	}
	
	@Override
	public FileVisitResult preVisitDirectory(Path arg0, BasicFileAttributes arg1) throws IOException {
		io.println(MsgType.INFO, "Visiting: " + arg0.getFileName());
		
		if (filter.getBeatmapFilters().isEmpty() && option == CleanerOption.REMOVE_BEATMAPS) {
			Files.delete(arg0);
			return FileVisitResult.SKIP_SUBTREE;
		}
		
		currentBeatmapBackgroundImgNames = BeatmapInfo.getBackgroundImgNames(arg0.toFile());
		
		if (currentBeatmapBackgroundImgNames.isEmpty()) {
			io.print(MsgType.INFO, "Corrupted: " + arg0.getFileName());
			return FileVisitResult.SKIP_SUBTREE;
		}
		
		return FileVisitResult.CONTINUE;
	}
	
	@Override
	public FileVisitResult visitFile(Path arg0, BasicFileAttributes arg1) throws IOException {
		String filename = arg0.getFileName().toString();
		
		boolean isBeatmap = filename.endsWith(".osu");
		BeatmapInfo info = isBeatmap ? BeatmapInfo.getBeatmapInfo(arg0) : null;
		boolean meetsFilter = meetsFilter(info);
		
		if (!meetsFilter) return FileVisitResult.CONTINUE;
		
		boolean isBackground = currentBeatmapBackgroundImgNames.contains(filename);
		boolean isSkin = filename.endsWith(".png") || (filename.endsWith(".jpg") && !isBackground);
		boolean isSound = filename.endsWith(".wav") || filename.endsWith(".mp3");
		boolean isStoryboard = (Files.isDirectory(arg0) && filename.contains("Storyboard"));
		float soundDurationSec = isSound ? getSoundDurationSec(arg0) : null;
		
		if (meetsFilter || filter.getBeatmapFilters().isEmpty()) {
			switch (option) {
			case REMOVE_BACKGROUNDS: if (isBackground) Files.delete(arg0); break;
			case REMOVE_STORYBOARDS: if (isStoryboard) Files.delete(arg0); break;
			//already handled in preVisitDirectory
			case REMOVE_BEATMAPS: if (isBeatmap) Files.delete(arg0); break;
			case REMOVE_SKIN: if (isSkin) Files.delete(arg0); break;
			case REMOVE_SOUNDS: if (isSound && soundDurationSec < 14) Files.delete(arg0); break;
			}
		}
		return FileVisitResult.CONTINUE;
	}
	
	private boolean meetsFilter(BeatmapInfo info) {
		for (BeatmapFilter bFilter : filter.getBeatmapFilters()) {
			boolean meets = true;
			switch (bFilter) {
				case MODE: meets = (filter.getMode() == info.getMode()); break;
				case DIFFICULTY: {
					switch (filter.getDifficultyOperator()) {
					case GREATER_THAN: meets = info.getDifficulty() > filter.getDifficulty(); break;
					case LESS_THAN: meets = info.getDifficulty() < filter.getDifficulty(); break;
					case NONE: meets = info.getDifficulty() == filter.getDifficulty(); break;
					}
				} break;
			}
			if (!meets) return false;
		}
		return true;
	}
	
	private float getSoundDurationSec(Path sound) {
		File soundFile = sound.toFile();
		float soundDurationSec = -1;

		try {
			AudioInputStream ais = AudioSystem.getAudioInputStream(soundFile);
			AudioFormat format = ais.getFormat();
		    long audioFileLength = soundFile.length();
		    int frameSize = format.getFrameSize();
		    float frameRate = format.getFrameRate();
		    soundDurationSec = (audioFileLength / (frameSize * frameRate));
		} catch (IOException | UnsupportedAudioFileException e) {
			e.printStackTrace();
		}
		return soundDurationSec;
	}
	
	@Override
	public FileVisitResult postVisitDirectory(Path arg0, IOException arg1) throws IOException {
		return FileVisitResult.CONTINUE;
	}

	@Override
	public FileVisitResult visitFileFailed(Path arg0, IOException arg1) throws IOException {
		io.println(MsgType.ERROR, "Failed visiting " + arg0.getFileName());
		++visitingErrorCounter;
		return FileVisitResult.CONTINUE;
	}
}
