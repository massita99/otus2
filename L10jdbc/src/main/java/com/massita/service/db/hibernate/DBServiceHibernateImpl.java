package com.massita.service.db.hibernate;

import com.massita.model.DataSet;
import com.massita.service.db.DBService;
import com.massita.service.db.dao.DataSetDao;
import com.massita.service.db.dao.DataSetDaoHibernateImpl;
import com.massita.service.messaging.MessageListener;
import com.massita.service.messaging.MessageService;
import com.massita.service.messaging.message.DbMessage;
import com.massita.service.messaging.message.Message;
import com.massita.service.messaging.message.ObjectMessage;
import lombok.Setter;
import org.hibernate.*;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.springframework.beans.factory.InitializingBean;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.massita.service.messaging.message.DbMessage.DB_SERVICE_ADDRESS;

public class DBServiceHibernateImpl<T extends DataSet> implements DBService<T>, MessageListener, InitializingBean {

    private final static Logger logger = Logger.getLogger(DBServiceHibernateImpl.class.getName());

    private final SessionFactory sessionFactory;

    @Setter
    private MessageService messageService;

    public DBServiceHibernateImpl(Configuration configuration) {

        sessionFactory = createSessionFactory(configuration);
    }

    public DBServiceHibernateImpl(List<Class<?>> classes, MessageService messageService) {
        Configuration configuration = new Configuration()
                .configure("service/db/hibernate/hibernate.cfg.xml");
        classes.forEach(configuration::addAnnotatedClass);
        sessionFactory = createSessionFactory(configuration);
        this.messageService = messageService;
        System.out.println("DbService start");
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
    @Transactional
    public Optional<T> readForClass(long id, Class<T> clazz) {
        return runInSession(session -> {
            DataSetDao<T> dao = new DataSetDaoHibernateImpl<>(session);
            T object = dao.load(id, clazz);
            try {
                Hibernate.initialize(object);
            } catch (ObjectNotFoundException e) {
                return Optional.empty();
            }
            return Optional.ofNullable(object);
        });
    }

    @Override
    public long count(Class<T> clazz) {
        return runInSession(session -> {
            DataSetDao<T> dao = new DataSetDaoHibernateImpl<>(session);
            return dao.count(clazz);
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

    @Override
    @SuppressWarnings("unchecked")
    public void onMessage(Message message) {
        if (!message.getClass().isAssignableFrom(DbMessage.class)) {
            logger.log(Level.WARNING, "DbService can't handle message with type " + message.getClass());
        }
        DbMessage dbMessage = (DbMessage) message;
        switch (dbMessage.getMessageType()) {
            case SAVE:
                this.save((T)dbMessage.getBody());
                break;
            case LOAD:
                Optional<T> result = this.readForClass((Long) dbMessage.getBody(), (Class<T>) dbMessage.getObjectType());
                if (result.isPresent()) {
                //Send return message back to sender
                    messageService.sendMessage(new ObjectMessage(message.getTo(), message.getFrom(), initializeAndUnproxy(result.get())));
                } else {
                    messageService.sendMessage(new ObjectMessage(message.getTo(), message.getFrom(), null));
                }

                break;
            case COUNT:
                long count = this.count((Class<T>) dbMessage.getObjectType());
                //Send return message back to sender
                messageService.sendMessage(new ObjectMessage(message.getTo(), message.getFrom(), count));
                break;
            default:
                logger.log(Level.WARNING, "Unknown dbMessage type: " + dbMessage.getMessageType());
        }
    }

    private  <T extends DataSet> T initializeAndUnproxy(T entity) {
        if (entity == null) {
            throw new
                    NullPointerException("Entity passed for initialization is null");
        }
        return entity.makeClone();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        messageService.subscribe(DB_SERVICE_ADDRESS, this);
    }
}
