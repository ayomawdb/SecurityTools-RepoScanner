package org.wso2.security.tools.reposcanner.scanner;

import org.wso2.security.tools.reposcanner.AppConfig;
import org.wso2.security.tools.reposcanner.ConsoleUtil;
import org.wso2.security.tools.reposcanner.artifact.ArtifactInfoGenerator;
import org.wso2.security.tools.reposcanner.pojo.ArtifactInfo;
import org.wso2.security.tools.reposcanner.pojo.ErrorInfo;
import org.wso2.security.tools.reposcanner.pojo.RepoInfo;
import org.wso2.security.tools.reposcanner.storage.Storage;

import java.io.File;
import java.util.Date;
import java.util.concurrent.Callable;

/**
 * Created by ayoma on 4/16/17.
 */
public class GitRepoScannerArtifactInfoTask implements Callable<ArtifactInfo> {
    private ArtifactInfoGenerator mavenArtifactInfoGenerator;
    private RepoInfo repoInfo;
    private File artifactTempFolder;
    private File mavenBuildConfigFile;
    private String consoleTag;
    private Storage storage;

    public GitRepoScannerArtifactInfoTask(String consoleTag, ArtifactInfoGenerator mavenArtifactInfoGenerator, RepoInfo repoInfo, File artifactTempFolder, File mavenBuildConfigFile, Storage storage) {
        this.mavenArtifactInfoGenerator = mavenArtifactInfoGenerator;
        this.repoInfo = repoInfo;
        this.artifactTempFolder = artifactTempFolder;
        this.mavenBuildConfigFile = mavenBuildConfigFile;
        this.consoleTag = consoleTag;
        this.storage = storage;
    }

    public ArtifactInfo call() throws Exception {
        try {
            ArtifactInfo artifactInfo = mavenArtifactInfoGenerator.getArtifactInfo(consoleTag, repoInfo, artifactTempFolder, mavenBuildConfigFile);
            ConsoleUtil.println(consoleTag + "Maven ID extracted. Sending for storage.");
            return artifactInfo;
        } catch (Exception e) {
            String path = mavenBuildConfigFile.getAbsolutePath().substring(artifactTempFolder.getAbsolutePath().length(), mavenBuildConfigFile.getAbsolutePath().length());
            ErrorInfo errorInfo = new ErrorInfo(path, "MavenID not found", repoInfo, new Date());
            storage.persistError(errorInfo);
            ConsoleUtil.printInYellow(consoleTag + "[Skipping] Could not extract Maven ID from Maven executor");
            if(AppConfig.isVerbose()) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
