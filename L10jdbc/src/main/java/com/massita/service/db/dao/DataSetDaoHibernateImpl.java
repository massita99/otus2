package com.massita.service.db.dao;

import com.massita.model.DataSet;
import org.hibernate.Session;

import java.util.Optional;

public class DataSetDaoHibernateImpl<T extends DataSet> implements DataSetDao<T> {

    private Session session;

    public DataSetDaoHibernateImpl(Session session) {
        this.session = session;
    }

    @Override
    public void save(T user) {
        session.save(user);

    }

    @Override
    public Optional<T> load(long id, Class<T> clazz) {
        return Optional.ofNullable(session.load(clazz, id));
    }
}
