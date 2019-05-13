package com.massita.web.servlet;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.massita.model.UserDataSet;
import com.massita.service.db.DBService;
import com.massita.service.db.hibernate.HibernateProxyTypeAdapter;
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
        GsonBuilder b = new GsonBuilder();
        b.registerTypeAdapterFactory(HibernateProxyTypeAdapter.FACTORY);
        this.gson = b.create();
    }


    @Override
    @SneakyThrows
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        Long userId;
        String requestedId = req.getParameter("id");
        resp.setContentType(APPLICATION_JSON);
        PrintWriter out = resp.getWriter();

        try {
            userId = Long.parseLong(requestedId);
        } catch (NumberFormatException e) {
            resp.setStatus(NOT_FOUND.getCode());
            out.print(gson.toJson(Map.entry("message", String.format("Id %s should be numeric", requestedId))));
            logger.warn("Id {} should be numeric", requestedId);
            return;
        }
        Optional<UserDataSet> user = dbService.readForClass(userId, UserDataSet.class);

        if (user.isEmpty()) {
            resp.setStatus(NOT_FOUND.getCode());
            out.print(gson.toJson(Map.entry("message", String.format("User with id=%s not exist", userId))));
            logger.warn("User with id {} does not exist", userId);
            return;
        }

        out.print(gson.toJson(user.get()));
        resp.setStatus(SC_OK);

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
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

        String serializedUser = jb.toString();
        if (!serializedUser.isEmpty()) {
            UserDataSet user = gson.fromJson(serializedUser, UserDataSet.class);
            dbService.save(user);
            PrintWriter out = resp.getWriter();
            out.print(gson.toJson(Map.entry("message", "User was saved")));
            resp.setStatus(SC_OK);
        } else {
            resp.setStatus(SC_BAD_REQUEST);
            logger.error("Error while saving {}", serializedUser);
        }

    }
}
