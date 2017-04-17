package org.wso2.security.tools.reposcanner.downloader;

import org.wso2.security.tools.reposcanner.pojo.RepoInfo;

import java.io.File;
import java.util.List;

/**
 * Created by ayoma on 4/14/17.
 */
public interface RepoDownloader {
    public void downloadRepo(RepoInfo repoInfoList, File destinationFolder) throws Exception;
}
