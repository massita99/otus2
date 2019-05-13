package com.massita.service.db.dao;

import com.massita.model.DataSet;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;

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
    public T load(long id, Class<T> clazz) {
        T dataSet = session.load(clazz, id);
        return dataSet;
    }

    @Override
    public long count(Class<T> clazz) {
        Criteria crit = session.createCriteria(clazz);
        crit.setProjection(Projections.rowCount());
        return (Long)crit.uniqueResult();
    }
}
