package com.massita.service.db.executors;

import java.sql.*;

public class Executor {

    public static <T> T queryPreparedForClass(PreparedStatement statement, TResultHandlerForClass<T> handler, Class<T> clazz) {
        try (statement) {
            final ResultSet resultSet = statement.executeQuery();
            return handler.handle(resultSet, clazz);
        } catch (SQLException ex) {
            throw new RuntimeException("Unable to create sql query for: " + clazz.getSimpleName(), ex);
        }
    }

    public static void update(Connection connection, String update) {
        try (final Statement statement = connection.createStatement()) {
            statement.executeUpdate(update);
        } catch (SQLException ex) {
            throw new RuntimeException("Unable to process sql query for: " + update, ex);
        }
    }

    public static void updatePrepared(PreparedStatement statement) {
        try (statement) {
            statement.executeUpdate();
        } catch (SQLException ex) {
            throw new RuntimeException("Unable to process sql query", ex);
        }
    }
}
