package org.wso2.security.tools.reposcanner.downloader;

import org.wso2.security.tools.reposcanner.entiry.Repo;
import org.zeroturnaround.zip.ZipUtil;

import java.io.File;

/**
 * Created by ayoma on 4/20/17.
 */
public class GitHubMasterDownloader implements RepoDownloader {
    @Override
    public void downloadRepo(Repo repo, File destinationFolder) throws Exception {
        File tempZipFile = new File(destinationFolder.getAbsoluteFile() + File.separator + repo.getRepositoryName() + "-master.zip");
        downloadFile("https://github.com/" +repo.getUser()+ "/"+repo.getRepositoryName()+"/archive/master.zip", tempZipFile);
        ZipUtil.unpack(tempZipFile, destinationFolder);
    }
}
