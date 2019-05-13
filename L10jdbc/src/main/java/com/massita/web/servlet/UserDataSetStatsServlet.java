package com.massita.web.servlet;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.massita.model.UserDataSet;
import com.massita.service.db.DBService;
import com.massita.service.db.hibernate.HibernateProxyTypeAdapter;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class UserDataSetStatsServlet extends HttpServlet {

    private static final Logger logger
            = LoggerFactory.getLogger(UserDataSetStatsServlet.class);

    private static final String APPLICATION_JSON = "application/json;charset=UTF-8";
    public static final String COUNT = "count";
    private final DBService<UserDataSet> dbService;
    private final Gson gson;

    public UserDataSetStatsServlet(DBService<UserDataSet> dbService) {
        this.dbService = dbService;
        GsonBuilder b = new GsonBuilder();
        b.registerTypeAdapterFactory(HibernateProxyTypeAdapter.FACTORY);
        this.gson = b.create();
    }


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Map<String, String[]> result = new HashMap<>();
        String[] params = req.getParameterValues("stat");
        if (Arrays.asList(params).contains(COUNT)) {
            Long userCount = dbService.count(UserDataSet.class);
            result.put(COUNT, new String[]{userCount.toString()});
        }

        if (result.isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            logger.warn("Bad stat request");
        } else {
            resp.setContentType(APPLICATION_JSON);
            resp.getWriter().print(gson.toJson(result));
            resp.setStatus(HttpServletResponse.SC_OK);
        }
    }
}
