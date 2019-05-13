package com.massita.service.db;

import com.massita.model.AddressDataSet;
import com.massita.model.PhoneDataSet;
import com.massita.model.UserDataSet;
import com.massita.service.db.hibernate.DBServiceHibernateImpl;
import com.massita.service.db.util.dbcommon.ConnectionHelper;
import org.hibernate.cfg.Configuration;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;
import java.util.Set;

public class DBServiceHibernateImplTest {

    DBService<UserDataSet> dbService;
    DDLService ddlService;
    Connection connection;

    @Before
    public void init() throws SQLException {
        connection = ConnectionHelper.getConnection();
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        Configuration configuration = new Configuration()
                .configure(new File(classLoader.getResource("service/db/hibernate/hibernate.cfg.xml").getFile()))
                .addAnnotatedClass(UserDataSet.class)
                .addAnnotatedClass(PhoneDataSet.class)
                .addAnnotatedClass(AddressDataSet.class);
        dbService = new DBServiceHibernateImpl<>(configuration);
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
        testUser.setAddress(testAddress);
        dbService.save(testUser);

        Optional<UserDataSet> user = dbService.readForClass(2, UserDataSet.class);

        Assert.assertEquals(user.get().getName(), testUser.getName());
        Assert.assertEquals(user.get().getAddress(), testAddress);

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

    @Test
    public void count() {
        Assert.assertEquals(dbService.count(UserDataSet.class), 1);
        //add one user
        UserDataSet testUser = new UserDataSet("tony", 40);
        dbService.save(testUser);
        Assert.assertEquals(dbService.count(UserDataSet.class), 2);
    }
}