package org.wso2.security.tools.reposcanner;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import org.wso2.security.tools.reposcanner.scanner.GitRepoScanner;
import org.wso2.security.tools.reposcanner.scanner.RepoScanner;
import org.wso2.security.tools.reposcanner.storage.JDBCStorage;
import org.wso2.security.tools.reposcanner.storage.Storage;

/**
 * Created by ayoma on 4/14/17.
 */
public class Main {
    @Parameter(names = {"-git.oauth2"}, description = "OAuth 2 token used to access GitHub", password = true, order = 1, descriptionKey = "ABC")
    private String gitOAuth2Token;

    @Parameter(names = {"-git.users"}, description = "Comma separated list of GitHub user accounts to scan", order = 2, descriptionKey = "ABC")
    private String gitUserAccounts;

    @Parameter(names = {"-maven.home"}, description = "Maven home (if environment variables are not set)", order = 3)
    private String mavenHome;

    @Parameter(names = {"-storage"}, description = "Storage used in storing final results (Options: JDBC) (Default: JDBC)", order = 4)
    private String storageType;

    @Parameter(names = {"-jdbc.driver"}, description = "Database driver class (Default: com.mysql.jdbc.Driver)", order = 5)
    private String databaseDriver;

    @Parameter(names = {"-jdbc.url"}, description = "Database connection URL (Default: jdbc:mysql://localhost/RepoScanner)", order = 6)
    private String databaseUrl;

    @Parameter(names = {"-jdbc.username"}, description = "Database username (Default: root)", order = 7)
    private String databaseUsername;

    @Parameter(names = {"-jdbc.password"}, description = "Database password", password = true, order = 8)
    private String databasePassword;

    @Parameter(names = {"-jdbc.dialect"}, description = "Database Hibernate dialect (Default: org.hibernate.dialect.MySQLDialect)", order = 9)
    private String databaseHibernateDialect;

    @Parameter(names = {"-verbose", "-v"}, description = "Verbose output", order = 10)
    private boolean verbose;

    @Parameter(names = {"-debug", "-d"}, description = "Verbose + Debug output for debugging requirements", order = 11)
    private boolean debug;

    @Parameter(names = {"--help", "-help", "-?"}, help = true, order = 12)
    private boolean help;

    @Parameter(names = {"-jdbc.create"}, description = "Drop and create JDBC tables", order = 13)
    private boolean databaseCreate;

    @Parameter(names = {"-threads.tag"}, description = "Thread count used to fetch tag information for each repository (Default: 20)", order = 14)
    private int tagWorkerThreadCount;

    @Parameter(names = {"-threads.repo"}, description = "Thread count doing repository scanning (Example: scan each tag of each repository) (Default: 1)", order = 15)
    private int repoWorkerThreadCount;

    @Parameter(names = {"-threads.artifact"}, description = "Thread count doing artifact level scanning (Example: scan downloaded repository for build information) (Default: 1)", order = 16)
    private int artifactWorkerThreadCount;

    public static void main(String[] args) throws Exception {
        Main main = new Main();

        ConsoleUtil.println("-------------------------------------------------");
        ConsoleUtil.println("-----                                       -----");
        ConsoleUtil.println("-----          Repository Scanner           -----");
        ConsoleUtil.println("-----                                       -----");
        ConsoleUtil.println("-------------------------------------------------");
        JCommander jCommander = new JCommander(main, args);
        jCommander.setProgramName("WSO2 Repo Scanner");

        //JCommander.newBuilder().addObject(main).build().parse(args);

        if(main.databaseDriver == null || main.databaseDriver.length() == 0) {
            main.databaseDriver = "com.mysql.jdbc.Driver";
        }
        if(main.databaseUrl == null || main.databaseUrl.length() == 0) {
            main.databaseUrl = "jdbc:mysql://localhost/RepoScanner";
        }
        if(main.databaseUsername == null || main.databaseUsername.length() == 0) {
            main.databaseUsername = "root";
        }
        if(main.databaseHibernateDialect == null || main.databaseHibernateDialect.length() == 0) {
            main.databaseHibernateDialect = "org.hibernate.dialect.MySQLDialect";
        }
        if(main.storageType == null || main.storageType.length() == 0) {
            main.storageType = "JDBC";
        }

        if(main.help) {
            jCommander.usage();
            return;
        }

        main.start(jCommander);
    }

    public void start(JCommander jCommander) {
        AppConfig.addMavenSkipPatterns("[");
        AppConfig.addMavenSkipPatterns("Download");

        if(gitUserAccounts != null) {
            for (String user : gitUserAccounts.split(",")) {
                AppConfig.addGithubAccountsToScan(user);
            }
        }

        AppConfig.setMavenHome(mavenHome);
        AppConfig.setVerbose(verbose);
        if(debug) {
            AppConfig.setVerbose(true);
            AppConfig.setDebug(true);
        }
        AppConfig.setCreateDB(databaseCreate);

        Storage storage = null;
        if(storageType == null || storageType.trim().length() == 0 || storageType.equals("JDBC")) {
            if(databaseDriver == null || databaseUrl == null || databaseUsername ==null || databasePassword == null || databaseHibernateDialect == null) {
                ConsoleUtil.printInRed("JDBC parameters are not properly set (All -jdbc parameters are required). Terminating...");
                jCommander.usage();
                return;
            }
            storage = new JDBCStorage(databaseDriver, databaseUrl, databaseUsername, databasePassword.toCharArray(), databaseHibernateDialect);
        } else {
            ConsoleUtil.printInRed("No valid storage option selected. Terminating...");
            jCommander.usage();
            return;
        }

        if(gitOAuth2Token != null) {
            RepoScanner scanner = new GitRepoScanner(gitOAuth2Token.toCharArray());
            try {
                scanner.scan(storage);
                ConsoleUtil.printInGreen("Scanning complete. Terminating...");
            } catch (Exception e) {
                ConsoleUtil.printInRed("Exception occured during scanning process. Terminating...");
                e.printStackTrace();
                storage.close();
            }
        } else {
            ConsoleUtil.printInRed("No scanning parameters are set. Please use \"-git.oauth2\" parameter to set OAuth credentials to access GitHub account. Terminating...");
            jCommander.usage();
            storage.close();
        }
    }
}