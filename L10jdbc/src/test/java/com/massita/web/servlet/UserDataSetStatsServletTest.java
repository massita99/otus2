package com.massita.web.servlet;

import com.massita.service.messaging.MessageListener;
import com.massita.service.messaging.MessageService;
import com.massita.service.messaging.message.Message;
import com.massita.service.messaging.message.ObjectMessage;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.servlet.AsyncContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.StringWriter;

import static org.eclipse.jetty.http.HttpStatus.Code.OK;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class UserDataSetStatsServletTest {

    @Mock
    HttpServletRequest request;

    @Mock
    HttpServletResponse response;

    @Mock
    MessageService messageService;

    @Mock
    AsyncContext asyncCtx;

    UserDataSetStatsServlet userDataSetServlet;


    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        userDataSetServlet =new UserDataSetStatsServlet(messageService);
        when(request.startAsync()).thenReturn(asyncCtx);

    }


    @Test
    public void doGetCountPositive() throws Exception{

        //Mocking
        String[] reqParams = new String[]{"count"};
        when(request.getParameterValues("stat")).thenReturn(reqParams);

        ArgumentCaptor<MessageListener> listenerCaptor = ArgumentCaptor.forClass(MessageListener.class);

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        when(response.getWriter()).thenReturn(pw);

        //DoWork
        userDataSetServlet.doGet(request, response);

        //Simulate return Message
        sendServiceAnswer(new ObjectMessage(null, null, 5L), listenerCaptor);

        String result = sw.getBuffer().toString();
        JSONObject object = new JSONObject(result);

        //Verify
        verify(response).setStatus(OK.getCode());
        assertEquals(object.get("count").toString(), "[\"5\"]");
    }

    private void sendServiceAnswer(Message message, ArgumentCaptor<MessageListener> captor) {
        verify(messageService).subscribe(any(), captor.capture());
        captor.getValue().onMessage(new ObjectMessage(null, null, 5L));
    }
}
