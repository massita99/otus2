package com.massita.user;

import com.healthmarketscience.sqlbuilder.InsertQuery;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbSchema;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbSpec;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbTable;
import com.massita.executors.Executor;
import com.massita.util.ReflectionUtil;
import lombok.RequiredArgsConstructor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

import static com.massita.executors.ResultSetHelper.loadResultSetIntoObject;

@RequiredArgsConstructor
public class DataSetDaoImpl<T extends DataSet> implements DataSetDao<T> {

    private final Connection connection;

    private static final String SELECT_TABLE = "SELECT * FROM %s WHERE id = ?";

    @Override
    public void save(T user) throws SQLException {

        Executor.update(connection, getInsertQuery(user));

    }

    @Override
    public Optional<T> load(int id, Class<T> clazz) throws SQLException {
        final PreparedStatement statement = connection.prepareStatement(String.format(SELECT_TABLE, clazz.getSimpleName()));
        statement.setInt(1, id);
        return Optional.ofNullable(Executor.queryPreparedForClass(statement, this::extract, clazz));

    }


    private String getInsertQuery(T user) {

        DbSpec spec = new DbSpec();
        DbSchema schema = spec.addDefaultSchema();
        DbTable table = schema.addTable(user.getClass().getSimpleName());
        InsertQuery insertOrderQuery = new InsertQuery(table);

        ReflectionUtil.handleAllFields(user.getClass(), f -> {
            try {
                //Add columns to query
                insertOrderQuery.addColumn(table.addColumn(f.getName()), f.get(user));
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Unable to read field: " + f.getName(), e);
            }
        });

        return insertOrderQuery.validate().toString();
    }

    private T extract(ResultSet resultSet, Class<T> clazz) throws SQLException {

        //Query return nothing:
        if (!resultSet.next()) {
            return null;
        }

        try {
            T t = clazz.newInstance();
            loadResultSetIntoObject(resultSet, t);
            return t;

        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException("Unable to create record: " + e.getMessage(), e);
        }
    }

}

