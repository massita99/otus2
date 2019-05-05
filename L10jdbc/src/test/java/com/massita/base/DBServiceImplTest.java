package com.massita.base;

import com.massita.base.DBService;
import com.massita.base.DBServiceImpl;
import com.massita.dbcommon.ConnectionHelper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;

public class DBServiceImplTest {

    DBService dbService;
    Connection connection;

    @Before
    public void init() {
        connection = ConnectionHelper.getConnection();
        dbService = new DBServiceImpl(connection);
    }

    @Test
    public void getMetaData() throws SQLException {

        //Just to be sure that dbService works
        Assert.assertTrue(dbService.getMetaData().contains("H2"));
    }
}