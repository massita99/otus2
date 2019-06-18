package com.massita.web.servlet;

import com.google.gson.Gson;
import com.massita.model.UserDataSet;
import com.massita.service.GsonService;
import com.massita.service.messaging.MessageService;
import com.massita.service.messaging.message.DbMessage;
import com.massita.service.messaging.message.Message;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import javax.servlet.AsyncContext;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static com.massita.service.messaging.message.DbMessage.DB_SERVICE_ADDRESS;
import static com.massita.web.servlet.ServletHelper.doAsyncGet;
import static com.massita.web.servlet.ServletHelper.getJsonWriter;

@RequiredArgsConstructor
public class UserDataSetStatsServlet extends HttpServlet {

    private static final Logger logger
            = LoggerFactory.getLogger(UserDataSetStatsServlet.class);

    public static final String COUNT = "count";

    @Autowired
    private Gson gson;

    @Autowired
    private MessageService messageService;

    public UserDataSetStatsServlet(MessageService messageService) {
        this.messageService = messageService;
        this.gson = GsonService.getInstance().getGson();
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
        logger.info("Servlet UserDataSetStatsServlet started");

    }


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        Map<String, String[]> result = new HashMap<>();
        String[] params = req.getParameterValues("stat");


        if (Arrays.asList(params).contains(COUNT)) {

            AsyncContext asyncCtx = req.startAsync();

            Message messageToDb = new DbMessage(null,
                    DB_SERVICE_ADDRESS,
                    DbMessage.DbMessageType.COUNT,
                    null,
                    UserDataSet.class);

            doAsyncGet(messageService, messageToDb, asyncCtx,
                    receivedMessage -> {
                        String[] userCount = {Long.toString((Long) receivedMessage.getBody())};
                        result.put(COUNT, userCount);

                        PrintWriter out = getJsonWriter(resp);

                        out.print(gson.toJson(result));
                        resp.setStatus(HttpServletResponse.SC_OK);
                    }
            );
        }

    }

}
