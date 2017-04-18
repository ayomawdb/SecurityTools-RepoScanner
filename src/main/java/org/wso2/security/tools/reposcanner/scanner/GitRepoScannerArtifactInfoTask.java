package org.wso2.security.tools.reposcanner.scanner;

import org.apache.log4j.Logger;
import org.wso2.security.tools.reposcanner.AppConfig;
import org.wso2.security.tools.reposcanner.artifact.ArtifactInfoGenerator;
import org.wso2.security.tools.reposcanner.pojo.RepoArtifact;
import org.wso2.security.tools.reposcanner.pojo.RepoError;
import org.wso2.security.tools.reposcanner.pojo.Repo;
import org.wso2.security.tools.reposcanner.storage.Storage;

import java.io.File;
import java.util.Date;
import java.util.concurrent.Callable;

/**
 * Created by ayoma on 4/16/17.
 */
public class GitRepoScannerArtifactInfoTask implements Callable<RepoArtifact> {
    private static Logger log = Logger.getLogger(GitRepoScannerArtifactInfoTask.class.getName());

    private ArtifactInfoGenerator mavenArtifactInfoGenerator;
    private Repo repo;
    private File artifactTempFolder;
    private File mavenBuildConfigFile;
    private String consoleTag;
    private Storage storage;

    public GitRepoScannerArtifactInfoTask(String consoleTag, ArtifactInfoGenerator mavenArtifactInfoGenerator, Repo repo, File artifactTempFolder, File mavenBuildConfigFile, Storage storage) {
        this.mavenArtifactInfoGenerator = mavenArtifactInfoGenerator;
        this.repo = repo;
        this.artifactTempFolder = artifactTempFolder;
        this.mavenBuildConfigFile = mavenBuildConfigFile;
        this.consoleTag = consoleTag;
        this.storage = storage;
    }

    public RepoArtifact call() throws Exception {
        try {
            RepoArtifact repoArtifactInfo = mavenArtifactInfoGenerator.getArtifactInfo(consoleTag, repo, artifactTempFolder, mavenBuildConfigFile);
            log.info(consoleTag + "Maven ID extracted. Sending for storage.");
            return repoArtifactInfo;
        } catch (Exception e) {
            String path = mavenBuildConfigFile.getAbsolutePath().substring(artifactTempFolder.getAbsolutePath().length(), mavenBuildConfigFile.getAbsolutePath().length());
            RepoError repoError = new RepoError(path, "MavenID not found", repo, new Date());
            storage.persistError(repoError);

            if(AppConfig.isVerbose()) {
                log.warn(consoleTag + "[Skipping] Could not extract Maven ID from Maven executor", e);
            } else {
                log.warn(consoleTag + "[Skipping] Could not extract Maven ID from Maven executor");
            }
            return null;
        }
    }
}
