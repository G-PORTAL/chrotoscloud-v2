package net.chrotos.chrotoscloud.persistence.mysql;

import net.chrotos.chrotoscloud.CloudConfig;
import net.chrotos.chrotoscloud.persistence.DataSelectFilter;
import net.chrotos.chrotoscloud.persistence.PersistenceAdapter;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.Collection;
import java.util.Collections;

public class MysqlPersistenceAdapter implements PersistenceAdapter {
    private SessionFactory sessionFactory;
    private EntityManager entityManager;

    @Override
    public boolean isConnected() {
        return sessionFactory != null;
    }

    @Override
    public boolean configure(CloudConfig config) {
        try {
            Configuration dbConfig = new Configuration()
                    .configure("classpath:net/chrotos/chrotoscloud/persistence/mysql/hibernate.cfg.xml")
                    .setProperty("hibernate.connection.url", config.getPersistenceConnectionString())
                    .setProperty("hibernate.connection.username", config.getPersistenceUser())
                    .setProperty("hibernate.connection.password", config.getPersistencePassword());


            sessionFactory = dbConfig.buildSessionFactory();
            entityManager = sessionFactory.createEntityManager();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    @Override
    public <E> Collection<E> getAll(Class<E> clazz) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<E> cq = cb.createQuery(clazz);
        Root<E> rootEntry = cq.from(clazz);
        CriteriaQuery<E> all = cq.select(rootEntry);
        TypedQuery<E> allQuery = entityManager.createQuery(all);

        // TODO apply order

        return allQuery.getResultList();
    }

    @Override
    public <E> Collection<E> getAll(Class<E> clazz, DataSelectFilter filter) {
        return Collections.emptyList(); // TODO
    }

    @Override
    public <E> E getOne(Class<E> clazz, DataSelectFilter filter) {
        if (filter.getPrimaryKeyValue() == null) {
            throw new IllegalArgumentException("A primary Key Value needs to be defined");
        }

        return entityManager.find(clazz, filter.getPrimaryKeyValue());
    }
}
