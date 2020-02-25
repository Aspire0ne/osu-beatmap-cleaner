package com.gmail.matejpesl1.beatmaps;

import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.EnumSet;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import com.gmail.matejpesl1.beatmaps.Cleaner.CleanerOption;
import com.gmail.matejpesl1.utils.IOUtils;
import com.gmail.matejpesl1.utils.IOUtils.MsgType;

public class BeatmapVisitor implements FileVisitor<Path> {
	private static final IOUtils io = new IOUtils();
	private final OsuDir osuDir;
	Filter filter;
	CleanerOption option;
	String currentBeatmapBackgroundImgName = new String();
	
	int visitingErrorCounter;
	int totalErrorCounter;
	
	protected BeatmapVisitor(OsuDir osuDir) {
		this.osuDir = osuDir;
	}
	
	protected void cleanSongs(Filter filter, CleanerOption option) {
		this.filter = filter;
		this.option = option;
		
		io.println(MsgType.INFO, "Processing beatmaps...");
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
		currentBeatmapBackgroundImgName = BeatmapInfo.getBackgroundImgName(arg0);
		return FileVisitResult.CONTINUE;
	}
	
	@Override
	public FileVisitResult visitFile(Path arg0, BasicFileAttributes arg1) throws IOException {
		String filename = arg0.getFileName().toString();
		String backgroundImgName = BeatmapInfo.getBackgroundImgName(arg0.toFile());
		boolean isBackground = backgroundImgName.equals(filename);
		boolean isSound = filename.endsWith(".wav") || filename.endsWith(".mp3");
		boolean isBeatmap = filename.endsWith(".osu");
		boolean isStoryboard = Files.isDirectory(arg0) && filename.contains("Storyboard");
		BeatmapInfo info = null;
		float soundDurationSec = 0;
		
		if (isSound) {
			try {
				AudioInputStream ais = AudioSystem.getAudioInputStream(arg0.toFile());
			    AudioFormat format = ais.getFormat();
			    long audioFileLength = arg0.toFile().length();
			    int frameSize = format.getFrameSize();
			    float frameRate = format.getFrameRate();
			    soundDurationSec = (audioFileLength / (frameSize * frameRate));
			} catch (UnsupportedAudioFileException e) {
				e.printStackTrace();
			}
		}

		if (isBeatmap) {
			info = BeatmapInfo.getBeatmapInfo(arg0);
		}
		
		if (!filter.getBeatmapFilters().isEmpty()) {
			if (isBeatmap) {
				
			}
		} else {
			switch (option) {
				case REMOVE_BACKGROUNDS: {
					if (isBackground) {
						Files.delete(arg0);
					}	
				} break;
				case REMOVE_STORYBOARDS: {
					if (isStoryboard) {
						Files.delete(arg0);
					}
				} break;
				//already handled in preVisitDirectory
				case REMOVE_BEATMAPS:
					Files.delete(arg0); break;
				case REMOVE_SKIN: {
					if (filename.endsWith(".png") || filename.endsWith(".jpg") && !isBackground) {
						Files.delete(arg0);
					}
				} break;
				case REMOVE_SOUNDS: {
					if (isSound && soundDurationSec < 15) {
						Files.delete(arg0);
					}
				} break;
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
		io.println(MsgType.ERROR, "Failed visiting " + arg0.getFileName());
		++visitingErrorCounter;
		return FileVisitResult.CONTINUE;
	}
}
