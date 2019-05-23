package com.massita.web.servlet;

import lombok.SneakyThrows;

import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

public class ServletHelper {

    private static final String APPLICATION_JSON = "application/json;charset=UTF-8";

    @SneakyThrows
    public static PrintWriter getJsonWriter(HttpServletResponse resp) {
        resp.setContentType(APPLICATION_JSON);
        return resp.getWriter();
    }

}
