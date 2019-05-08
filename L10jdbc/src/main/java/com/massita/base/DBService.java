package com.massita.base;

import com.massita.user.DataSet;

import java.sql.SQLException;
import java.util.Optional;

public interface DBService<T extends DataSet> {

    void save(T dataSet) throws SQLException;

    Optional<T> readForClass(int id, Class<T> clazz) throws SQLException;
}
