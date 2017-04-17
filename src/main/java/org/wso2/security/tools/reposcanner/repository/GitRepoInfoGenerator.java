package org.wso2.security.tools.reposcanner.repository;

import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.RepositoryTag;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.RepositoryService;
import org.wso2.security.tools.reposcanner.AppConfig;
import org.wso2.security.tools.reposcanner.ConsoleUtil;
import org.wso2.security.tools.reposcanner.pojo.RepoInfo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by ayoma on 4/15/17.
 */
public class GitRepoInfoGenerator implements  RepoInfoGenerator {
    private GitHubClient client;

    @Deprecated
    public GitRepoInfoGenerator(String username, String password) {
        client = new GitHubClient();
        client.setCredentials(username, password);
    }

    public GitRepoInfoGenerator(char[] oAuth2Token) {
        client = new GitHubClient();
        client.setOAuth2Token(new String(oAuth2Token));
    }

    public List<RepoInfo> getRepoInfoList(String consoleTag, String[] users) throws Exception {
        List<Repository> repositoryList = getReposByUsers(consoleTag, users);
        Map<Repository, List<RepositoryTag>> repositoryTagMap = getTagsByRepositories(consoleTag, repositoryList);
        return getRepoInfoByRepositoryTagMap(consoleTag, repositoryTagMap);
    }

    private List<Repository> getReposByUsers(String consoleTag, String[] users) throws IOException, InterruptedException {
        List<Repository> repositoryList = new ArrayList<Repository>();

        RepositoryService service = new RepositoryService(client);

        for(String user : users) {
            ConsoleUtil.println(consoleTag + "Fetching repositories for GitHub user account: " + user);
            List<Repository> userRepositoryList = service.getRepositories(user);
            repositoryList.addAll(userRepositoryList);
            ConsoleUtil.println(consoleTag + userRepositoryList.size() + " repositories found for user account: " + user);
        }

        return repositoryList;
    }

    private Map<Repository, List<RepositoryTag>> getTagsByRepositories(String consoleTag, List<Repository> repositoryList) throws IOException, InterruptedException {
        Map<Repository, List<RepositoryTag>> repositoryTagList = new HashMap<Repository, List<RepositoryTag>>();

        RepositoryService service = new RepositoryService(client);

        ExecutorService executorService = Executors.newFixedThreadPool(AppConfig.getTagWorkerThreadCount());
        for(Repository repository : repositoryList) {
            Runnable runnable = new GitRepoTagInfoTask(repository, service, consoleTag, repositoryTagList);
            executorService.submit(runnable);
            //break;
        }
        executorService.shutdown();

        ConsoleUtil.println(consoleTag + "Started waiting for thread completion.");
        try {
            executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw e;
        }
        //Do cleanup and storage release
        ConsoleUtil.println(consoleTag + "All threads complete. Clean up tasks started.");

        return repositoryTagList;
    }

    private List<RepoInfo> getRepoInfoByRepositoryTagMap(String consoleTag, Map<Repository, List<RepositoryTag>> repositoryTagMap) {
        List<RepoInfo> repoInfoList = new ArrayList<RepoInfo>();
        for (Repository repository : repositoryTagMap.keySet()) {
            List<RepositoryTag> repositoryTagList = repositoryTagMap.get(repository);
            if(AppConfig.isVerbose()) {
                ConsoleUtil.println(consoleTag + "Repository    : " + repository.getName());
                ConsoleUtil.println(consoleTag + "   OwnerLogin : " + repository.getOwner().getLogin());
                ConsoleUtil.println(consoleTag + "   Clone URL  : " + repository.getCloneUrl());
                ConsoleUtil.println(consoleTag + "   Git URL    : " + repository.getGitUrl());
                ConsoleUtil.println(consoleTag + "   URL        : " + repository.getUrl());
                ConsoleUtil.println(consoleTag + "   HTML IRL   : " + repository.getHtmlUrl());
                ConsoleUtil.println(consoleTag + "   Master Br  : " + repository.getMasterBranch());
            }
            for (RepositoryTag repositoryTag : repositoryTagList) {
                if(AppConfig.isVerbose()) {
                    ConsoleUtil.println(consoleTag + "      Tag       : " + repositoryTag.getName());
                    ConsoleUtil.println(consoleTag + "      Tar       : " + repositoryTag.getTarballUrl());
                    ConsoleUtil.println(consoleTag + "      Zip       : " + repositoryTag.getZipballUrl());
                }
                RepoInfo RepoInfo = new RepoInfo(repository, repositoryTag);
                repoInfoList.add(RepoInfo);
            }
        }
        return repoInfoList;
    }
}
