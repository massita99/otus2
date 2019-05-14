package com.massita.web.servlet;

import com.google.gson.Gson;
import com.massita.model.UserDataSet;
import com.massita.service.GsonService;
import com.massita.service.db.DBService;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Optional;

import static com.massita.web.servlet.ServletHelper.getJsonWriter;
import static javax.servlet.http.HttpServletResponse.*;
import static org.eclipse.jetty.http.HttpStatus.Code.NOT_FOUND;


public class UserDataSetServlet extends HttpServlet {

    private static final Logger logger
            = LoggerFactory.getLogger(UserDataSetServlet.class);

    private static final String APPLICATION_JSON = "application/json;charset=UTF-8";
    private final DBService<UserDataSet> dbService;
    private final Gson gson;

    public UserDataSetServlet(DBService<UserDataSet> dbService) {
        this.dbService = dbService;
        this.gson = GsonService.getInstance().getGson();
    }


    @Override
    @SneakyThrows
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {

        PrintWriter out = getJsonWriter(resp);

        Optional<Long> userId = getLongParameter("id", req, resp, out);
        if (userId.isEmpty()) {
            return;
        }

        Optional<UserDataSet> user = dbService.readForClass(userId.get(), UserDataSet.class);

        if (user.isEmpty()) {

            resp.setStatus(NOT_FOUND.getCode());
            out.print(gson.toJson(Map.entry("message", String.format("User with id=%s not exist", userId.get()))));
            logger.warn("User with id {} does not exist", userId.get());
        } else {
            out.print(gson.toJson(user.get()));
            resp.setStatus(SC_OK);
        }

    }

    private Optional<Long> getLongParameter(String parameterName, HttpServletRequest req, HttpServletResponse resp, PrintWriter out) {
        Long userId;
        String requestedId = req.getParameter(parameterName);
        try {
            userId = Long.parseLong(requestedId);
        } catch (NumberFormatException e) {
            resp.setStatus(NOT_FOUND.getCode());
            out.print(gson.toJson(Map.entry("message", String.format("%s %s should be numeric", parameterName, requestedId))));
            logger.warn("{} {} should be numeric", parameterName, requestedId);
            return Optional.empty();
        }
        return Optional.of(userId);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String serializedUser = getRequestBody(req, resp);

        if (!serializedUser.isEmpty()) {

            UserDataSet user = gson.fromJson(serializedUser, UserDataSet.class);

            dbService.save(user);

            PrintWriter out = getJsonWriter(resp);
            out.print(gson.toJson(Map.entry("message", "User was saved")));
            resp.setStatus(SC_OK);
        } else {
            resp.setStatus(SC_BAD_REQUEST);
            logger.error("Error while saving {}", serializedUser);
        }

    }

    private String getRequestBody(HttpServletRequest req, HttpServletResponse resp) {
        StringBuilder jb = new StringBuilder();
        String line;
        try {
            BufferedReader reader = req.getReader();
            while ((line = reader.readLine()) != null) {
                jb.append(line);
            }
        } catch (Exception e) {
            resp.setStatus(SC_INTERNAL_SERVER_ERROR);
            logger.error("Error while reading request");
        }

        return jb.toString();
    }
}
