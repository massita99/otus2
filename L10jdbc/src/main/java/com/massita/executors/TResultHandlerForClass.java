package com.massita.executors;

import java.sql.ResultSet;
import java.sql.SQLException;

@FunctionalInterface
public interface TResultHandlerForClass<T> {
    T handle(ResultSet resultSet, Class<T> clazz) throws SQLException;
}
