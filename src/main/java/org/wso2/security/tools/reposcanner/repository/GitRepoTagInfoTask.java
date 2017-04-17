package org.wso2.security.tools.reposcanner.repository;

import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.RepositoryTag;
import org.eclipse.egit.github.core.service.RepositoryService;
import org.wso2.security.tools.reposcanner.AppConfig;
import org.wso2.security.tools.reposcanner.ConsoleUtil;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by ayoma on 4/17/17.
 */
public class GitRepoTagInfoTask implements Runnable {
    private Repository repository;
    private RepositoryService service;
    private String consoleTag;
    private Map<Repository, List<RepositoryTag>> repositoryTagList;

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
            repositoryTagList.put(repository, repositoryTagLists);
            ConsoleUtil.println(consoleTag + repositoryTagLists.size() + " tags found for repository " + repository.getName() + " of user account: " + repository.getOwner().getLogin());
        } catch (IOException e) {
            ConsoleUtil.printInRed(consoleTag + "Unable to get tags for " + repository.getName() + " of user account: " + repository.getOwner().getLogin());
            if(AppConfig.isVerbose()) {
                e.printStackTrace();
            }
        }
    }
}
