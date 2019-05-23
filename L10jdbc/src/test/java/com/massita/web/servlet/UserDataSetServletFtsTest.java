package com.massita.web.servlet;

import com.massita.model.AddressDataSet;
import com.massita.model.PhoneDataSet;
import com.massita.model.UserDataSet;
import com.massita.service.db.DBService;
import com.massita.service.db.DDLService;
import com.massita.service.db.DDLServiceImpl;
import com.massita.service.db.hibernate.DBServiceHibernateImpl;
import com.massita.service.db.util.dbcommon.ConnectionHelper;
import com.massita.service.messaging.MessageListener;
import com.massita.service.messaging.MessageService;
import org.hibernate.cfg.Configuration;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.servlet.AsyncContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static com.massita.service.messaging.message.DbMessage.DB_SERVICE_ADDRESS;
import static org.eclipse.jetty.http.HttpStatus.Code.OK;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class UserDataSetServletFtsTest {
    @Mock
    HttpServletRequest request;

    @Mock
    HttpServletResponse response;


    DBService<UserDataSet> dbService;
    UserDataSetServlet userDataSetServlet;
    UserDataSetStatsServlet userDataSetStatsServlet;
    Connection connection;
    DDLService ddlService;
    MessageService messageService;

    @Mock
    AsyncContext asyncCtx;


    @Before
    public void setUp() throws SQLException {
        MockitoAnnotations.initMocks(this);
        connection = ConnectionHelper.getConnection();
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        Configuration configuration = new Configuration()
                .configure(new File(classLoader.getResource("service/db/hibernate/hibernate.cfg.xml").getFile()))
                .addAnnotatedClass(UserDataSet.class)
                .addAnnotatedClass(PhoneDataSet.class)
                .addAnnotatedClass(AddressDataSet.class);
        dbService = new DBServiceHibernateImpl<>(configuration);
        ddlService = new DDLServiceImpl(connection);
        ddlService.prepareTables();
        try (final Statement statement = connection.createStatement()) {
            statement.execute("insert into userdataset (name, age) values ('massita', 30 );");
        }
        messageService = new MessageService();
        messageService.start();
        ((DBServiceHibernateImpl)dbService).setMessageService(messageService);
        messageService.subscribe(DB_SERVICE_ADDRESS, (MessageListener) dbService);
        userDataSetStatsServlet = new UserDataSetStatsServlet(messageService);
        userDataSetServlet = new UserDataSetServlet(messageService);

        when(request.startAsync()).thenReturn(asyncCtx);



    }

    @After
    public void afterTest() throws SQLException {
        ddlService.deleteTables();
    }


    @Test
    public void test() throws Exception {
        //Mocking
        when(request.getParameter("id")).thenReturn("1");

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        when(response.getWriter()).thenReturn(pw);

        //DoWork Stage 1 first looking for existing
        userDataSetServlet.doGet(request, response);
        Thread.sleep(200);
        String result = sw.getBuffer().toString();
        JSONObject object = new JSONObject(result);

        //Verify
        verify(response).setStatus(OK.getCode());
        assertEquals(object.getString("name"), "massita");


        //Prepare new data
        StringReader sr = new StringReader("{\"name\":\"mass\",\"age\":20}");
        BufferedReader pr = new BufferedReader(sr);
        when(request.getReader()).thenReturn(pr);
        sw = new StringWriter();
        pw = new PrintWriter(sw);
        when(response.getWriter()).thenReturn(pw);

        //do work Stage 2 add user and check that count is 2
        userDataSetServlet.doPost(request, response);
        // Wait for messageService
        Thread.sleep(200);
        //Prepare for stat
        String[] reqParams = new String[]{"count"};
        when(request.getParameterValues("stat")).thenReturn(reqParams);
        sw = new StringWriter();
        pw = new PrintWriter(sw);
        when(response.getWriter()).thenReturn(pw);

        //do work 3 check stat
        userDataSetStatsServlet.doGet(request, response);
        // Wait for messageService
        Thread.sleep(200);
        result = sw.getBuffer().toString();
        object = new JSONObject(result);

        //Verify
        assertEquals(object.get("count").toString(), "[\"2\"]");
    }
}
