package org.wso2.security.tools.reposcanner.downloader;

import org.wso2.security.tools.reposcanner.entiry.Repo;

import java.io.File;

/**
 * Created by ayoma on 4/14/17.
 */
public interface RepoDownloader {
    public void downloadRepo(Repo repoList, File destinationFolder) throws Exception;
}
