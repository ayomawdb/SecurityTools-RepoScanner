package org.wso2.security.tools.reposcanner.artifact;

import org.wso2.security.tools.reposcanner.pojo.RepoArtifact;
import org.wso2.security.tools.reposcanner.pojo.Repo;

import java.io.File;

/**
 * Created by ayoma on 4/15/17.
 */
public interface ArtifactInfoGenerator {
    public RepoArtifact getArtifactInfo(String consoleTag, Repo repo, File baseFolder, File configFile) throws Exception;
}
