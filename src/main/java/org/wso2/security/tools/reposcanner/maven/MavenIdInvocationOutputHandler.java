package org.wso2.security.tools.reposcanner.maven;

import org.apache.log4j.Logger;
import org.apache.maven.shared.invoker.InvocationOutputHandler;
import org.wso2.security.tools.reposcanner.AppConfig;

import java.util.List;

/**
 * Created by ayoma on 4/15/17.
 */
public class MavenIdInvocationOutputHandler implements InvocationOutputHandler {
    private static Logger log = Logger.getLogger(MavenIdInvocationOutputHandler.class.getName());

    private String mavenId;
    private String consoleTag;

    public MavenIdInvocationOutputHandler(String consoleTag) {
        this.consoleTag = consoleTag;
    }

    public void consumeLine(String s) {
        if(AppConfig.isDebug()) {
            log.debug(consoleTag + "[MavenInvocation] " + s);
        }

        List<String> mavenOutputSkipPatterns = AppConfig.getMavenOutputSkipPatterns();
        boolean isConsumable = true;
        for(String  mavenOutputSkipPattern : mavenOutputSkipPatterns) {
            if(s.startsWith(mavenOutputSkipPattern)) {
                isConsumable = false;
            }
        }
        if(isConsumable && !(s.contains("null object") || s.contains("invalid expression"))) {
            mavenId = s;
        }
    }

    public String getMavenId() {
        return mavenId;
    }

    public void setMavenId(String mavenId) {
        this.mavenId = mavenId;
    }
}
