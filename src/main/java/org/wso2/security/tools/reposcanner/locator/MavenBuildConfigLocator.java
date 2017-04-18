package org.wso2.security.tools.reposcanner.locator;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by ayoma on 4/15/17.
 */
public class MavenBuildConfigLocator implements BuildConfigLocator{

    public List<File> locate(File sourceFolder) {
        List<File> fileCollection = new ArrayList<File>();

        Collection<File> sourceFiles = FileUtils.listFiles(sourceFolder, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
        for (File file : sourceFiles) {
            if(file.getAbsolutePath().toLowerCase().endsWith(File.separator + "pom.xml")) {
                fileCollection.add(file);
            }
        }
        return fileCollection;
    }
}
