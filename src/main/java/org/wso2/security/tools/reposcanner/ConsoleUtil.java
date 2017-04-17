package org.wso2.security.tools.reposcanner;

/**
 * Created by ayoma on 4/2/17.
 */
public class ConsoleUtil {
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_YELLOW = "\u001B[33m";

    public static void printInRed(String message) {
        System.out.println(ANSI_RED + message + ANSI_RESET);
    }

    public static void printInYellow(String message) {
        System.out.println(ANSI_YELLOW + message + ANSI_RESET);
    }

    public static void printInGreen(String message) {
        System.out.println(ANSI_GREEN + message + ANSI_RESET);
    }

    public static void println(String message) {
        System.out.println(message);
    }

    public static void printDebug(String message) {
        System.out.println(message);
    }
}
