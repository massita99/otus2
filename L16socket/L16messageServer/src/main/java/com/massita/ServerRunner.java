package com.massita;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ServerRunner {

    public static void main(String[] args) throws Exception {
        ApplicationContext context;
        context = new ClassPathXmlApplicationContext("applicationContextServer.xml");
        Thread.currentThread().join();

    }
}
