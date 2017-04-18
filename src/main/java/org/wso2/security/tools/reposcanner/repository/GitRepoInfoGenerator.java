package org.wso2.security.tools.reposcanner.repository;

import org.apache.log4j.Logger;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.RepositoryTag;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.RepositoryService;
import org.wso2.security.tools.reposcanner.AppConfig;
import org.wso2.security.tools.reposcanner.pojo.Repo;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by ayoma on 4/15/17.
 */
public class GitRepoInfoGenerator implements  RepoInfoGenerator {
    private static Logger log = Logger.getLogger(GitRepoInfoGenerator.class.getName());

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

    public List<Repo> getRepoInfoList(String consoleTag, String[] users) throws Exception {
        List<Repository> repositoryList = getReposByUsers(consoleTag, users);
        Map<Repository, List<RepositoryTag>> repositoryTagMap = getTagsByRepositories(consoleTag, repositoryList);
        return getRepoInfoByRepositoryTagMap(consoleTag, repositoryTagMap);
    }

    private List<Repository> getReposByUsers(String consoleTag, String[] users) throws IOException, InterruptedException {
        List<Repository> repositoryList = new ArrayList<Repository>();

        RepositoryService service = new RepositoryService(client);

        for(String user : users) {
            log.info(consoleTag + "Fetching repositories for GitHub user account: " + user);
            List<Repository> userRepositoryList = service.getRepositories(user);
            repositoryList.addAll(userRepositoryList);
            log.info(consoleTag + userRepositoryList.size() + " repositories found for user account: " + user);
        }

        return repositoryList;
    }

    private Map<Repository, List<RepositoryTag>> getTagsByRepositories(String consoleTag, List<Repository> repositoryList) throws IOException, InterruptedException {
        Map<Repository, List<RepositoryTag>> repositoryTagList = new LinkedHashMap<Repository, List<RepositoryTag>>();

        RepositoryService service = new RepositoryService(client);

        ExecutorService executorService = Executors.newFixedThreadPool(AppConfig.getTagWorkerThreadCount());
        for(Repository repository : repositoryList) {
            Runnable runnable = new GitRepoTagInfoTask(repository, service, consoleTag, repositoryTagList);
            executorService.submit(runnable);
            //break;
        }
        executorService.shutdown();

        log.info(consoleTag + "Started waiting for thread completion.");
        try {
            executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
        } catch (InterruptedException e) {
            throw e;
        }
        //Do cleanup and storage release
        log.info(consoleTag + "All threads complete. Clean up tasks started.");

        return repositoryTagList;
    }

    private List<Repo> getRepoInfoByRepositoryTagMap(String consoleTag, Map<Repository, List<RepositoryTag>> repositoryTagMap) {
        List<Repo> repoList = new ArrayList<Repo>();
        for (Repository repository : repositoryTagMap.keySet()) {
            List<RepositoryTag> repositoryTagList = repositoryTagMap.get(repository);
            if(AppConfig.isVerbose()) {
                log.debug(consoleTag + "Repository    : " + repository.getName());
                log.debug(consoleTag + "   OwnerLogin : " + repository.getOwner().getLogin());
                log.debug(consoleTag + "   Clone URL  : " + repository.getCloneUrl());
                log.debug(consoleTag + "   Git URL    : " + repository.getGitUrl());
                log.debug(consoleTag + "   URL        : " + repository.getUrl());
                log.debug(consoleTag + "   HTML IRL   : " + repository.getHtmlUrl());
                log.debug(consoleTag + "   Master Br  : " + repository.getMasterBranch());
            }
            for (RepositoryTag repositoryTag : repositoryTagList) {
                if(AppConfig.isVerbose()) {
                    log.debug(consoleTag + "      Tag       : " + repositoryTag.getName());
                    log.debug(consoleTag + "      Tar       : " + repositoryTag.getTarballUrl());
                    log.debug(consoleTag + "      Zip       : " + repositoryTag.getZipballUrl());
                }
                Repo Repo = new Repo(repository, repositoryTag);
                repoList.add(Repo);
            }
        }
        return repoList;
    }
}
