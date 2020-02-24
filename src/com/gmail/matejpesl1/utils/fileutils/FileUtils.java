package com.gmail.matejpesl1.utils.fileutils;

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
}
