package org.wso2.security.tools.reposcanner.storage;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.wso2.security.tools.reposcanner.AppConfig;
import org.wso2.security.tools.reposcanner.ConsoleUtil;
import org.wso2.security.tools.reposcanner.pojo.ArtifactInfo;
import org.wso2.security.tools.reposcanner.pojo.ErrorInfo;
import org.wso2.security.tools.reposcanner.pojo.RepoInfo;

import java.util.List;
import java.util.Properties;

/**
 * Created by ayoma on 4/13/17.
 */
public class JDBCStorage implements Storage {
    private static SessionFactory sessionFactory;

    public JDBCStorage(String driverName, String connectionUri, String username, char[] password, String hibernateDialect) {
        try{
            StandardServiceRegistryBuilder registryBuilder =  new StandardServiceRegistryBuilder();

            Properties properties = new Properties();
            properties.put("hibernate.connection.driver_class", driverName);
            properties.put("hibernate.connection.url", connectionUri);
            properties.put("hibernate.connection.username", username);
            properties.put("hibernate.connection.password", new String(password));
            properties.put("hibernate.dialect", hibernateDialect);
            if(AppConfig.isCreateDB()) {
                properties.put("hibernate.hbm2ddl.auto", "create");
            }
            if(AppConfig.isDebug()) {
                properties.put("hibernate.show_sql", "true");
                properties.put("hibernate.format_sql", "true");
            }

            Configuration configuration = new Configuration();
            configuration.addProperties(properties);

            configuration.addAnnotatedClass(RepoInfo.class);
            configuration.addAnnotatedClass(ArtifactInfo.class);
            configuration.addAnnotatedClass(ErrorInfo.class);

            sessionFactory = configuration.buildSessionFactory();

            for(int i = 0; i < password.length; i++) {
                password[i] = ' ';
            }
        }catch (Throwable ex) {
            System.err.println("Failed to create sessionFactory object." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public synchronized boolean isPresent(RepoInfo repoInfo) throws Exception {
        return !getRepoInfoList(repoInfo.getRepositoryName(), repoInfo.getTagName()).isEmpty();
    }

    public synchronized List<RepoInfo> getRepoInfoList(String repositoryName, String tagName) throws Exception {
        Session session = sessionFactory.openSession();
        String hql = "FROM org.wso2.security.tools.reposcanner.pojo.RepoInfo R WHERE R.repositoryName = :repositoryName AND R.tagName = :tagName";
        Query query = session.createQuery(hql);
        query.setParameter("repositoryName", repositoryName);
        query.setParameter("tagName", tagName);
        List results = query.list();
        if(results.size() > 1) {
            ConsoleUtil.printInYellow("[Unexpected] Unexpected condition. Repo Name "+ repositoryName + ", Tag " + tagName +" found multiple times");
        }
        session.close();
        return results;
    }

    public synchronized boolean persist(ArtifactInfo artifactInfo) throws Exception {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();

        try {
            List<RepoInfo> repoInfoList = getRepoInfoList(artifactInfo.getRepoInfo().getRepositoryName(), artifactInfo.getRepoInfo().getTagName());
            if (repoInfoList.isEmpty()) {
                session.save(artifactInfo.getRepoInfo());
            } else {
                artifactInfo.setRepoInfo(repoInfoList.get(0));
            }
            session.save(artifactInfo);
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        } finally {
            session.close();
        }
        return true;
    }

    @Override
    public synchronized boolean persistError(ErrorInfo errorInfo) throws Exception {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();

        try {
            List<RepoInfo> repoInfoList = getRepoInfoList(errorInfo.getRepoInfo().getRepositoryName(), errorInfo.getRepoInfo().getTagName());
            if (repoInfoList.isEmpty()) {
                session.save(errorInfo.getRepoInfo());
            } else {
                errorInfo.setRepoInfo(repoInfoList.get(0));
            }
            session.save(errorInfo);
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        } finally {
            session.close();
        }

        return true;
    }

    public void close() {
        sessionFactory.close();
    }
}
