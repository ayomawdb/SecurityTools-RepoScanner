package org.wso2.security.tools.reposcanner.pojo;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by ayoma on 4/17/17.
 */
@Entity
@Table(name = "ERROR_INFO")
public class ErrorInfo {
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy=GenerationType.AUTO, generator="error_info_seq_gen")
    @SequenceGenerator(name="error_info_seq_gen", sequenceName="ERROR_INFO_SEQ")
    private Long id;

    @Column(name = "BUILD_CONFIG", length = 2048)
    private String buildConfigLocation;

    @Column(name = "ERROR_REASON")
    private String errorReason;

    @ManyToOne
    @JoinColumn(name = "REPO_INFO_ID", nullable = false)
    private RepoInfo repoInfo;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "ADDED_DATE")
    private Date addedDate;

    public ErrorInfo(String buildConfigLocation, String errorReason, RepoInfo repoInfo, Date addedDate) {
        this.buildConfigLocation = buildConfigLocation;
        this.errorReason = errorReason;
        this.repoInfo = repoInfo;
        this.addedDate = addedDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBuildConfigLocation() {
        return buildConfigLocation;
    }

    public void setBuildConfigLocation(String buildConfigLocation) {
        this.buildConfigLocation = buildConfigLocation;
    }

    public String getErrorReason() {
        return errorReason;
    }

    public void setErrorReason(String errorReason) {
        this.errorReason = errorReason;
    }

    public RepoInfo getRepoInfo() {
        return repoInfo;
    }

    public void setRepoInfo(RepoInfo repoInfo) {
        this.repoInfo = repoInfo;
    }

    public Date getAddedDate() {
        return addedDate;
    }

    public void setAddedDate(Date addedDate) {
        this.addedDate = addedDate;
    }
}
