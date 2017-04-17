package org.wso2.security.tools.reposcanner.artifact;

import org.apache.maven.shared.invoker.*;
import org.wso2.security.tools.reposcanner.AppConfig;
import org.wso2.security.tools.reposcanner.ConsoleUtil;
import org.wso2.security.tools.reposcanner.maven.MavenIdInvocationOutputHandler;
import org.wso2.security.tools.reposcanner.pojo.ArtifactInfo;
import org.wso2.security.tools.reposcanner.pojo.RepoInfo;

import java.io.Console;
import java.io.File;
import java.util.Arrays;
import java.util.Map;
import java.util.Properties;

/**
 * Created by ayoma on 4/14/17.
 */
public class MavenArtifactInfoGenerator implements ArtifactInfoGenerator {

    public ArtifactInfo getArtifactInfo(String consoleTag, RepoInfo repoInfo, File baseFolder, File configFile) throws MavenInvocationException {
        String id = getSignature(consoleTag, configFile);
        if(id != null && id.split(":").length == 4) {
            String path = configFile.getAbsolutePath().substring(baseFolder.getAbsolutePath().length(), configFile.getAbsolutePath().length());
            String identifier = path.split(File.separator)[1];

            ConsoleUtil.println(consoleTag + "MavenID for path \"" + path + "\" is " + id);

            path = path.substring(path.indexOf(File.separator,1), path.length());
            path = path.replace("pom.xml", "");

            ArtifactInfo mavenInfo = new ArtifactInfo(repoInfo, path, id);
            return mavenInfo;
        } else {
            throw new IllegalArgumentException("Invalid or incomplete MavenID: " + id);
        }
    }

    private String getSignature(String consoleTag, File baseFile) throws MavenInvocationException {
        Properties props = System.getProperties();
        if(AppConfig.getMavenHome() != null) {
            props.setProperty("maven.home", AppConfig.getMavenHome());
        }

        MavenIdInvocationOutputHandler handler = new MavenIdInvocationOutputHandler(consoleTag);
        InvocationRequest request = new DefaultInvocationRequest();
        request.setOutputHandler(handler);
        request.setPomFile( baseFile );
        request.setGoals( Arrays.asList( "org.apache.maven.plugins:maven-help-plugin:evaluate", "-Dexpression=project.id") );

        Invoker invoker = new DefaultInvoker();
        invoker.execute( request );

        return handler.getMavenId();
    }
}
