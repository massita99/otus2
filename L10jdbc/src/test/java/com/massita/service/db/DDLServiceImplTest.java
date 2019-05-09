package com.massita.service.db;

import com.massita.service.db.util.dbcommon.ConnectionHelper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;

public class DDLServiceImplTest {

    DDLService ddlService;
    Connection connection;

    @Before
    public void init() {
        connection = ConnectionHelper.getConnection();
        ddlService = new DDLServiceImpl(connection);
    }

    @Test
    public void getMetaData() throws SQLException {

        //Just to be sure that dbService works
        Assert.assertTrue(ddlService.getMetaData().contains("H2"));
    }
}