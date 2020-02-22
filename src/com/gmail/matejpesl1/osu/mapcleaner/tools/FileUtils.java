package com.gmail.matejpesl1.osu.mapcleaner.tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Files;

public class FileUtils {
	public static final String ROOT = System.getProperty("user.home");
	
	private FileUtils() {
		
	}
	
	public static void changeFileAttribute(File file, String attributeToChange, boolean value) throws IOException, SecurityException {
		Files.setAttribute(file.toPath(), attributeToChange, value);
	}
	
	public static void writeToFile(File file, String text) throws SecurityException, IOException {
		//default values
		boolean isHidden = file.isHidden();
		boolean canWrite = file.canWrite();
		
		if (!canWrite) {
			file.setWritable(true);
		}
		
		if (isHidden) {
			changeFileAttribute(file, "dos:hidden", false);
		}
		
		BufferedWriter writer = new BufferedWriter(new FileWriter(file));
		writer.write(text);
		writer.close();
		
		if (isHidden) {
			FileUtils.changeFileAttribute(file, "dos:hidden", true);
		}
		
		if (canWrite) {
			file.setWritable(true);
		}
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
	    
	    return content.toString();
	  }
	
	public static void deleteFile(File file) throws SecurityException, IOException {
    	Files.deleteIfExists(file.toPath());
	}
}
