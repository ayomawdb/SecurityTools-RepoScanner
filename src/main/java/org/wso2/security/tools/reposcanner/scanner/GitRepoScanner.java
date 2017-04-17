package org.wso2.security.tools.reposcanner.scanner;

import org.apache.commons.io.FileUtils;
import org.wso2.security.tools.reposcanner.AppConfig;
import org.wso2.security.tools.reposcanner.ConsoleUtil;
import org.wso2.security.tools.reposcanner.artifact.ArtifactInfoGenerator;
import org.wso2.security.tools.reposcanner.artifact.MavenArtifactInfoGenerator;
import org.wso2.security.tools.reposcanner.downloader.GitRepoDownloader;
import org.wso2.security.tools.reposcanner.downloader.RepoDownloader;
import org.wso2.security.tools.reposcanner.locator.BuildConfigLocator;
import org.wso2.security.tools.reposcanner.locator.MavenBuildConfigLocator;
import org.wso2.security.tools.reposcanner.pojo.ArtifactInfo;
import org.wso2.security.tools.reposcanner.pojo.RepoInfo;
import org.wso2.security.tools.reposcanner.repository.GitRepoInfoGenerator;
import org.wso2.security.tools.reposcanner.repository.RepoInfoGenerator;
import org.wso2.security.tools.reposcanner.storage.Storage;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by ayoma on 4/15/17.
 */
public class GitRepoScanner implements RepoScanner {
    private char[] oAuth2Token;

    public GitRepoScanner(char[] oAuth2Token) {
        this.oAuth2Token = oAuth2Token;
    }

    public void scan(Storage storage) throws Exception {
        String consoleTag = "[GIT] ";
        ConsoleUtil.println(consoleTag + "GIT repository scanning started.");

        BuildConfigLocator buildConfigLocator = new MavenBuildConfigLocator();
        ArtifactInfoGenerator mavenArtifactInfoGenerator = new MavenArtifactInfoGenerator();
        RepoDownloader gitRepoDownloader = new GitRepoDownloader();

        //Create temp folder for storing downloaded repository content
        File gitTempFolder = new File("temp-git");
        if(gitTempFolder.exists()) {
            FileUtils.deleteDirectory(gitTempFolder);
        }
        gitTempFolder.mkdir();
        ConsoleUtil.println(consoleTag + "Temporary folder created at: " + gitTempFolder.getAbsolutePath());

        //Get list of repositories from GitHub
        RepoInfoGenerator repoInfoGenerator = new GitRepoInfoGenerator(oAuth2Token);
        List<RepoInfo> repoInfoList = null;
        try {
            if(AppConfig.getGithubAccountsToScan() == null) {
                repoInfoList = new ArrayList<>();
                ConsoleUtil.printInYellow(consoleTag + "No GitHub user accounts provided for the scan. Terminating...");
            } else {
                repoInfoList = repoInfoGenerator.getRepoInfoList(consoleTag, AppConfig.getGithubAccountsToScan().toArray(new String[0]));
            }
        } catch (Exception e) {
            ConsoleUtil.printInRed(consoleTag + "Exception occurred in retrieving GitHub repositories for user accounts: " + Arrays.toString(AppConfig.getGithubAccountsToScan().toArray(new String[0])));
            throw e;
        }

        //Submit scanning task to thread pool
        ExecutorService executorService = Executors.newFixedThreadPool(AppConfig.getRepoWorkerThreadCount());
        for(RepoInfo repoInfo : repoInfoList) {
            String newConsoleTag = consoleTag + "[User:" + repoInfo.getUser() + ",Repo:" + repoInfo.getRepositoryUrl() + ",Tag:" + repoInfo.getTagName() +"] ";
            if(!storage.isPresent(repoInfo)) {
                ConsoleUtil.println(newConsoleTag + "[Adding] Adding repo to scanning pool");
                Runnable runnable = new GitRepoScannerTask(newConsoleTag, gitTempFolder, repoInfo, gitRepoDownloader, buildConfigLocator, mavenArtifactInfoGenerator, storage);
                executorService.submit(runnable);
                //break;
            } else {
                ConsoleUtil.printInYellow(newConsoleTag + "[Skipping] Repo is already present in storage.");
            }
        }
        executorService.shutdown();

        //Wait for completion of all the threads
        ConsoleUtil.println(consoleTag + "Started waiting for thread completion.");
        try {
            executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw e;
        }

        //Do cleanup and storage release
        ConsoleUtil.println(consoleTag + "All threads complete. Clean up tasks started.");
        FileUtils.deleteDirectory(gitTempFolder);
        storage.close();
    }
}
