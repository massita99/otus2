package com.massita.user;

import com.healthmarketscience.sqlbuilder.InsertQuery;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbSchema;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbSpec;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbTable;
import com.massita.executors.Executor;
import com.massita.util.ReflectionUtil;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.IntStream;

import static com.massita.executors.JdbcHelper.loadResultSetIntoObject;

@RequiredArgsConstructor
public class DataSetDaoImpl<T extends DataSet> implements DataSetDao<T> {

    private final Connection connection;

    private static final String SELECT_TABLE = "SELECT * FROM %s WHERE id = ?";

    private final Map<Class<?>, PreparedStatement> saveStatementsCache = new HashMap<>();
    private final Map<Class<?>, PreparedStatement> selectStatementsCache = new HashMap<>();

    @Override
    public void save(T user) {
        final PreparedStatement statement = saveStatementsCache.computeIfAbsent(user.getClass(), this::getPreparedInsertQuery);

        List<Object> fieldsValues = new LinkedList<>();

        // add all object field values to fieldsValues list
        ReflectionUtil.handleAllFields(user.getClass(), field -> {
            try {
                if (field.getName().equals("id")) {
                    fieldsValues.add(null);
                } else{
                    fieldsValues.add(field.get(user));
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Unable to read field: " + field.getName(), e);
            }
        });
        //Set all fieldsValues to PreparedStatement
        IntStream.rangeClosed(1, fieldsValues.size()).forEach(counter -> {
            try {
                int fieldIndex = counter - 1;
                statement.setObject(counter, fieldsValues.get(fieldIndex));
            } catch (SQLException e) {
                throw new RuntimeException("Unable to create sql query for: " + user.toString(), e);
            }
        });

        Executor.updatePrepared(statement);
    }

    @SneakyThrows
    private PreparedStatement getPreparedInsertQuery(Class<?> aClass) {
        DbSpec spec = new DbSpec();
        DbSchema schema = spec.addDefaultSchema();
        DbTable table = schema.addTable(aClass.getSimpleName());
        InsertQuery insertOrderQuery = new InsertQuery(table);
        insertOrderQuery.addCustomPreparedColumns(ReflectionUtil.getAllSerializableFields(aClass)
                .stream()
                .map(Field::getName).toArray());
        return connection.prepareStatement(insertOrderQuery.validate().toString());
    }

    @Override
    @SneakyThrows
    public Optional<T> load(long id, Class<T> clazz) {
        final PreparedStatement statement = selectStatementsCache.computeIfAbsent(clazz, this::getPreparedSelectQuery);
        statement.setLong(1, id);
        return Optional.ofNullable(Executor.queryPreparedForClass(statement, this::extract, clazz));
    }

    @SneakyThrows
    private PreparedStatement getPreparedSelectQuery(Class<?> clazz) {
        return connection.prepareStatement(String.format(SELECT_TABLE, clazz.getSimpleName()));
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

