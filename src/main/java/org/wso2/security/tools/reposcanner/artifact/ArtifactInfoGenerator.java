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

package org.wso2.security.tools.reposcanner.artifact;

import org.wso2.security.tools.reposcanner.entiry.Repo;
import org.wso2.security.tools.reposcanner.entiry.RepoArtifact;

import java.io.File;

/**
 * Responsible of creating {{org.wso2.security.tools.reposcanner.entiry.RepoArtifact}} object using a given configuration / metadata file
 */
public interface ArtifactInfoGenerator {
    /**
     *
     * @param consoleTag Console message prefix that should be used by the method to log any information
     * @param baseFolder Repository base location being scanned
     * @param configFile Configuration or metadata file to be scanned for artifact information
     * @return {{org.wso2.security.tools.reposcanner.entiry.RepoArtifact}} object for given configuration or metadata file
     * @throws Exception
     */
    public RepoArtifact getArtifact(String consoleTag, File baseFolder, File configFile) throws Exception;
}
