package com.massita.base;

import com.massita.user.DataSet;

import java.util.Optional;

public interface DBService<T extends DataSet> {

    void save(T dataSet);

    Optional<T> readForClass(long id, Class<T> clazz);
}
