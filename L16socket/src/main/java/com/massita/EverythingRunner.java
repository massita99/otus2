package com.massita;

import org.eclipse.jetty.jmx.MBeanContainer;
import org.eclipse.jetty.webapp.WebAppContext;
import org.h2.tools.Server;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EverythingRunner {

    private static final int THREADS_COUNT = 2;
    public static final String DB_SERVICE_JAR_PATH = "./L16socket/L16db/target/L16db.jar";
    public static final String MESSAGE_SERVER_JAR_PATH = "./L16socket/L16messageServer/target/L16messageServer-1.0-SNAPSHOT.jar";
    public static final String H2_DB_PORT = "7778";
    public static final String WAR_PATH = "./L16socket/L16web/target/L16web1.war";
    public static final int FRONTEND_FIRST_PORT = 8080;
    public static final int FRONTEND_SECOND_PORT = 18080;

    public static void main(String[] args) throws Exception {

        //Start DB
        Server dbServer = Server.createTcpServer("-tcp", "-tcpAllowOthers", "-ifNotExists", "-tcpPort", H2_DB_PORT).start();

        //Start message Server
        Process serverProcess = Runtime.getRuntime()
                .exec("java -jar " + MESSAGE_SERVER_JAR_PATH);

        BufferedReader outputServer = new BufferedReader(new InputStreamReader(serverProcess.getInputStream()));

        waitForServerStart(outputServer);

        //Start dbServers
        Process db1Process = Runtime.getRuntime()
                .exec("java -jar " + DB_SERVICE_JAR_PATH);

        Process db2Process = Runtime.getRuntime()
                .exec("java -jar " + DB_SERVICE_JAR_PATH);


        BufferedReader outputDb1 = new BufferedReader(new InputStreamReader(db1Process.getInputStream()));
        BufferedReader outputDb2 = new BufferedReader(new InputStreamReader(db2Process.getInputStream()));

        waitForSbServicesStarted(outputDb1, outputDb2);

        ExecutorService executorService = Executors.newFixedThreadPool(THREADS_COUNT);

        executorService.submit(() -> runFrontend(FRONTEND_FIRST_PORT));
        executorService.submit(() -> runFrontend(FRONTEND_SECOND_PORT));

    }

    private static void runFrontend(int port) {

        org.eclipse.jetty.server.Server webServer = new org.eclipse.jetty.server.Server (port);

        MBeanContainer mbContainer = new MBeanContainer(
                ManagementFactory.getPlatformMBeanServer());
        webServer.addBean(mbContainer);

        WebAppContext webapp = new WebAppContext();
        webapp.setContextPath("/");
        File warFile = new File(
                WAR_PATH);
        webapp.setWar(warFile.getAbsolutePath());
        webServer.setHandler(webapp);

        try {
            webServer.start();
            webServer.dumpStdErr();
            webServer.join();
        } catch (Exception e) {
            e.printStackTrace();

        }


    }

    private static void waitForSbServicesStarted(BufferedReader outputDb1, BufferedReader outputDb2) throws IOException {

        boolean waitForDbServiceStart1 = true;
        boolean waitForDbServiceStart2 = true;
        while (waitForDbServiceStart1 || waitForDbServiceStart2) {

            if (waitForDbServiceStart1 && outputDb1.readLine().contains("DbService start")) {
                waitForDbServiceStart1 = false;
            }
            if (waitForDbServiceStart2 && outputDb2.readLine().contains("DbService start")) {
                waitForDbServiceStart2 = false;
            }
        }
    }

    private static void waitForServerStart(BufferedReader outputServer) throws IOException {
        boolean waitForServerStart = true;
        while (waitForServerStart) {

            if (outputServer.readLine().contains("Server started")) {
                waitForServerStart = false;
            }
        }
    }
}
