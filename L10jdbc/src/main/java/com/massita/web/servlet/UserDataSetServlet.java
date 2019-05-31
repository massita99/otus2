package com.massita.web.servlet;

import com.google.gson.Gson;
import com.massita.model.UserDataSet;
import com.massita.service.GsonService;
import com.massita.service.messaging.MessageService;
import com.massita.service.messaging.message.DbMessage;
import com.massita.service.messaging.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import javax.servlet.AsyncContext;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Optional;

import static com.massita.service.messaging.message.DbMessage.DB_SERVICE_ADDRESS;
import static com.massita.web.servlet.ServletHelper.doAsyncGet;
import static com.massita.web.servlet.ServletHelper.getJsonWriter;
import static javax.servlet.http.HttpServletResponse.*;
import static org.eclipse.jetty.http.HttpStatus.Code.NOT_FOUND;

@Configurable
public class UserDataSetServlet extends HttpServlet {

    private static final Logger logger
            = LoggerFactory.getLogger(UserDataSetServlet.class);

    private static final String APPLICATION_JSON = "application/json;charset=UTF-8";

    @Autowired
    private MessageService messageService;
    @Autowired
    private Gson gson;

    public UserDataSetServlet() {
    }

    public UserDataSetServlet(MessageService messageService) {
        this.gson = GsonService.getInstance().getGson();
        this.messageService = messageService;
    }

    @Override
    public void init(ServletConfig config) throws ServletException{
        super.init(config);
        SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
        logger.info("Servlet UserDataSetServlet started");

    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {

        Optional<Long> userId = getLongParameter("id", req, resp);
        if (userId.isEmpty()) {
            return;
        }
        AsyncContext asyncCtx = req.startAsync();

        Message messageToDb = new DbMessage(null,
                DB_SERVICE_ADDRESS,
                DbMessage.DbMessageType.LOAD,
                userId.get(),
                UserDataSet.class);

        doAsyncGet(messageService, messageToDb, asyncCtx,
                receivedMessage -> {
                    Optional<UserDataSet> user = (Optional<UserDataSet>) receivedMessage.getBody();

                    PrintWriter out = getJsonWriter(resp);
                    if (user.isEmpty()) {

                        resp.setStatus(NOT_FOUND.getCode());
                        out.print(gson.toJson(Map.entry("message", String.format("User with id=%s not exist", userId.get()))));
                        logger.warn("User with id {} does not exist", userId.get());
                    } else {
                        out.print(gson.toJson(user.get()));
                        resp.setStatus(SC_OK);
                    }
                }
        );
    }

    private Optional<Long> getLongParameter(String parameterName, HttpServletRequest req, HttpServletResponse resp) {
        Long userId;
        String requestedId = req.getParameter(parameterName);
        try {
            userId = Long.parseLong(requestedId);
        } catch (NumberFormatException e) {
            PrintWriter out = getJsonWriter(resp);
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

            messageService.sendMessage(new DbMessage(null,
                    DB_SERVICE_ADDRESS,
                    DbMessage.DbMessageType.SAVE,
                    user,
                    UserDataSet.class));

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
