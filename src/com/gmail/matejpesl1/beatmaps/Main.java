package com.gmail.matejpesl1.beatmaps;

import com.gmail.matejpesl1.beatmaps.tools.ConsolePrinter;

public class Main extends ConsolePrinter {
	public static boolean isEnviromentIde;
	
	public static void main(String[] args) {
		isEnviromentIde = isEnviromentIde();
		println(MsgType.INFO,
				"Cleaner has been launched in " + (isEnviromentIde ? "IDE mode" : "non-IDE mode") + "\n");
		showLogo();
		sleep(3000);
		Cleaner cleaner = new Cleaner();
		cleaner.showMenu();
	}
	
	private static void showLogo() {
		print(MsgType.ORDINARY,
				"\r\n" + 
				"                    ____            _                         \r\n" + 
				"                   / __ \\          | |                        \r\n" + 
				"                  | |  | |___ _   _| |                        \r\n" + 
				"                  | |  | / __| | | | |                        \r\n" + 
				"                  | |__| \\__ \\ |_| |_|                        \r\n" + 
				"                   \\____/|___/\\__,_(_)                        \r\n" + 
				"                               _                            \r\n" + 
				"                              | |                           \r\n" + 
				"  _ __ ___   __ _ _ __     ___| | ___  __ _ _ __   ___ _ __ \r\n" + 
				" | '_ ` _ \\ / _` | '_ \\   / __| |/ _ \\/ _` | '_ \\ / _ \\ '__|\r\n" +
				" | | | | | | (_| | |_) | | (__| |  __/ (_| | | | |  __/ |   \r\n" + 
				" |_| |_| |_|\\__,_| .__/   \\___|_|\\___|\\__,_|_| |_|\\___|_|   \r\n" + 
				"                 | |                                        \r\n");
		print(MsgType.ORDINARY,
				"                 |_|");
		sleep(2500);
        println(MsgType.ORDINARY, "               by matejpesl1@gmail.com       \r\n");
	}
	
	public static boolean isEnviromentIde() {
	    String runPath = Cleaner.class.getResource("Cleaner.class").getPath();
	    /*if the program is compiled with "Package required libraries into generated JAR" option,
	    Eclipse will use its jar-in-jar loader and the resource url will start with rsrc:*/
	    boolean startedFromJar = (runPath.startsWith("jar:") || runPath.startsWith("rsrc:"));
	    
	    String classPath = System.getProperty("java.class.path").toLowerCase();
	    boolean isEclipse = classPath.contains("eclipse");
	    boolean isNetbeans = classPath.contains("neatbeans");
	    boolean isIdea = classPath.contains("idea_rt.jar");
	    return (isEclipse || isIdea || !startedFromJar || isNetbeans);
	}
	
	public static void sleep(int duration) {
		try {
			if (!isEnviromentIde) {
				Thread.sleep(duration);	
			}
			
		} catch (InterruptedException e) {
			println(MsgType.ERROR, "Main thread was interrupted from sleep", e);
		}
	}
}
