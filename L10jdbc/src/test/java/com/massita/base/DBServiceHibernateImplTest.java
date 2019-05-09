package com.massita.base;

import com.massita.dbcommon.ConnectionHelper;
import com.massita.user.AddressDataSet;
import com.massita.user.PhoneDataSet;
import com.massita.user.UserDataSet;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class DBServiceHibernateImplTest {

    DBService<UserDataSet> dbService;
    DDLService ddlService;
    Connection connection;

    @Before
    public void init() throws SQLException {
        connection = ConnectionHelper.getConnection();
        dbService = new DBServiceHibernateImpl<>(List.of(UserDataSet.class, AddressDataSet.class, PhoneDataSet.class));
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
    public void readPositiveSimple() {
        Optional<UserDataSet> user = dbService.readForClass(1, UserDataSet.class);
        Assert.assertEquals(user.get().getName(), "massita");

    }

    @Test
    public void save() {
        UserDataSet testDataSet = new UserDataSet("tony", 40);
        dbService.save(testDataSet);

        Optional<UserDataSet> user = dbService.readForClass(2, UserDataSet.class);

        Assert.assertEquals(user.get().getName(), testDataSet.getName());

    }

   @Test
    public void saveWithAddress() {

        UserDataSet testUser = new UserDataSet("tony", 40);
        AddressDataSet testAddress = new AddressDataSet("Sretenka");
        testUser.setStreet(testAddress);
        dbService.save(testUser);

        Optional<UserDataSet> user = dbService.readForClass(2, UserDataSet.class);

        Assert.assertEquals(user.get().getName(), testUser.getName());
        Assert.assertEquals(user.get().getStreet(), testAddress);

    }

    @Test
    public void saveWithPhone() {
        UserDataSet testUser = new UserDataSet("tony", 40);
        PhoneDataSet testPhone = new PhoneDataSet("5556677");
        testUser.setPhones(Set.of(testPhone));
        dbService.save(testUser);

        Optional<UserDataSet> user = dbService.readForClass(2, UserDataSet.class);

        Assert.assertEquals(user.get().getName(), testUser.getName());
        Assert.assertEquals(user.get().getPhones().size(), 1);

    }
}