package org.wso2.security.tools.reposcanner.pojo;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by ayoma on 4/11/17.
 */
@Entity
@Table(name = "ARTIFACT_INFO")
public class ArtifactInfo {
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy=GenerationType.AUTO, generator="artifact_info_seq_gen")
    @SequenceGenerator(name="artifact_info_seq_gen", sequenceName="ARTIFACT_INFO_SEQ")
    private Long id;

    @Column(name = "PATH", nullable = false, length = 2048)
    private String path;

    @Column(name = "GROUP_ID", nullable = false)
    private String groupId;

    @Column(name = "ARTIFACT_ID", nullable = false)
    private String artifactId;

    @Column(name = "PACKAGING", nullable = false)
    private String packaging;

    @Column(name = "VERSION", nullable = false)
    private String version;

    @ManyToOne
    @JoinColumn(name = "REPO_INFO_ID", nullable = false)
    private RepoInfo repoInfo;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "ADDED_DATE")
    private Date addedDate;

    public ArtifactInfo(RepoInfo repoInfo, String path, String mavenId) {
        String[] mavenIdParts = mavenId.split(":");
        this.path = path;
        this.groupId = mavenIdParts[0];
        this.artifactId = mavenIdParts[1];
        this.packaging = mavenIdParts[2];
        this.version = mavenIdParts[3];
        this.repoInfo = repoInfo;
        this.addedDate = new Date();
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public void setArtifactId(String artifactId) {
        this.artifactId = artifactId;
    }

    public String getPackaging() {
        return packaging;
    }

    public void setPackaging(String packaging) {
        this.packaging = packaging;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public RepoInfo getRepoInfo() {
        return repoInfo;
    }

    public void setRepoInfo(RepoInfo repoInfo) {
        this.repoInfo = repoInfo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ArtifactInfo mavenInfo = (ArtifactInfo) o;

        if (!groupId.equals(mavenInfo.groupId)) return false;
        if (!artifactId.equals(mavenInfo.artifactId)) return false;
        if (!packaging.equals(mavenInfo.packaging)) return false;
        return version.equals(mavenInfo.version);

    }

    @Override
    public int hashCode() {
        int result = groupId.hashCode();
        result = 31 * result + artifactId.hashCode();
        result = 31 * result + packaging.hashCode();
        result = 31 * result + version.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "ArtifactInfo{" +
                "path='" + path + '\'' +
                ", groupId='" + groupId + '\'' +
                ", artifactId='" + artifactId + '\'' +
                ", packaging='" + packaging + '\'' +
                ", version='" + version + '\'' +
                '}';
    }
}
