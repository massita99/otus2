package com.massita.web.servlet;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class ServletHelper {

    private static final String APPLICATION_JSON = "application/json;charset=UTF-8";

    public static PrintWriter getJsonWriter(HttpServletResponse resp) throws IOException {
        resp.setContentType(APPLICATION_JSON);
        return resp.getWriter();
    }

}
