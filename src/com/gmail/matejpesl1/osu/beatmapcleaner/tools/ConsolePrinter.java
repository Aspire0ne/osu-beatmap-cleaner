package com.gmail.matejpesl1.osu.beatmapcleaner.tools;

import org.eclipse.jdt.annotation.Nullable;

import com.gmail.matejpesl1.osu.beatmapcleaner.main.Main;

public class ConsolePrinter {
	public enum MsgType{ERROR, INFO, ORDINARY, DEBUG};
	
	private static void printText(MsgType msgType, String textToPrint, @Nullable Exception e, boolean newLine) {
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
	
	private static void write(StringBuilder text, boolean newLine, boolean error) {
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
	
	public static void println(MsgType msgType, String textToPrint, @Nullable Exception e) {
		printText(msgType, textToPrint, e, true);
	}
	
	public static void println(MsgType msgType, String textToPrint) {
		printText(msgType, textToPrint, null, true);
	}
	
	public static void print(MsgType msgType, String textToPrint, @Nullable Exception e) {
		printText(msgType, textToPrint, e, false);
	}
	
	public static void print(MsgType msgType, String textToPrint) {
		printText(msgType, textToPrint, null, false);
	}
}
