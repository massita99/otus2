package com.massita.base;

import com.massita.user.DataSet;
import com.massita.user.DataSetDao;
import com.massita.user.DataSetDaoImpl;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

public class DBServiceImpl<T extends DataSet> implements DBService<T> {

    private final Connection connection;

    private final DataSetDao<T> dataSetDao;


    public DBServiceImpl(Connection connection) {
        this.connection = connection;
        this.dataSetDao = new DataSetDaoImpl<>(connection);
    }

    @Override
    public void save(T dataSet) throws SQLException {
        dataSetDao.save(dataSet);
    }

    @Override
    public Optional<T> readForClass(int id, Class<T> clazz) throws SQLException {
        return dataSetDao.load(id, clazz);
    }
}
