package org.wso2.security.tools.reposcanner.repository;

import org.apache.log4j.Logger;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.RepositoryTag;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.RepositoryService;
import org.wso2.security.tools.reposcanner.entiry.Repo;
import org.wso2.security.tools.reposcanner.entiry.RepoType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by ayoma on 4/15/17.
 */
public class GitHubRepoInfoGenerator implements RepoInfoGenerator {
    private static Logger log = Logger.getLogger(GitHubRepoInfoGenerator.class.getName());

    private GitHubClient client;

    public GitHubRepoInfoGenerator(char[] oAuth2Token) {
        client = new GitHubClient();
        client.setOAuth2Token(new String(oAuth2Token));
    }

    @Override
    public List<Repo> getRepoList(String consoleTag, List<String> users) {
        RepositoryService repositoryService = new RepositoryService(client);
        List<Repo> repoList = Collections.synchronizedList(new ArrayList());

        //Get the list of git repositories for each GitHub user account
        users.parallelStream().forEach(user -> {
            log.info(consoleTag + "Fetching repositories for GitHub user account: " + user);
            try {
                List<Repository> userRepositoryList = repositoryService.getRepositories(user);

                //Get the list of tags for each repository
                userRepositoryList.parallelStream().forEach(repository -> {
                    log.info(consoleTag + "Fetching tags for GitHub user account: " + user + " repository: " + repository.getName());
                    try {
                        List<RepositoryTag> repositoryTagLists = repositoryService.getTags(repository);

                        //Create persistable Repo object with repository and tag information
                        repositoryTagLists.parallelStream().forEach(repositoryTag -> {
                            Repo repo = new Repo(RepoType.GIT, repository.getOwner().getLogin(), repository.getName(), repository.getCloneUrl(), repositoryTag.getName(), repositoryTag.getZipballUrl(), new Date());
                            repoList.add(repo);
                        });

                    } catch (Exception e) {
                        log.error("Error in fetching tags for GitHub user account: " + user + " repository: " + repository.getName(), e);
                    }
                });

                log.info(consoleTag + userRepositoryList.size() + " repositories found for user account: " + user);
            } catch (Exception e) {
                log.error("Error in fetching repositories for GitHub user account: " + user, e);
            }
        });

        return repoList;
    }
}
