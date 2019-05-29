package com.massita.web;

import com.massita.service.ServiceRunner;
import com.massita.service.messaging.MessageService;
import com.massita.web.servlet.UserDataSetServlet;
import com.massita.web.servlet.UserDataSetStatsServlet;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public class WebServer {

    private final static int PORT = 8080;

    public void start() throws Exception {

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);

        Server server = new Server(PORT);

        ServiceRunner.runAllServices();
        MessageService messageService = ServiceRunner.context.getMessageService();

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

}
