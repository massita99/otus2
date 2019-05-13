package com.massita.web.servlet;

import com.massita.model.UserDataSet;
import com.massita.service.db.DBService;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Optional;

import static org.eclipse.jetty.http.HttpStatus.Code.NOT_FOUND;
import static org.eclipse.jetty.http.HttpStatus.Code.OK;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class UserDataSetServletTest {

    @Mock
    HttpServletRequest request;

    @Mock
    HttpServletResponse response;

    @Mock
    DBService<UserDataSet> dbService;

    UserDataSetServlet userDataSetServlet;


    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        userDataSetServlet =new UserDataSetServlet(dbService);

    }


    @Test
    public void doGetPositive() throws Exception{
        //Mocking
        when(request.getParameter("id")).thenReturn("1");
        when(dbService.readForClass(1l, UserDataSet.class)).thenReturn(Optional.of(new UserDataSet("mass", 20)));

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        when(response.getWriter()).thenReturn(pw);

        //DoWork
        userDataSetServlet.doGet(request, response);
        String result = sw.getBuffer().toString();
        JSONObject object = new JSONObject(result);

        //Verify
        verify(response).setStatus(OK.getCode());
        assertEquals(object.getString("name"), "mass");
    }

    @Test
    public void doGetNegative() throws Exception {
        when(request.getParameter("id")).thenReturn("2");
        when(dbService.readForClass(2l, UserDataSet.class)).thenReturn(Optional.empty());

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        when(response.getWriter()).thenReturn(pw);

        userDataSetServlet.doGet(request, response);

        verify(response).setStatus(NOT_FOUND.getCode());
    }

    @Test
    public void doPostPositive() throws Exception{
        //Mocking

        ArgumentCaptor<UserDataSet> userCaptor = ArgumentCaptor.forClass(UserDataSet.class);

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
        verify(dbService).save(userCaptor.capture());
        assertEquals(userCaptor.getValue().getName(), "mass");
    }
}