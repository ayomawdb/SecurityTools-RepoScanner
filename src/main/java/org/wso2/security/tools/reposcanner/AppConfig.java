package org.wso2.security.tools.reposcanner;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ayoma on 4/14/17.
 */
public class AppConfig {
    private static boolean verbose;
    private static boolean debug;
    private static boolean createDB;
    private static boolean rescanRepos;
    private static String mavenHome;
    private static List<String> githubAccounts;
    private static List<String> mavenOutputSkipPatterns;

    public static boolean isVerbose() {
        return verbose;
    }

    public static void setVerbose(boolean verbose) {
        AppConfig.verbose = verbose;
    }

    public static boolean isDebug() {
        return debug;
    }

    public static void setDebug(boolean debug) {
        AppConfig.debug = debug;
    }

    public static boolean isCreateDB() {
        return createDB;
    }

    public static void setCreateDB(boolean createDB) {
        AppConfig.createDB = createDB;
    }

    public static boolean isRescanRepos() {
        return rescanRepos;
    }

    public static void setRescanRepos(boolean rescanRepos) {
        AppConfig.rescanRepos = rescanRepos;
    }

    public static String getMavenHome() {
        return mavenHome;
    }

    public static void setMavenHome(String mavenHome) {
        AppConfig.mavenHome = mavenHome;
    }

    public static List<String> getGithubAccounts() {
        return githubAccounts;
    }

    public static void setGithubAccounts(List<String> githubAccounts) {
        AppConfig.githubAccounts = githubAccounts;
    }

    public static void addGithubAccount(String githubAccount) {
        if (AppConfig.githubAccounts == null) {
            AppConfig.githubAccounts = new ArrayList<>();
        }
        AppConfig.githubAccounts.add(githubAccount);
    }

    public static List<String> getMavenOutputSkipPatterns() {
        return mavenOutputSkipPatterns;
    }

    public static void setMavenOutputSkipPatterns(List<String> mavenOutputSkipPatterns) {
        AppConfig.mavenOutputSkipPatterns = mavenOutputSkipPatterns;
    }

    public static void addMavenOutputSkipPattern(String mavenOutputSkipPattern) {
        if (AppConfig.mavenOutputSkipPatterns == null) {
            AppConfig.mavenOutputSkipPatterns = new ArrayList<>();
        }
        AppConfig.mavenOutputSkipPatterns.add(mavenOutputSkipPattern);
    }

    static {
        AppConfig.addMavenOutputSkipPattern("[");
        AppConfig.addMavenOutputSkipPattern("Download");
    }
}
