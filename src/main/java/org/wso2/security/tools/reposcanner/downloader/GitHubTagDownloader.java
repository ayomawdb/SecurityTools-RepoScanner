package org.wso2.security.tools.reposcanner.downloader;

import org.wso2.security.tools.reposcanner.entiry.Repo;
import org.zeroturnaround.zip.ZipUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

/**
 * Created by ayoma on 4/14/17.
 */
public class GitHubTagDownloader implements RepoDownloader {
    @Override
    public void downloadRepo(Repo repo, File destinationFolder) throws IOException {
        File tempZipFile = new File(destinationFolder.getAbsoluteFile() + File.separator + repo.getRepositoryName() + "-Tag-" + repo.getTagName() + ".zip");
        downloadFile(repo.getTagZip(), tempZipFile);
        ZipUtil.unpack(tempZipFile, destinationFolder);
    }
}
