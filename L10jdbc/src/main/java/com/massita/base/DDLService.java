package com.massita.base;

import java.sql.SQLException;

public interface DDLService {

    String getMetaData() throws SQLException;

    void prepareTables() throws SQLException;

    void deleteTables() throws SQLException;

}
