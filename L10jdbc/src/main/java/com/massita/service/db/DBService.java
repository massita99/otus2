package com.massita.service.db;

import com.massita.model.DataSet;

import java.util.Optional;

public interface DBService<T extends DataSet> {

    void save(T dataSet);

    Optional<T> readForClass(long id, Class<T> clazz);
}
