package org.wso2.security.tools.reposcanner.downloader;

import org.wso2.security.tools.reposcanner.entiry.Repo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

/**
 * Created by ayoma on 4/14/17.
 */
public interface RepoDownloader {
    public void downloadRepo(Repo repoList, File destinationFolder) throws Exception;

    default void downloadFile(String sourceUrl, File destinationFile) throws IOException {
        URL website = new URL(sourceUrl);
        ReadableByteChannel rbc = Channels.newChannel(website.openStream());
        FileOutputStream fos = new FileOutputStream(destinationFile);
        fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
    }
}
