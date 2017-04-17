package org.wso2.security.tools.reposcanner.repository;

import org.wso2.security.tools.reposcanner.pojo.RepoInfo;

import java.util.List;

/**
 * Created by ayoma on 4/15/17.
 */
public interface RepoInfoGenerator {
    public List<RepoInfo> getRepoInfoList(String consleTag, String[] users) throws Exception;
}
