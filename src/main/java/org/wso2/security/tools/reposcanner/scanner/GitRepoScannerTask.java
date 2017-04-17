package org.wso2.security.tools.reposcanner.scanner;

import org.apache.commons.io.FileUtils;
import org.wso2.security.tools.reposcanner.AppConfig;
import org.wso2.security.tools.reposcanner.ConsoleUtil;
import org.wso2.security.tools.reposcanner.artifact.ArtifactInfoGenerator;
import org.wso2.security.tools.reposcanner.downloader.RepoDownloader;
import org.wso2.security.tools.reposcanner.locator.BuildConfigLocator;
import org.wso2.security.tools.reposcanner.pojo.ArtifactInfo;
import org.wso2.security.tools.reposcanner.pojo.RepoInfo;
import org.wso2.security.tools.reposcanner.storage.Storage;

import java.io.File;
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
    private File gitTempFolder;
    private RepoInfo repoInfo;
    private RepoDownloader gitRepoDownloader;
    private BuildConfigLocator buildConfigLocator;
    private ArtifactInfoGenerator mavenArtifactInfoGenerator;
    private Storage storage;
    private String consoleTag;

    public GitRepoScannerTask(String consoleTag, File gitTempFolder, RepoInfo repoInfo, RepoDownloader gitRepoDownloader, BuildConfigLocator buildConfigLocator, ArtifactInfoGenerator mavenArtifactInfoGenerator, Storage storage) {
        this.gitTempFolder = gitTempFolder;
        this.repoInfo = repoInfo;
        this.gitRepoDownloader = gitRepoDownloader;
        this.buildConfigLocator = buildConfigLocator;
        this.mavenArtifactInfoGenerator = mavenArtifactInfoGenerator;
        this.storage = storage;
        this.consoleTag = consoleTag;
    }

    public void run() {
        try {
            //Create folder to store files from Github
            String identifier = repoInfo.getRepositoryName() + "-Tag-" + repoInfo.getTagName();
            File artifactTempFolder = new File(gitTempFolder.getAbsoluteFile() + File.separator + identifier);
            artifactTempFolder.mkdir();
            ConsoleUtil.println(consoleTag + "Temporary folder created at: " + artifactTempFolder.getAbsolutePath());

            //Download from GitHub and extract ZIP
            ConsoleUtil.println(consoleTag + "Downloading started");
            gitRepoDownloader.downloadRepo(repoInfo, artifactTempFolder);
            ConsoleUtil.println(consoleTag + "Downloading completed");

            //Locate POM files within the extracted ZIP
            ConsoleUtil.println(consoleTag + "POM searching started");
            List<File> mavenBuildConfigFiles = buildConfigLocator.locate(artifactTempFolder);
            ConsoleUtil.println(consoleTag + "POM searching completed");

            //Execute maven executor plugin on each POM to get Maven ID (groupId, artifactId, packaging, version)
            ExecutorService executorService = Executors.newFixedThreadPool(AppConfig.getArtifactWorkerThreadCount());
            List<Future<ArtifactInfo>> artifactInfoList = new ArrayList<Future<ArtifactInfo>>();
            for (File mavenBuildConfigFile : mavenBuildConfigFiles) {
                String newConsoleTag = consoleTag + mavenBuildConfigFile.getCanonicalPath();

                ConsoleUtil.println(newConsoleTag + "[Adding] Adding POM for artifact information gathering pool");
                Callable<ArtifactInfo> callable = new GitRepoScannerArtifactInfoTask(newConsoleTag, mavenArtifactInfoGenerator, repoInfo, artifactTempFolder, mavenBuildConfigFile, storage);
                Future<ArtifactInfo> futureArtifactInfo = executorService.submit(callable);
                artifactInfoList.add(futureArtifactInfo);
            }
            executorService.shutdown();

            //Persist artifact info as threads complete
            ConsoleUtil.println(consoleTag + "Started waiting for thread completion.");
            for (Future<ArtifactInfo> artifactInfoFuture : artifactInfoList) {
                ArtifactInfo artifactInfo = artifactInfoFuture.get();
                if(artifactInfo != null) {
                    storage.persist(artifactInfo);
                }
            }

            //Do cleanup and storage release
            ConsoleUtil.println(consoleTag + "All threads complete. Clean up tasks started.");
            ConsoleUtil.println(consoleTag + "Deleting: "  + artifactTempFolder.getAbsolutePath());
            FileUtils.deleteDirectory(artifactTempFolder);
        } catch (Exception e) {
            String identifier = repoInfo.getRepositoryName() + "-Tag-" + repoInfo.getTagName();
            ConsoleUtil.printInRed(consoleTag + "Git repository scanning failed: " + identifier);
            if(AppConfig.isVerbose()) {
                e.printStackTrace();
            }
        }
    }
}
