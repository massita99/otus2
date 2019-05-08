package com.massita.user;

import com.massita.base.DBService;
import com.massita.base.DBServiceImpl;
import com.massita.base.DDLService;
import com.massita.base.DDLServiceImpl;
import com.massita.dbcommon.ConnectionHelper;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

public class DataSetDaoImplTest {

    DBService dbService;
    DDLService ddlService;
    Connection connection;

    @Before
    public void init() throws SQLException {
        connection = ConnectionHelper.getConnection();
        dbService = new DBServiceImpl(connection);
        ddlService = new DDLServiceImpl(connection);
        ddlService.prepareTables();
        try (final Statement statement = connection.createStatement()) {
            statement.execute("INSERT INTO userdataset (name, age) VALUES ('massita', 30 );");
        }

    }

    @After
    public void afterTest() throws SQLException {
        ddlService.deleteTables();
    }

    @Test
    public void loadPositive() throws SQLException {
        DataSetDao<UserDataSet> dataSetDao = new DataSetDaoImpl<UserDataSet>(connection);
        Optional<UserDataSet> user = dataSetDao.load(1, UserDataSet.class);
        Assert.assertEquals(user.get().getName(), "massita");

    }

    @Test
    public void loadNegative() throws SQLException {
        DataSetDao<UserDataSet> dataSetDao = new DataSetDaoImpl<UserDataSet>(connection);
        Optional<UserDataSet> user = dataSetDao.load(2, UserDataSet.class);
        Assert.assertTrue(user.isEmpty());

    }

    @Test
    public void save() throws SQLException {
        DataSetDao<UserDataSet> dataSetDao = new DataSetDaoImpl<UserDataSet>(connection);
        UserDataSet testDataSet = new UserDataSet("tony", 40);
        dataSetDao.save(testDataSet);

        Optional<UserDataSet> user = dataSetDao.load(2, UserDataSet.class);

        Assert.assertEquals(user.get(), testDataSet);

    }


}