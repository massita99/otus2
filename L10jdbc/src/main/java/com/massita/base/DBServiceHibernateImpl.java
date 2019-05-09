package com.massita.base;

import com.massita.user.DataSet;
import com.massita.user.DataSetDao;
import com.massita.user.DataSetDaoHibernateImpl;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class DBServiceHibernateImpl<T extends DataSet> implements DBService<T> {

    private final SessionFactory sessionFactory;

    public DBServiceHibernateImpl(List<Class<?>> annotatedClasses) {
        Configuration configuration = new Configuration();

        annotatedClasses.forEach(configuration::addAnnotatedClass);

        configuration.setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
        configuration.setProperty("hibernate.connection.driver_class", "org.h2.Driver");
        configuration.setProperty("hibernate.connection.url", "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1");
        configuration.setProperty("hibernate.show_sql", "true");
        configuration.setProperty("hibernate.hbm2ddl.auto", "update");
        configuration.setProperty("hibernate.enable_lazy_load_no_trans", "true");

        sessionFactory = createSessionFactory(configuration);
    }

    private static SessionFactory createSessionFactory(Configuration configuration) {
        StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder();
        builder.applySettings(configuration.getProperties());
        ServiceRegistry serviceRegistry = builder.build();
        return configuration.buildSessionFactory(serviceRegistry);
    }


    @Override
    public void save(T dataSet) {
        runInSession(session -> {
            DataSetDao<T> dao = new DataSetDaoHibernateImpl<>(session);
            dao.save(dataSet);
            return null;
        });
    }

    @Override
    public Optional<T> readForClass(long id, Class<T> clazz) {
        return runInSession(session -> {
            DataSetDao<T> dao = new DataSetDaoHibernateImpl<>(session);
            Optional<T> object = dao.load(id, clazz);
            Hibernate.initialize(object);
            return object;
        });
    }

    private <R> R runInSession(Function<Session, R> function) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            R result = function.apply(session);
            transaction.commit();
            return result;
        }
    }
}
