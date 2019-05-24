package com.massita.web.servlet;

import com.massita.service.messaging.MessageListener;
import com.massita.service.messaging.MessageService;
import com.massita.service.messaging.message.Address;
import com.massita.service.messaging.message.Message;
import com.massita.service.messaging.message.ObjectMessage;
import lombok.SneakyThrows;

import javax.servlet.AsyncContext;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.function.Consumer;

public class ServletHelper {

    private static final String APPLICATION_JSON = "application/json;charset=UTF-8";

    @SneakyThrows
    public static PrintWriter getJsonWriter(HttpServletResponse resp) {
        resp.setContentType(APPLICATION_JSON);
        return resp.getWriter();
    }

    public static void doAsyncGet(MessageService service, Message message, AsyncContext asyncContext, Consumer<ObjectMessage> serviceResultHandler) {
        Address resultWaiterAddress = new Address();
        MessageListener resultWaiter = new MessageListener() {
            @Override
            public void onMessage(Message message) {
                if (message instanceof ObjectMessage) {

                    ObjectMessage objectMessage = (ObjectMessage) message;

                    serviceResultHandler.accept(objectMessage);

                    service.unsubscribe(resultWaiterAddress, this);
                    asyncContext.complete();
                }
            }

        };
        service.subscribe(resultWaiterAddress, resultWaiter);
        message.setFrom(resultWaiterAddress);
        service.sendMessage(message);
    }

}
