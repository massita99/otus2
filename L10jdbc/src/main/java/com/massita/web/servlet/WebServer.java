package com.massita.web.servlet;

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
import lombok.SneakyThrows;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.hibernate.cfg.Configuration;

import java.sql.Connection;

import static com.massita.service.messaging.message.DbMessage.DB_SERVICE_ADDRESS;

public class WebServer {

    private final static int PORT = 8080;

    public void start() throws Exception {

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);

        Server server = new Server(PORT);

        DBService<UserDataSet> dbService = getDbService();
        MessageService messageService = getMessageService();

        ((DBServiceHibernateImpl)dbService).setMessageService(messageService);
        messageService.subscribe(DB_SERVICE_ADDRESS, (MessageListener) dbService);

        //Add custom servlets
        context.addServlet(new ServletHolder(new UserDataSetServlet(messageService)), "/user");

        context.addServlet(new ServletHolder(new UserDataSetStatsServlet(messageService)), "/stat");

        ResourceHandler resource_handler = new ResourceHandler();
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();

        // Configure the ResourceHandler. Setting the resource base indicates where the files should be served out of.
        resource_handler.setDirectoriesListed(true);
        resource_handler.setWelcomeFiles(new String[]{"main.html"});
        resource_handler.setResourceBase(classLoader.getResource("web").toString());

        // Add the ResourceHandler to the server.
        HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[]{resource_handler, context});
        server.setHandler(handlers);

        // Start things up! By using the server.join() the server thread will join with the current thread.
        server.start();
        server.join();
    }

    @SneakyThrows
    private DBService<UserDataSet> getDbService() {
        Connection connection = ConnectionHelper.getConnection();
        Configuration configuration = new Configuration()
                .configure("service/db/hibernate/hibernate.cfg.xml")
                .addAnnotatedClass(UserDataSet.class)
                .addAnnotatedClass(PhoneDataSet.class)
                .addAnnotatedClass(AddressDataSet.class);
        DBService dbService = new DBServiceHibernateImpl<>(configuration);
        DDLService ddlService = new DDLServiceImpl(connection);
        ddlService.prepareTables();
        return dbService;
    }

    private MessageService getMessageService() {
        MessageService service = new MessageService();
        service.start();
        return service;
    }
}
