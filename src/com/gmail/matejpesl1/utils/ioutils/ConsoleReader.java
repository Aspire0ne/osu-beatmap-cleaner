package com.gmail.matejpesl1.utils.ioutils;

import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConsoleReader {
	public static final Scanner SC = new Scanner(System.in);
	public static String getInput(String[] options) {
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
	
	public static String getInput() {
		return getInput(new String[]{});
	}
	
	public static String getInput(Pattern[] patterns, boolean removeSpaces) {
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
	
	
	public static void waitForKeyPress() throws InterruptedException, IOException, SecurityException {
		new ProcessBuilder("cmd", "/c", "pause > null").inheritIO().start().waitFor();
	}
}
