/*
 *  Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.wso2.security.tools.reposcanner.downloader;

import org.wso2.security.tools.reposcanner.entiry.Repo;
import org.zeroturnaround.zip.ZipUtil;

import java.io.File;

/**
 * Responsible of downloading Github master branch zip files and unzip if necessary
 */
public class GitHubMasterDownloader implements RepoDownloader {
    @Override
    public void downloadRepo(Repo repo, File destinationFolder, boolean unzip) throws Exception {
        File tempZipFile = new File(destinationFolder.getAbsoluteFile() + File.separator + repo.getRepositoryName() + "-master.zip");
        DownloadUtil.downloadFile("https://github.com/" + repo.getUser() + "/" + repo.getRepositoryName() + "/archive/master.zip", tempZipFile);
        if (unzip) {
            ZipUtil.unpack(tempZipFile, destinationFolder);
        }
    }
}
