package org.wso2.security.tools.reposcanner.pojo;

import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.RepositoryTag;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by ayoma on 4/11/17.
 */
@Entity
@Table(name = "REPO_INFO")
public class RepoInfo {
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy=GenerationType.AUTO, generator="repo_info_seq_gen")
    @SequenceGenerator(name="repo_info_seq_gen", sequenceName="REPO_INFO_SEQ")
    private Long id;

    @Column(name = "REPO_TYPE", nullable = false)
    @Enumerated(EnumType.STRING)
    private RepoType repoType;

    @Column(name = "USER", nullable = false)
    private String user;

    @Column(name = "REPO_NAME", nullable = false)
    private String repositoryName;

    @Column(name = "REPO_URL", nullable = false, length = 2048)
    private String repositoryUrl;

    @Column(name = "TAG_NAME", nullable = false)
    private String tagName;

    @Column(name = "TAG_ZIP", nullable = false, length = 2048)
    private String tagZip;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "ADDED_DATE")
    private Date addedDate;

    public RepoInfo() {
    }

    public RepoInfo(Repository repository, RepositoryTag repositoryTag) {
        this.repoType=RepoType.GIT;
        this.user = repository.getOwner().getLogin();
        this.repositoryName = repository.getName();
        this.repositoryUrl = repository.getCloneUrl();
        this.tagName = repositoryTag.getName();
        this.tagZip = repositoryTag.getZipballUrl();
        this.addedDate = new Date();
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public RepoType getRepoType() {
        return repoType;
    }

    public void setRepoType(RepoType repoType) {
        this.repoType = repoType;
    }

    public String getRepositoryName() {
        return repositoryName;
    }

    public void setRepositoryName(String repositoryName) {
        this.repositoryName = repositoryName;
    }

    public String getRepositoryUrl() {
        return repositoryUrl;
    }

    public void setRepositoryUrl(String repositoryUrl) {
        this.repositoryUrl = repositoryUrl;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public String getTagZip() {
        return tagZip;
    }

    public void setTagZip(String tagZip) {
        this.tagZip = tagZip;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RepoInfo gitInfo = (RepoInfo) o;

        if (!repositoryName.equals(gitInfo.repositoryName)) return false;
        if (!repositoryUrl.equals(gitInfo.repositoryUrl)) return false;
        return tagName.equals(gitInfo.tagName);

    }

    @Override
    public int hashCode() {
        int result = repositoryName.hashCode();
        result = 31 * result + repositoryUrl.hashCode();
        result = 31 * result + tagName.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "GitInfo{" +
                "repositoryName='" + repositoryName + '\'' +
                ", repositoryUrl='" + repositoryUrl + '\'' +
                ", tagName='" + tagName + '\'' +
                ", tagZip='" + tagZip + '\'' +
                '}';
    }
}
