package org.wso2.security.tools.reposcanner.artifact;

import org.wso2.security.tools.reposcanner.pojo.ArtifactInfo;
import org.wso2.security.tools.reposcanner.pojo.RepoInfo;

import java.io.File;
import java.util.Map;

/**
 * Created by ayoma on 4/15/17.
 */
public interface ArtifactInfoGenerator {
    public ArtifactInfo getArtifactInfo(String consoleTag, RepoInfo repoInfo, File baseFolder, File configFile) throws Exception;
}
