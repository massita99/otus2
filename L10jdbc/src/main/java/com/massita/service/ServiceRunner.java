package com.massita.service;

import com.massita.model.AddressDataSet;
import com.massita.model.PhoneDataSet;
import com.massita.model.UserDataSet;
import com.massita.service.db.DBService;
import com.massita.service.db.hibernate.DBServiceHibernateImpl;
import com.massita.service.messaging.MessageService;
import com.massita.service.messaging.MessageServiceImpl;
import lombok.SneakyThrows;
import org.hibernate.cfg.Configuration;

public class ServiceRunner {

    @SneakyThrows
    public static DBService<UserDataSet> prepareDbService(MessageServiceImpl messageService) {
       // Connection connection = ConnectionHelper.getConnection();
        Configuration configuration = new Configuration()
                .configure("service/db/hibernate/hibernate.cfg.xml")
                .addAnnotatedClass(UserDataSet.class)
                .addAnnotatedClass(PhoneDataSet.class)
                .addAnnotatedClass(AddressDataSet.class);
        DBService dbService = new DBServiceHibernateImpl<>(configuration);
        ((DBServiceHibernateImpl)dbService).setMessageService(messageService);

/*        DDLService ddlService = new DDLServiceImpl(connection);
        ddlService.prepareTables();*/
        return dbService;
    }

    public static MessageService prepareMessageService() {
        MessageService service = new MessageServiceImpl();
        service.start();
        return service;
    }

}
