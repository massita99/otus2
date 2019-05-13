package com.massita.service.db.dao;

import com.massita.model.UserDataSet;
import com.massita.service.db.DBService;
import com.massita.service.db.DDLService;
import com.massita.service.db.DDLServiceImpl;
import com.massita.service.db.custom.DBServiceImpl;
import com.massita.service.db.util.dbcommon.ConnectionHelper;
import org.junit.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

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
    @Ignore("doesn't support onetomany onetoone")
    public void loadPositive() throws SQLException {
        DataSetDao<UserDataSet> dataSetDao = new DataSetDaoImpl<UserDataSet>(connection);
        UserDataSet user = dataSetDao.load(1, UserDataSet.class);
        Assert.assertEquals(user.getName(), "massita");

    }

    @Test
    @Ignore("doesn't support onetomany onetoone")
    public void loadNegative() throws SQLException {
        DataSetDao<UserDataSet> dataSetDao = new DataSetDaoImpl<UserDataSet>(connection);
        UserDataSet user = dataSetDao.load(2, UserDataSet.class);
        Assert.assertNull(user);

    }

    @Test
    @Ignore("doesn't support onetomany onetoone")
    public void save() throws SQLException {
        DataSetDao<UserDataSet> dataSetDao = new DataSetDaoImpl<UserDataSet>(connection);
        UserDataSet testDataSet = new UserDataSet("tony", 40);
        dataSetDao.save(testDataSet);

        UserDataSet user = dataSetDao.load(2, UserDataSet.class);

        Assert.assertEquals(user, testDataSet);

    }


}