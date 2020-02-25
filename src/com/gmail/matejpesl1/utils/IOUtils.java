package com.gmail.matejpesl1.utils;

import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jdt.annotation.Nullable;

import com.gmail.matejpesl1.beatmaps.Main;

public class IOUtils {
	public enum MsgType{ERROR, INFO, ORDINARY, DEBUG};
	public static final Scanner SC = new Scanner(System.in);
	
	public String getInput(String[] options) {
		while (true) {
			String input = SC.nextLine().trim();
			if (options.length != 0) {
				if (Arrays.stream(options).noneMatch(input::equals)) {
					System.out.println("Incorrect input, please try again.");
					continue;
				}
			}
			return input;
		}
	}
	
	public String getInput() {
		return getInput(new String[]{});
	}
	
	public String getInput(Pattern[] patterns, boolean removeSpaces) {
		while (true) {
			String input = getInput(new String[]{});
			if (removeSpaces) {
				input = input.replaceAll("\\s","");
			}
			for (Pattern p : patterns) {
				Matcher m = p.matcher(input);
				if (m.matches()) {
					return input;
				}
			}
			System.out.println("Incorrect input, please try again.");
		}
	}
	
	
	public void waitForKeyPress() throws InterruptedException, IOException, SecurityException {
		new ProcessBuilder("cmd", "/c", "pause > null").inheritIO().start().waitFor();
	}
	
	private void printText(MsgType msgType, String textToPrint, @Nullable Exception e, boolean newLine) {
		StringBuilder modifText = new StringBuilder(textToPrint);
		if (msgType != MsgType.ORDINARY) {
			modifText.insert(0, '<');
			int indexOfLastChar = modifText.length();
			
			if (textToPrint.endsWith("\n")) {
				int i = textToPrint.lastIndexOf("\n");
				do {
					--i;
				} while ('\n' == textToPrint.charAt(i));
				indexOfLastChar = i + 2;
			}
			modifText.insert(indexOfLastChar, '>');
		}
		
		switch (msgType) {
			case INFO: {
				modifText.insert(0, "[INFO]: ");
				write(modifText, newLine, false);
			} break;
			case ORDINARY: {
				write(modifText, newLine, false);
			} break;
			case ERROR: {
				modifText.insert(0, "[ERROR]: ");
				write(modifText, newLine, true);
				if (e != null) {
					e.printStackTrace();	
				}
			} break;
			case DEBUG: {
				modifText.insert(0, "[DEBUG]: ");
				if (Main.isEnviromentIde) {
					write(modifText, newLine, false);
				}
			} break;
		}
	}
	
	private void write(StringBuilder text, boolean newLine, boolean error) {
		if (newLine) {
			if (error) {
				System.err.println(text);	
			} else {
				System.out.println(text);
			}
		} else {
			if (error) {
				System.err.print(text);	
			} else {
				System.out.print(text);
			}
		}
	}
	
	public void println(MsgType msgType, String textToPrint, @Nullable Exception e) {
		printText(msgType, textToPrint, e, true);
	}
	
	public void println(MsgType msgType, String textToPrint) {
		printText(msgType, textToPrint, null, true);
	}
	
	public void print(MsgType msgType, String textToPrint, @Nullable Exception e) {
		printText(msgType, textToPrint, e, false);
	}
	
	public void print(MsgType msgType, String textToPrint) {
		printText(msgType, textToPrint, null, false);
	}
}
