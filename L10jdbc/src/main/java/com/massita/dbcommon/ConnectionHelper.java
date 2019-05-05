package com.massita.dbcommon;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionHelper {

    public static Connection getConnection() {
        try {
            DriverManager.registerDriver(new org.h2.Driver());

            String url = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1";

            return DriverManager.getConnection(url);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
