package com.massita.web.servlet;

import com.google.gson.Gson;
import com.massita.model.UserDataSet;
import com.massita.service.GsonService;
import com.massita.service.db.DBService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static com.massita.web.servlet.ServletHelper.getJsonWriter;

@RequiredArgsConstructor
public class UserDataSetStatsServlet extends HttpServlet {

    private static final Logger logger
            = LoggerFactory.getLogger(UserDataSetStatsServlet.class);

    public static final String COUNT = "count";
    private final DBService<UserDataSet> dbService;
    private final Gson gson;

    public UserDataSetStatsServlet(DBService<UserDataSet> dbService) {
        this.dbService = dbService;
        this.gson = GsonService.getInstance().getGson();
    }


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)throws IOException {
        Map<String, String[]> result = new HashMap<>();
        String[] params = req.getParameterValues("stat");
        if (Arrays.asList(params).contains(COUNT)) {
            long userCount = dbService.count(UserDataSet.class);
            result.put(COUNT, new String[]{Long.toString(userCount)});
        }

        if (result.isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            logger.warn("Bad stat request");
        } else {

            PrintWriter out = getJsonWriter(resp);
            out.print(gson.toJson(result));
            resp.setStatus(HttpServletResponse.SC_OK);
        }
    }

}
