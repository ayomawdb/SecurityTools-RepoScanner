package org.wso2.security.tools.reposcanner.artifact;

import org.apache.log4j.Logger;
import org.apache.maven.shared.invoker.*;
import org.wso2.security.tools.reposcanner.AppConfig;
import org.wso2.security.tools.reposcanner.maven.MavenIdInvocationOutputHandler;
import org.wso2.security.tools.reposcanner.pojo.RepoArtifact;
import org.wso2.security.tools.reposcanner.pojo.Repo;

import java.io.File;
import java.util.Arrays;
import java.util.Properties;

/**
 * Created by ayoma on 4/14/17.
 */
public class MavenArtifactInfoGenerator implements ArtifactInfoGenerator {
    private static Logger log = Logger.getLogger(MavenArtifactInfoGenerator.class.getName());

    public RepoArtifact getArtifactInfo(String consoleTag, Repo repo, File baseFolder, File configFile) throws MavenInvocationException {
        String path = configFile.getAbsolutePath().substring(baseFolder.getAbsolutePath().length(), configFile.getAbsolutePath().length());

        log.info(consoleTag + "Calling MavenID and FinalName identification process for path: " + path);

        String id = getSignature(consoleTag, configFile);
        String finalName = getFinalName(consoleTag, configFile);
        if(id != null && id.split(":").length == 4) {
            log.info(consoleTag + "MavenID for path \"" + path + "\" is " + id);
            log.info(consoleTag + "FinalName for path \"" + path + "\" is " + finalName);

            path = path.substring(path.indexOf(File.separator,1), path.length());
            path = path.replace("pom.xml", "");

            RepoArtifact mavenInfo = new RepoArtifact(repo, path, id, finalName);
            return mavenInfo;
        } else {
            throw new IllegalArgumentException("Invalid or incomplete MavenID (" + id + ") for path: " + path);
        }
    }

    private String getSignature(String consoleTag, File baseFile) throws MavenInvocationException {
        Properties props = System.getProperties();

        props.setProperty("maven.home", AppConfig.getMavenHome());

        MavenIdInvocationOutputHandler handler = new MavenIdInvocationOutputHandler(consoleTag);
        InvocationRequest request = new DefaultInvocationRequest();
        request.setOutputHandler(handler);
        request.setPomFile( baseFile );
        request.setGoals( Arrays.asList( "org.apache.maven.plugins:maven-help-plugin:evaluate", "-Dexpression=project.id") );

        Invoker invoker = new DefaultInvoker();
        invoker.execute( request );

        return handler.getMavenId();
    }

    private String getFinalName(String consoleTag, File baseFile) throws MavenInvocationException {
        Properties props = System.getProperties();

        props.setProperty("maven.home", AppConfig.getMavenHome());

        MavenIdInvocationOutputHandler handler = new MavenIdInvocationOutputHandler(consoleTag);
        InvocationRequest request = new DefaultInvocationRequest();
        request.setOutputHandler(handler);
        request.setPomFile( baseFile );
        request.setGoals( Arrays.asList( "org.apache.maven.plugins:maven-help-plugin:evaluate", "-Dexpression=project.build.finalName") );

        Invoker invoker = new DefaultInvoker();
        invoker.execute( request );

        return handler.getMavenId();
    }
}
