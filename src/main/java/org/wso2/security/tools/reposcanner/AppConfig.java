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
    private static List<String> githubAccountsToScan;
    private static List<String> mavenOutputSkipPatterns;

    private static int repoWorkerThreadCount = 1;
    private static int artifactWorkerThreadCount = 1;
    private static int tagWorkerThreadCount = 20;

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

    public static List<String> getGithubAccountsToScan() {
        return githubAccountsToScan;
    }

    public static void setGithubAccountsToScan(List<String> githubAccountsToScan) {
        AppConfig.githubAccountsToScan = githubAccountsToScan;
    }

    public static List<String> getMavenOutputSkipPatterns() {
        return mavenOutputSkipPatterns;
    }

    public static void setMavenOutputSkipPatterns(List<String> mavenOutputSkipPatterns) {
        AppConfig.mavenOutputSkipPatterns = mavenOutputSkipPatterns;
    }

    public static int getRepoWorkerThreadCount() {
        return repoWorkerThreadCount;
    }

    public static void setRepoWorkerThreadCount(int repoWorkerThreadCount) {
        AppConfig.repoWorkerThreadCount = repoWorkerThreadCount;
    }

    public static int getArtifactWorkerThreadCount() {
        return artifactWorkerThreadCount;
    }

    public static void setArtifactWorkerThreadCount(int artifactWorkerThreadCount) {
        AppConfig.artifactWorkerThreadCount = artifactWorkerThreadCount;
    }

    public static int getTagWorkerThreadCount() {
        return tagWorkerThreadCount;
    }

    public static void setTagWorkerThreadCount(int tagWorkerThreadCount) {
        AppConfig.tagWorkerThreadCount = tagWorkerThreadCount;
    }

    public static void addMavenSkipPatterns(String mavenOutputSkipPattern) {
        if(mavenOutputSkipPatterns == null) {
            mavenOutputSkipPatterns = new ArrayList<String>();
        }
        mavenOutputSkipPatterns.add(mavenOutputSkipPattern);
    }

    public static void addGithubAccountsToScan(String githubAccountToScan) {
        if(githubAccountsToScan == null) {
            githubAccountsToScan = new ArrayList<String>();
        }
        githubAccountsToScan.add(githubAccountToScan);
    }
}
