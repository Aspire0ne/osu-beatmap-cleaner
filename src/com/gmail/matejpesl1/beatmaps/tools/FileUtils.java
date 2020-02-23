package com.gmail.matejpesl1.beatmaps.tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;

public class FileUtils {
	public static final String ROOT = System.getProperty("user.home");
	
	private FileUtils() {
		
	}
	
	public static void changeFileAttribute(File file, String attributeToChange, boolean value) throws SecurityException, IOException {
		Files.setAttribute(file.toPath(), attributeToChange, value);
	}
	
	public static void writeToFile(File file, String text) throws SecurityException, IOException {
		FileWriter fileWriter = new FileWriter(file);
		BufferedWriter writer = new BufferedWriter(fileWriter);
		writer.write(text);
		writer.close();
		fileWriter.close();
	}
	
	public static String getFileContent(File file) throws SecurityException, IOException {
		FileInputStream stream = new FileInputStream(file);
		Reader reader = new InputStreamReader(stream, "windows-1250");
		BufferedReader br = new BufferedReader(reader);

		String line;
		StringBuilder content = new StringBuilder();
		while ((line = br.readLine()) != null) {
			content.append(line).append("\n");
		}
		br.close();
	    reader.close();
	    stream.close();
		
	    return content.toString();
	  }
	
	public static void deleteFile(File file) throws SecurityException, IOException {
    	Files.deleteIfExists(file.toPath());
	}
	
	public class FileSearch implements FileVisitor<Path> {
		private ArrayList<String> namesOfDirsToSkip = new ArrayList<>();
		private final ArrayList<String> finalFiles = new ArrayList<>();
		private final ArrayList<String> keywords;
		private final ArrayList<String> extensions;
		private boolean returnFileNames;
		private String nameOfStartingDir;
		
		public FileSearch(ArrayList<String> extensions, ArrayList<String> keywords) {
			this.extensions = extensions;
			this.keywords = keywords;
		}
		
		public FileSearch(String extension, ArrayList<String> keywords) {
			if (extension != null) {
				this.extensions = new ArrayList<>(Collections.singletonList(extension));
			} else {
				this.extensions = null;
			}
			this.keywords = keywords;
		}
		
		public ArrayList<String> searchFiles(File dir, boolean includeSubDirs, boolean returnFileNames, ArrayList<String> namesOfDirsToSkip) {
			return searchFiles(Paths.get(dir.getPath()), includeSubDirs, returnFileNames, namesOfDirsToSkip);
		}
		
		public ArrayList<String> searchFiles(Path dir, boolean includeSubDirs, boolean returnFileNames, ArrayList<String> namesOfDirsToSkip) {
			this.returnFileNames = returnFileNames;
			this.namesOfDirsToSkip = namesOfDirsToSkip;
			nameOfStartingDir = dir.getFileName().toString();
			
			try {
				Files.walkFileTree(dir, EnumSet.noneOf(FileVisitOption.class), includeSubDirs ? Integer.MAX_VALUE : 1, this);
			} catch (IOException e) {
				e.printStackTrace();
				
			}
			return finalFiles;
		}
		
		
		
		
		@Override
		public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attributes) {
			String dirName = dir.getFileName().toString().toLowerCase();
			String pathToDir = dir.toString();
			if (namesOfDirsToSkip != null && namesOfDirsToSkip.parallelStream().anyMatch(dirName::equals)) {
				return FileVisitResult.SKIP_SUBTREE;
			}

			if (dirName.equals(nameOfStartingDir) || extensions != null) {
				return FileVisitResult.CONTINUE;
			}
			
			if (keywords != null) {
				if (keywords.parallelStream().anyMatch(dirName::contains)) {
					finalFiles.add(returnFileNames ? dirName + " (složka)": pathToDir);
				}
			} else {
				finalFiles.add(returnFileNames ? dirName : pathToDir);	
			}
			
			return FileVisitResult.CONTINUE;
		}

		
		
		
		@Override
		public FileVisitResult visitFile(Path filePath, BasicFileAttributes attributes) {
			String filename = filePath.getFileName().toString().toLowerCase();
			String fileExtension = filePath.toFile().isDirectory() ? "" : filename.substring(filename.lastIndexOf(".") + 1);
			String pathToFile = filePath.toString();
			boolean extensionEquals = false;
			boolean keywordEquals = false;
			
			if (extensions != null) {
				for (String extension : extensions) {
					if (fileExtension.equals(extension)) {
						extensionEquals = true;
						break;
					}
				}
			}
			
			if (!extensionEquals && extensions != null) {
				return FileVisitResult.CONTINUE;
			}
			if (keywords != null) {
				boolean containsKeyword = keywords.parallelStream().anyMatch(filename::contains);
				if (containsKeyword) {
					keywordEquals = true;
				} 
			}
			
			if (!keywordEquals && keywords != null) {
				return FileVisitResult.CONTINUE;
			}
			
			finalFiles.add(returnFileNames ? filename : pathToFile);
			return FileVisitResult.CONTINUE;
		}
		
		
		
		
		@Override
		public FileVisitResult visitFileFailed(Path file, IOException exc) {
			return FileVisitResult.CONTINUE;
		}

		
		
		
		@Override
		public FileVisitResult postVisitDirectory(Path file, IOException exc) {
			return FileVisitResult.CONTINUE;
		}
	}
}
