package org.wso2.security.tools.reposcanner.downloader;

import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.RepositoryTag;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.RepositoryService;
import org.wso2.security.tools.reposcanner.AppConfig;
import org.wso2.security.tools.reposcanner.pojo.RepoInfo;
import org.zeroturnaround.zip.ZipUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ayoma on 4/14/17.
 */
public class GitRepoDownloader implements RepoDownloader {
    public void downloadRepo(RepoInfo repoInfo, File destinationFolder) throws IOException {
        File tempZipFile = new File(destinationFolder.getAbsoluteFile() + File.separator + repoInfo.getRepositoryName() + "-Tag-" + repoInfo.getTagName() + ".zip");
        downloadFile(repoInfo.getTagZip(), tempZipFile);
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
