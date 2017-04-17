package org.wso2.security.tools.reposcanner.locator;

import java.io.File;
import java.util.List;

/**
 * Created by ayoma on 4/15/17.
 */
public interface BuildConfigLocator {
    public List<File> locate(File sourceFolder);
}
