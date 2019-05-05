package com.massita.executors;

import java.sql.*;

public class Executor {

    public static <T> T queryPreparedForClass(PreparedStatement statement, TResultHandlerForClass<T> handler, Class<T> clazz) throws SQLException {
        try (statement) {
            final ResultSet resultSet = statement.executeQuery();
            return handler.handle(resultSet, clazz);
        }
    }

    public static void update(Connection connection, String update) throws SQLException {
        try (final Statement statement = connection.createStatement()) {
            statement.executeUpdate(update);
        }
    }
}
