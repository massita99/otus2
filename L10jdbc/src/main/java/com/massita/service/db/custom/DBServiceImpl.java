package com.massita.service.db.custom;

import com.massita.model.DataSet;
import com.massita.service.db.DBService;
import com.massita.service.db.dao.DataSetDao;
import com.massita.service.db.dao.DataSetDaoImpl;

import java.sql.Connection;
import java.util.Optional;

public class DBServiceImpl<T extends DataSet> implements DBService<T> {

    private final Connection connection;

    private final DataSetDao<T> dataSetDao;


    public DBServiceImpl(Connection connection) {
        this.connection = connection;
        this.dataSetDao = new DataSetDaoImpl<>(connection);
    }

    @Override
    public void save(T dataSet) {
        dataSetDao.save(dataSet);
    }

    @Override
    public Optional<T> readForClass(long id, Class<T> clazz) {
        return dataSetDao.load(id, clazz);
    }
}
