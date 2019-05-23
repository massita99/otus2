package com.massita.service;

import com.massita.model.AddressDataSet;
import com.massita.model.PhoneDataSet;
import com.massita.model.UserDataSet;
import com.massita.service.db.DBService;
import com.massita.service.db.DDLService;
import com.massita.service.db.DDLServiceImpl;
import com.massita.service.db.hibernate.DBServiceHibernateImpl;
import com.massita.service.db.util.dbcommon.ConnectionHelper;
import com.massita.service.messaging.MessageListener;
import com.massita.service.messaging.MessageService;
import com.massita.service.messaging.message.Address;
import com.massita.service.messaging.message.DbMessage;
import com.massita.service.messaging.message.Message;
import com.massita.service.messaging.message.ObjectMessage;
import lombok.SneakyThrows;
import org.hibernate.cfg.Configuration;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DbServiceWithMessageFtsTest {

    DBService<UserDataSet> dbService;
    DDLService ddlService;
    Connection connection;
    MessageService messageService;
    Address dbServiceAddress;

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
        messageService = new MessageService();
        messageService.start();
        dbServiceAddress = new Address("DB");
        //((DBServiceHibernateImpl)dbService).setMessageService(messageService);
        messageService.subscribe(dbServiceAddress, (MessageListener) dbService);

    }

    @After
    public void afterTest() throws SQLException {
        ddlService.deleteTables();
    }

    @Test
    public void loadTest() {
        Address loadAddress = new Address();
        MessageListener loadListener = new MessageListener() {
            @Override
            public void onMessage(Message message) {
                ObjectMessage objectMessage = (ObjectMessage) message;
                Assert.assertEquals("massita", ((UserDataSet)objectMessage.getBody()).getName());
            }
        };

        messageService.subscribe(loadAddress, loadListener);

        Message loadMessage = new DbMessage(loadAddress, dbServiceAddress, DbMessage.DbMessageType.LOAD,
                1l, UserDataSet.class);
        messageService.sendMessage(loadMessage);

    }

    @Test
    public void countTest() {
        Address countAddress = new Address();
        MessageListener countListener = new MessageListener() {
            @Override
            public void onMessage(Message message) {
                ObjectMessage objectMessage = (ObjectMessage) message;
                Assert.assertEquals(1, objectMessage.getBody());
            }
        };

        messageService.subscribe(countAddress, countListener);

        Message countMessage = new DbMessage(countAddress, dbServiceAddress, DbMessage.DbMessageType.COUNT,
                null, UserDataSet.class);
        messageService.sendMessage(countMessage);

    }

    @Test
    @SneakyThrows
    public void saveTest() {
        UserDataSet testDataSet = new UserDataSet("tony", 40);

        Message saveMessage = new DbMessage(null, dbServiceAddress, DbMessage.DbMessageType.SAVE,
                testDataSet, UserDataSet.class);
        messageService.sendMessage(saveMessage);
        //Just to be sure that message delivered
        Thread.sleep(500);
        Assert.assertEquals(2, dbService.count(UserDataSet.class));

    }
}
