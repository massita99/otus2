package com.massita.base;

import com.massita.user.DataSet;
import com.massita.user.DataSetDao;
import com.massita.user.DataSetDaoImpl;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;
import java.util.StringJoiner;

public class DBServiceImpl<T extends DataSet> implements DBService<T>, DDLService {
    private static final String CREATE_TABLE_USER = "CREATE TABLE IF NOT EXISTS userdataset (\n" +
            "  id        BIGSERIAL NOT NULL PRIMARY KEY,\n" +
            "  name VARCHAR(255),\n" +
            "  age       INTEGER\n" +
            ");";
    private static final String DROP_TABLE_USER = "DROP TABLE IF EXISTS userdataset;";

    private final Connection connection;

    private final DataSetDao<T> dataSetDao;


    public DBServiceImpl(Connection connection) {
        this.connection = connection;
        this.dataSetDao = new DataSetDaoImpl<>(connection);
    }

    @Override
    public String getMetaData() throws SQLException {
        final StringJoiner joiner = new StringJoiner("\n");
        joiner.add("Autocommit: " + connection.getAutoCommit());
        final DatabaseMetaData metaData = connection.getMetaData();
        joiner.add("DB name: " + metaData.getDatabaseProductName());
        joiner.add("DB version: " + metaData.getDatabaseProductVersion());
        joiner.add("Driver name: " + metaData.getDriverName());
        joiner.add("Driver version: " + metaData.getDriverVersion());
        joiner.add("JDBC version: " + metaData.getJDBCMajorVersion() + '.' + metaData.getJDBCMinorVersion());
        return joiner.toString();
    }

    @Override
    public void prepareTables() throws SQLException {
        try (final Statement statement = connection.createStatement()) {
            statement.executeUpdate(CREATE_TABLE_USER);
        }
    }

    @Override
    public void deleteTables() throws SQLException {
        try (final Statement statement = connection.createStatement()) {
            statement.executeUpdate(DROP_TABLE_USER);
        }
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
