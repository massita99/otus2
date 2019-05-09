package com.massita.service.db.dao;

import com.massita.model.DataSet;

import java.sql.SQLException;
import java.util.Optional;

public interface DataSetDao<T extends DataSet> {

    /**
     * Save persist object to DB
     * @param user - object that should be stored in DB
     * @throws SQLException
     */

    void save(T user);

    /**
     * Load object to POJO from DB
     * @param id - object id in DB
     * @param clazz - object Class
     * @return object of requested class
     * @throws SQLException
     */
    Optional<T> load(long id, Class<T> clazz);
}
