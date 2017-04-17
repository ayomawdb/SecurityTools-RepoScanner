package org.wso2.security.tools.reposcanner.storage;

import org.wso2.security.tools.reposcanner.pojo.ArtifactInfo;
import org.wso2.security.tools.reposcanner.pojo.ErrorInfo;
import org.wso2.security.tools.reposcanner.pojo.RepoInfo;

import java.sql.SQLException;

/**
 * Created by ayoma on 4/13/17.
 */
public interface Storage {
    public boolean isPresent(RepoInfo repoInfo) throws Exception;
    public boolean persist(ArtifactInfo artifactInfo) throws Exception;
    public boolean persistError(ErrorInfo errorInfo) throws Exception;
    public void close();
}
