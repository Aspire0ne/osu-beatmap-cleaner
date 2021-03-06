package com.gmail.matejpesl1.beatmaps;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.EnumSet;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import com.gmail.matejpesl1.beatmaps.BeatmapInfo.UncompleteBeatmapInfoException;
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
	File dirToMoveFilesInto;
	private boolean move;
	
	int visitingErrorCounter;
	int totalErrorCounter;
	int processedBeatmaps;
	int deletedBeatmaps;
	int deletedStoryboards;
	int deletedSkins;
	int deletedSounds;
	int deletedBackgrounds;
	
	protected BeatmapVisitor(OsuDir osuDir) {
		this.osuDir = osuDir;
	}
	
	protected Result cleanSongs(Filter filter, CleanerOption option, File dirToMoveTo) {
		io.println(MsgType.INFO, "Starting process...");
		this.filter = filter;
		this.option = option;
		this.dirToMoveFilesInto = dirToMoveTo;
		move = dirToMoveTo != null;
		try {
			Files.walkFileTree(osuDir.getSongsDir().toPath(), EnumSet.noneOf(FileVisitOption.class), 2, this);
		} catch (IOException e) {
			++totalErrorCounter;
		}
		io.println(MsgType.INFO, "Finishing process...");
		Result result = new Result(visitingErrorCounter, totalErrorCounter, processedBeatmaps);
		result.deletedBackgrounds = deletedBackgrounds;
		result.deletedBeatmaps = deletedBeatmaps;
		result.deletedSounds = deletedSounds;
		result.deletedSkins = deletedSkins;
		result.deletedStoryboards = deletedStoryboards;
		return result;
	}
	
	protected class Result {
		public int visitingErrorCounter;
		public int totalErrorCounter;
		public int processedBeatmaps;
		
		public int deletedBeatmaps;
		public int deletedStoryboards;
		public int deletedSkins;
		public int deletedSounds;
		public int deletedBackgrounds;
		
		private Result(int visitingErrorCounter, int totalErrorCounter, int processedBeatmaps) {
			this.visitingErrorCounter = visitingErrorCounter;
			this.totalErrorCounter = totalErrorCounter;
			this.processedBeatmaps = processedBeatmaps;
		}
	}
	
	@Override
	public FileVisitResult preVisitDirectory(Path arg0, BasicFileAttributes arg1) throws IOException {
		io.println(MsgType.INFO, "Visiting: " + arg0.getFileName());
		if (arg0.getFileName().toString().equals("Songs")) {
			io.println(MsgType.DEBUG, "songs folder -> continue");
			return FileVisitResult.CONTINUE;
		}
		
		io.println(MsgType.DEBUG, "1");
		++processedBeatmaps;
		if ((filter.getBeatmapFilters().isEmpty() && option == CleanerOption.REMOVE_BEATMAPS)) {
			deleteOrMove(arg0);
			return FileVisitResult.SKIP_SUBTREE;
		}
		
		io.println(MsgType.DEBUG, "2");
		try {
			currentBeatmapBackgroundImgNames = BeatmapInfo.getBackgroundImgNames(arg0.toFile());	
		} catch (UncompleteBeatmapInfoException e) {
			io.println(MsgType.ERROR, "Corrupted: " + arg0.getFileName());
			e.printStackTrace();
			return FileVisitResult.SKIP_SUBTREE;
		}
		
		io.println(MsgType.DEBUG, "3");
		
		io.println(MsgType.DEBUG, "4");
		return FileVisitResult.CONTINUE;
	}
	
	@Override
	public FileVisitResult visitFile(Path arg0, BasicFileAttributes arg1) throws IOException {
		String filename = arg0.getFileName().toString();
		
		boolean isBeatmap = filename.endsWith(".osu");
		BeatmapInfo info = null;
		try {
			info = isBeatmap ? BeatmapInfo.getBeatmapInfo(arg0) : null;
		} catch (UncompleteBeatmapInfoException e) {
			e.printStackTrace();
		}
		boolean meetsFilter = meetsFilter(info);
		
		if (!meetsFilter) return FileVisitResult.CONTINUE;
		
		boolean isBackground = currentBeatmapBackgroundImgNames.contains(filename);
		boolean isSkin = filename.endsWith(".png") || (filename.endsWith(".jpg") && !isBackground);
		boolean isSound = filename.endsWith(".wav") || filename.endsWith(".mp3");
		boolean isStoryboard = (Files.isDirectory(arg0) && filename.contains("Storyboard"));
		float soundDurationSec = isSound ? getSoundDurationSec(arg0) : -1;

		if (meetsFilter || filter.getBeatmapFilters().isEmpty()) {
			switch (option) {
			case REMOVE_BACKGROUNDS: if (isBackground) deleteOrMove(arg0); break;
			case REMOVE_STORYBOARDS: if (isStoryboard) deleteOrMove(arg0); break;
			//already handled in preVisitDirectory
			case REMOVE_BEATMAPS: if (isBeatmap) deleteOrMove(arg0); break;
			case REMOVE_SKIN: if (isSkin) deleteOrMove(arg0); break;
			case REMOVE_SOUNDS: if (isSound && soundDurationSec < 14) deleteOrMove(arg0); break;
			}
		}
		return FileVisitResult.CONTINUE;
	}
	
	private void deleteOrMove(Path file) {
		try {
			if (move) {
				Files.move(file, dirToMoveFilesInto.toPath(), StandardCopyOption.REPLACE_EXISTING);	
			} else {
				Files.delete(file);	
			}	
		} catch (IOException e) {
			++totalErrorCounter;
		}
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
			System.out.println(sound);
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
