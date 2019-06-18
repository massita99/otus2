package com.massita.web.servlet;

import com.massita.model.UserDataSet;
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
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;

import static org.eclipse.jetty.http.HttpStatus.Code.NOT_FOUND;
import static org.eclipse.jetty.http.HttpStatus.Code.OK;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class UserDataSetServletTest {

    @Mock
    HttpServletRequest request;

    @Mock
    HttpServletResponse response;

    @Mock
    MessageService messageService;

    @Mock
    AsyncContext asyncCtx;

    UserDataSetServlet userDataSetServlet;


    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        userDataSetServlet =new UserDataSetServlet(messageService);
        when(request.startAsync()).thenReturn(asyncCtx);

    }


    @Test
    public void doGetPositive() throws Exception{
        //Mocking
        when(request.getParameter("id")).thenReturn("1");

        ArgumentCaptor<MessageListener> listenerCaptor = ArgumentCaptor.forClass(MessageListener.class);

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        when(response.getWriter()).thenReturn(pw);

        //DoWork
        userDataSetServlet.doGet(request, response);
        //Simulate return Message
        sendServiceAnswer(new ObjectMessage(null, null, new UserDataSet("mass", 20)), listenerCaptor);

        String result = sw.getBuffer().toString();
        JSONObject object = new JSONObject(result);

        //Verify
        verify(response).setStatus(OK.getCode());
        assertEquals(object.getString("name"), "mass");
    }

    @Test
    public void doGetNegative() throws Exception {
        when(request.getParameter("id")).thenReturn("2");
        //when(dbService.readForClass(2l, UserDataSet.class)).thenReturn(Optional.empty());
        ArgumentCaptor<MessageListener> listenerCaptor = ArgumentCaptor.forClass(MessageListener.class);

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        when(response.getWriter()).thenReturn(pw);

        userDataSetServlet.doGet(request, response);
        //Simulate return Message
        sendServiceAnswer(new ObjectMessage(null, null, null), listenerCaptor);

        verify(response).setStatus(NOT_FOUND.getCode());
    }

    @Test
    public void doPostPositive() throws Exception{
        //Mocking

        ArgumentCaptor<ObjectMessage> messageCaptor = ArgumentCaptor.forClass(ObjectMessage.class);

        StringReader sr = new StringReader("{\"name\":\"mass\",\"age\":20}");
        BufferedReader pr = new BufferedReader(sr);
        when(request.getReader()).thenReturn(pr);
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        when(response.getWriter()).thenReturn(pw);
        //do work
        userDataSetServlet.doPost(request, response);

        //Verify
        verify(response).setStatus(OK.getCode());
        verify(messageService).sendMessage(messageCaptor.capture());
        assertEquals(((UserDataSet)messageCaptor.getValue().getBody()).getName(), "mass");
    }

    private void sendServiceAnswer(Message message, ArgumentCaptor<MessageListener> captor) {
        verify(messageService).subscribe(any(), captor.capture());
        captor.getValue().onMessage(message);
    }
}