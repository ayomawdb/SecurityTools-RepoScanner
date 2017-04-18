package org.wso2.security.tools.reposcanner.scanner;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.wso2.security.tools.reposcanner.AppConfig;
import org.wso2.security.tools.reposcanner.artifact.ArtifactInfoGenerator;
import org.wso2.security.tools.reposcanner.artifact.MavenArtifactInfoGenerator;
import org.wso2.security.tools.reposcanner.downloader.GitRepoDownloader;
import org.wso2.security.tools.reposcanner.downloader.RepoDownloader;
import org.wso2.security.tools.reposcanner.locator.BuildConfigLocator;
import org.wso2.security.tools.reposcanner.locator.MavenBuildConfigLocator;
import org.wso2.security.tools.reposcanner.pojo.Repo;
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
    private static Logger log = Logger.getLogger(GitRepoScanner.class.getName());

    private char[] oAuth2Token;

    public GitRepoScanner(char[] oAuth2Token) {
        this.oAuth2Token = oAuth2Token;
    }

    public void scan(Storage storage) throws Exception {
        String consoleTag = "[GIT] ";
        log.info(consoleTag + "GIT repository scanning started.");

        BuildConfigLocator buildConfigLocator = new MavenBuildConfigLocator();
        ArtifactInfoGenerator mavenArtifactInfoGenerator = new MavenArtifactInfoGenerator();
        RepoDownloader gitRepoDownloader = new GitRepoDownloader();

        //Create temp folder for storing downloaded repository content
        File gitTempFolder = new File("temp-git");
        if(gitTempFolder.exists()) {
            FileUtils.deleteDirectory(gitTempFolder);
        }
        gitTempFolder.mkdir();
        log.info(consoleTag + "Temporary folder created at: " + gitTempFolder.getAbsolutePath());

        //Get list of repositories from GitHub
        RepoInfoGenerator repoInfoGenerator = new GitRepoInfoGenerator(oAuth2Token);
        List<Repo> repoList = null;
        try {
            if(AppConfig.getGithubAccountsToScan() == null) {
                repoList = new ArrayList<>();
                log.error(consoleTag + "No GitHub user accounts provided for the scan. Terminating...");
                return;
            } else {
                repoList = repoInfoGenerator.getRepoInfoList(consoleTag, AppConfig.getGithubAccountsToScan().toArray(new String[0]));
            }
        } catch (Exception e) {
            log.error(consoleTag + "Exception occurred in retrieving GitHub repositories for user accounts: " + Arrays.toString(AppConfig.getGithubAccountsToScan().toArray(new String[0])),e);
            throw e;
        }

        //Submit scanning task to thread pool
        ExecutorService executorService = Executors.newFixedThreadPool(AppConfig.getRepoWorkerThreadCount());
        for(Repo repo : repoList) {
            String newConsoleTag = consoleTag + "[User:" + repo.getUser() + ",Repo:" + repo.getRepositoryUrl() + ",Tag:" + repo.getTagName() +"] ";
            if(AppConfig.isRescanRepos() || !storage.isRepoPresent(repo)) {
                log.info(newConsoleTag + "[Adding] Adding repo to scanning pool");
                Runnable runnable = new GitRepoScannerTask(newConsoleTag, gitTempFolder, repo, gitRepoDownloader, buildConfigLocator, mavenArtifactInfoGenerator, storage);
                executorService.submit(runnable);
                //break;
            } else {
                log.warn(newConsoleTag + "[Skipping] Repo is already present in storage.");
            }
        }
        executorService.shutdown();

        //Wait for completion of all the threads
        log.info(consoleTag + "Started waiting for thread completion.");
        try {
            executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
        } catch (InterruptedException e) {
            throw e;
        }

        //Do cleanup and storage release
        log.info(consoleTag + "All threads complete. Clean up tasks started.");
        FileUtils.deleteDirectory(gitTempFolder);
    }
}
