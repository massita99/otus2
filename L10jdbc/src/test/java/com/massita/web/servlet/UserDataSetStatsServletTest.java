package com.massita.web.servlet;

import com.massita.model.UserDataSet;
import com.massita.service.db.DBService;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.StringWriter;

import static org.eclipse.jetty.http.HttpStatus.Code.OK;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class UserDataSetStatsServletTest {

    @Mock
    HttpServletRequest request;

    @Mock
    HttpServletResponse response;

    @Mock
    DBService<UserDataSet> dbService;

    UserDataSetStatsServlet userDataSetServlet;


    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        userDataSetServlet =new UserDataSetStatsServlet(dbService);

    }


    @Test
    public void doGetCountPositive() throws Exception{

        //Mocking
        String[] reqParams = new String[]{"count"};
        when(request.getParameterValues("stat")).thenReturn(reqParams);

        when(dbService.count(UserDataSet.class)).thenReturn(5l);

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        when(response.getWriter()).thenReturn(pw);

        //DoWork
        userDataSetServlet.doGet(request, response);
        String result = sw.getBuffer().toString();
        JSONObject object = new JSONObject(result);

        //Verify
        verify(response).setStatus(OK.getCode());
        assertEquals(object.get("count").toString(), "[\"5\"]");
    }
}