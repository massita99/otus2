package com.massita.executors;

import com.massita.util.ReflectionUtil;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ResultSetHelper {

    public static void loadResultSetIntoObject(ResultSet resultSet, Object object) {

        ReflectionUtil.handleAllFields(object.getClass(), f -> {

            //Save column to object
            String fieldName = f.getName();
            try {
                f.set(object, resultSet.getObject(fieldName));
            } catch (IllegalAccessException | SQLException e) {
                throw new RuntimeException("Unable to save field: " + f.getName(), e);
            }
        });
    }
}
