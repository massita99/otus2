package com.massita.service.db;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.StringJoiner;

public class DDLServiceImpl implements DDLService {

    private static final String CREATE_TABLE_USER = "CREATE TABLE IF NOT EXISTS userdataset (\n" +
            "  id        BIGSERIAL NOT NULL PRIMARY KEY,\n" +
            "  name VARCHAR(255),\n" +
            "  age       INTEGER\n" +
            ");";

    private static final String CREATE_TABLE_PHONE = "CREATE TABLE IF NOT EXISTS phonedataset (\n" +
            "  id        BIGSERIAL NOT NULL PRIMARY KEY,\n" +
            "  number VARCHAR(255)\n" +
            ");";

    private static final String CREATE_TABLE_ADDRESS = "CREATE TABLE IF NOT EXISTS addressdataset (\n" +
            "  id        BIGSERIAL NOT NULL PRIMARY KEY,\n" +
            "  street VARCHAR(255)\n" +
            ");";

    private static final String DROP_TABLE_USER = "DROP TABLE IF EXISTS userdataset;";

    private static final String DROP_TABLE_PHONE = "DROP TABLE IF EXISTS phonedataset;";

    private static final String DROP_TABLE_ADDRESS = "DROP TABLE IF EXISTS addressdataset;";

    private final Connection connection;

    public DDLServiceImpl(Connection connection) {
        this.connection = connection;
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
            statement.executeUpdate(CREATE_TABLE_PHONE);
            statement.executeUpdate(CREATE_TABLE_ADDRESS);
        }
    }

    @Override
    public void deleteTables() throws SQLException {
        try (final Statement statement = connection.createStatement()) {
            statement.executeUpdate(DROP_TABLE_USER);
            statement.executeUpdate(DROP_TABLE_PHONE);
            statement.executeUpdate(DROP_TABLE_ADDRESS);
        }
    }
}
