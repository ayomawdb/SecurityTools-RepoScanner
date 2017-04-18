package org.wso2.security.tools.reposcanner.downloader;

import org.wso2.security.tools.reposcanner.pojo.Repo;
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
public class GitRepoDownloader implements RepoDownloader {
    public void downloadRepo(Repo repo, File destinationFolder) throws IOException {
        File tempZipFile = new File(destinationFolder.getAbsoluteFile() + File.separator + repo.getRepositoryName() + "-Tag-" + repo.getTagName() + ".zip");
        downloadFile(repo.getTagZip(), tempZipFile);
        unzip(tempZipFile, destinationFolder);
    }

    private void downloadFile(String sourceUrl, File destinationFile) throws IOException {
        URL website = new URL(sourceUrl);
        ReadableByteChannel rbc = Channels.newChannel(website.openStream());
        FileOutputStream fos = new FileOutputStream(destinationFile);
        fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
    }

    private void unzip(File zipFile, File zipExtractFolder) {
        ZipUtil.unpack(zipFile, zipExtractFolder);
    }
}
