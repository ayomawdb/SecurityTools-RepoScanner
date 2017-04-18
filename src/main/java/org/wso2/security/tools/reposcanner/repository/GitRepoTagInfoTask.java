package org.wso2.security.tools.reposcanner.repository;

import org.apache.log4j.Logger;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.RepositoryTag;
import org.eclipse.egit.github.core.service.RepositoryService;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by ayoma on 4/17/17.
 */
public class GitRepoTagInfoTask implements Runnable {
    private static Logger log = Logger.getLogger(GitRepoTagInfoTask.class.getName());

    private Repository repository;
    private RepositoryService service;
    private String consoleTag;
    private volatile Map<Repository, List<RepositoryTag>> repositoryTagList;

    public GitRepoTagInfoTask(Repository repository, RepositoryService service, String consoleTag, Map<Repository, List<RepositoryTag>> repositoryTagList) {
        this.repository = repository;
        this.service = service;
        this.consoleTag = consoleTag;
        this.repositoryTagList = repositoryTagList;
    }

    @Override
    public void run() {
        try {
            List<RepositoryTag> repositoryTagLists = service.getTags(repository);
            synchronized (repositoryTagList) {
                repositoryTagList.put(repository, repositoryTagLists);
            }
            log.info(consoleTag + repositoryTagLists.size() + " tags found for repository " + repository.getName() + " of user account: " + repository.getOwner().getLogin());
        } catch (IOException e) {
            log.error(consoleTag + "Unable to get tags for " + repository.getName() + " of user account: " + repository.getOwner().getLogin(), e);
        }
    }
}
