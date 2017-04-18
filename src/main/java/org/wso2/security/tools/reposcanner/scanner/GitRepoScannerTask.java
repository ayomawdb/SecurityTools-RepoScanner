package org.wso2.security.tools.reposcanner.scanner;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.wso2.security.tools.reposcanner.AppConfig;
import org.wso2.security.tools.reposcanner.artifact.ArtifactInfoGenerator;
import org.wso2.security.tools.reposcanner.downloader.RepoDownloader;
import org.wso2.security.tools.reposcanner.locator.BuildConfigLocator;
import org.wso2.security.tools.reposcanner.pojo.RepoArtifact;
import org.wso2.security.tools.reposcanner.pojo.Repo;
import org.wso2.security.tools.reposcanner.storage.Storage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by ayoma on 4/16/17.
 */
public class GitRepoScannerTask implements Runnable {
    private static Logger log = Logger.getLogger(GitRepoScanner.class.getName());

    private File gitTempFolder;
    private Repo repo;
    private RepoDownloader gitRepoDownloader;
    private BuildConfigLocator buildConfigLocator;
    private ArtifactInfoGenerator mavenArtifactInfoGenerator;
    private Storage storage;
    private String consoleTag;

    public GitRepoScannerTask(String consoleTag, File gitTempFolder, Repo repo, RepoDownloader gitRepoDownloader, BuildConfigLocator buildConfigLocator, ArtifactInfoGenerator mavenArtifactInfoGenerator, Storage storage) {
        this.gitTempFolder = gitTempFolder;
        this.repo = repo;
        this.gitRepoDownloader = gitRepoDownloader;
        this.buildConfigLocator = buildConfigLocator;
        this.mavenArtifactInfoGenerator = mavenArtifactInfoGenerator;
        this.storage = storage;
        this.consoleTag = consoleTag;
    }

    public void run() {
        //Create folder to store files from Github
        String identifier = repo.getRepositoryName() + "-Tag-" + repo.getTagName();
        File artifactTempFolder = new File(gitTempFolder.getAbsoluteFile() + File.separator + identifier);
        artifactTempFolder.mkdir();
        log.info(consoleTag + "Temporary folder created at: " + artifactTempFolder.getAbsolutePath());

        try {
            //Download from GitHub and extract ZIP
            log.info(consoleTag + "Downloading started");
            gitRepoDownloader.downloadRepo(repo, artifactTempFolder);
            log.info(consoleTag + "Downloading completed");

            //Locate POM files within the extracted ZIP
            log.info(consoleTag + "POM searching started");
            List<File> mavenBuildConfigFiles = buildConfigLocator.locate(artifactTempFolder);
            log.info(consoleTag + "POM searching completed");

            //Execute maven executor plugin on each POM to get Maven ID (groupId, artifactId, packaging, version)
            ExecutorService executorService = Executors.newFixedThreadPool(AppConfig.getArtifactWorkerThreadCount());
            List<Future<RepoArtifact>> artifactInfoList = new ArrayList<Future<RepoArtifact>>();
            for (File mavenBuildConfigFile : mavenBuildConfigFiles) {
                String path = mavenBuildConfigFile.getAbsolutePath().substring(artifactTempFolder.getAbsolutePath().length(), mavenBuildConfigFile.getAbsolutePath().length());

                String newConsoleTag = consoleTag + "[" + path + "] ";

                boolean scanArtifact = true;
                if(AppConfig.isRescanRepos() && storage.isArtifactPresent(repo, path)) {
                    scanArtifact = false;
                }
                if(scanArtifact) {
                    log.info(newConsoleTag + "[Adding] Adding POM for artifact information gathering pool");
                    Callable<RepoArtifact> callable = new GitRepoScannerArtifactInfoTask(newConsoleTag, mavenArtifactInfoGenerator, repo, artifactTempFolder, mavenBuildConfigFile, storage);
                    Future<RepoArtifact> futureArtifactInfo = executorService.submit(callable);
                    artifactInfoList.add(futureArtifactInfo);
                } else {
                    log.warn(newConsoleTag + "[Skipping] Artifact is already present in storage.");
                }
            }
            executorService.shutdown();

            //Persist artifact info as threads complete
            log.info(consoleTag + "Started waiting for thread completion.");
            for (Future<RepoArtifact> artifactInfoFuture : artifactInfoList) {
                RepoArtifact repoArtifactInfo = artifactInfoFuture.get();
                if(repoArtifactInfo != null) {
                    storage.persist(repoArtifactInfo);
                }
            }

            //Do cleanup and storage release
            log.info(consoleTag + "All threads complete. Clean up tasks started.");
            log.info(consoleTag + "Deleting: "  + artifactTempFolder.getAbsolutePath());
            FileUtils.deleteDirectory(artifactTempFolder);
        } catch (Exception e) {
            try {
                FileUtils.deleteDirectory(artifactTempFolder);
            } catch (IOException e1) {
                log.warn( "Exception in removing temp folder: " + artifactTempFolder.getAbsolutePath());
            }
            log.error(consoleTag + "Git repository scanning failed: " + identifier, e);
        }
    }
}
