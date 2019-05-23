package com.massita.web.servlet;

import com.google.gson.Gson;
import com.massita.model.UserDataSet;
import com.massita.service.GsonService;
import com.massita.service.messaging.MessageListener;
import com.massita.service.messaging.MessageService;
import com.massita.service.messaging.message.Address;
import com.massita.service.messaging.message.DbMessage;
import com.massita.service.messaging.message.Message;
import com.massita.service.messaging.message.ObjectMessage;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.AsyncContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static com.massita.service.messaging.message.DbMessage.DB_SERVICE_ADDRESS;
import static com.massita.web.servlet.ServletHelper.getJsonWriter;

@RequiredArgsConstructor
public class UserDataSetStatsServlet extends HttpServlet {

    private static final Logger logger
            = LoggerFactory.getLogger(UserDataSetStatsServlet.class);

    public static final String COUNT = "count";
    private final Gson gson;
    private MessageService messageService;

    public UserDataSetStatsServlet(MessageService messageService) {
        this.messageService = messageService;
        this.gson = GsonService.getInstance().getGson();
    }


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Map<String, String[]> result = new HashMap<>();
        String[] params = req.getParameterValues("stat");

        AsyncContext asyncCtx = req.startAsync();

        if (Arrays.asList(params).contains(COUNT)) {

            Address resultWaiterAddress = new Address();
            MessageListener resultWaiter = new MessageListener() {
                @Override
                public void onMessage(Message message) {
                    if (message instanceof ObjectMessage) {
                        ObjectMessage objectMessage = (ObjectMessage) message;
                        result.put(COUNT, new String[]{Long.toString((Long) objectMessage.getBody())});
                        messageService.unsubscribe(resultWaiterAddress, this);
                        if (result.isEmpty()) {
                            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                            logger.warn("Bad stat request");
                        } else {

                            PrintWriter out = getJsonWriter(resp);
                            out.print(gson.toJson(result));
                            resp.setStatus(HttpServletResponse.SC_OK);
                        }
                        asyncCtx.complete();
                    }
                }

            };
            messageService.subscribe(resultWaiterAddress, resultWaiter);
            messageService.sendMessage(new DbMessage(resultWaiterAddress,
                    DB_SERVICE_ADDRESS,
                    DbMessage.DbMessageType.COUNT,
                    null,
                    UserDataSet.class));
        }

    }

}
