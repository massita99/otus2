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
import lombok.Data;
import lombok.SneakyThrows;
import org.hibernate.cfg.Configuration;

import java.sql.Connection;

import static com.massita.service.messaging.message.DbMessage.DB_SERVICE_ADDRESS;

public class ServiceRunner {

    public static AppContext context = new AppContext();

    public static void runAllServices() {

        MessageService messageService = prepareMessageService();
        DBService<UserDataSet> dbService = prepareDbService(messageService);
        messageService.subscribe(DB_SERVICE_ADDRESS, (MessageListener) dbService);

        context.setDbService(dbService);
        context.setMessageService(messageService);
    }


    @SneakyThrows
    private static DBService<UserDataSet> prepareDbService(MessageService messageService) {
        Connection connection = ConnectionHelper.getConnection();
        Configuration configuration = new Configuration()
                .configure("service/db/hibernate/hibernate.cfg.xml")
                .addAnnotatedClass(UserDataSet.class)
                .addAnnotatedClass(PhoneDataSet.class)
                .addAnnotatedClass(AddressDataSet.class);
        DBService dbService = new DBServiceHibernateImpl<>(configuration);
        ((DBServiceHibernateImpl)dbService).setMessageService(messageService);

        DDLService ddlService = new DDLServiceImpl(connection);
        ddlService.prepareTables();
        return dbService;
    }

    private static MessageService prepareMessageService() {
        MessageService service = new MessageService();
        service.start();
        return service;
    }

    @Data
    public static class AppContext {
        private MessageService messageService;
        private DBService<UserDataSet> dbService;
    }
}
