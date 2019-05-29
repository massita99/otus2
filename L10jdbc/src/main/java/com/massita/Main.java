package com.massita;

import com.massita.service.ServiceRunner;
import com.massita.web.WebServer;

public class Main {

    public static void main(String[] args) throws Exception {
        ServiceRunner.runAllServices();
        new WebServer().start();
    }



}
