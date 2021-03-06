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

import java.io.File;

/**
 * Responsible of source repository downloading
 */
public interface RepoDownloader {
    /**
     * Downloading given repository into the destination
     *
     * @param repo Repository to download
     * @param destinationFolder Destination of the downloaded file
     * @param unzip Should zip downloaded get unzipped
     * @throws Exception
     */
    public void downloadRepo(Repo repo, File destinationFolder, boolean unzip) throws Exception;

    default void downloadRepo(Repo repo, File destinationFolder) throws Exception {
        downloadRepo(repo, destinationFolder, true);
    }
}
