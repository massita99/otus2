package com.massita.service.db.dao;

import com.massita.model.DataSet;

public interface DataSetDao<T extends DataSet> {

    /**
     * Save persist object to DB
     * @param user - object that should be stored in DB

     */

    void save(T user);

    /**
     * Load object to POJO from DB
     * @param id - object id in DB
     * @param clazz - object Class
     * @return object of requested class
     */
    T load(long id, Class<T> clazz);

    /**
     * Count number of entity
     * @param clazz - class of entity to count
     * @return cunt
     */
    long count(Class<T> clazz);
}
