package com.massita.workers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.massita.service.messaging.message.Address;
import com.massita.service.messaging.message.Message;
import com.massita.service.messaging.message.ObjectMessage;
import com.massita.utils.serealizers.SpecialObjectMapperFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.*;
import java.net.Socket;

import static org.mockito.Mockito.when;


public class SocketMessageWorkerTest {

    @Mock
    Socket socket;

    MessageWorker messageWorker;

    Message testMessage;

    ObjectMapper mapper = SpecialObjectMapperFactory.getMessageObjectMapper();

    ByteArrayOutputStream mockOutputStream;

    @Before
    public void setUp() throws IOException {
        MockitoAnnotations.initMocks(this);
        when(socket.isConnected()).thenReturn(true);
        testMessage = new ObjectMessage(new Address("from"), new Address("to"), "Tests");

        mockOutputStream = new ByteArrayOutputStream();
        when(socket.getOutputStream()).thenReturn(mockOutputStream);
        InputStream stubInputStream = new ByteArrayInputStream("".getBytes());
        when(socket.getInputStream()).thenReturn(stubInputStream);

        messageWorker = new SocketMessageWorker(socket);


    }

    @Test
    public void poolTest() throws IOException, InterruptedException {
        //Prepare data
        String serialiazedTestMessage = mapper.writeValueAsString(testMessage) + "\n\n";
        InputStream mockInputStream = new ByteArrayInputStream(serialiazedTestMessage.getBytes());

        when(socket.getInputStream()).thenReturn(mockInputStream);

        //Looking for message
        ((SocketMessageWorker)messageWorker).init();
        //Just to be sure that thread receive message
        Thread.sleep(100);
        Message receivedMessage = messageWorker.pool();

        //Test
        Assert.assertEquals(testMessage.getFrom().getId(), receivedMessage.getFrom().getId());

    }

    @Test
    public void sendTest() throws IOException, InterruptedException {

        //Sending for message
        ((SocketMessageWorker)messageWorker).init();
        messageWorker.send(testMessage);

        //Just to be sure that thread send message
        Thread.sleep(50);
        Message sendedMessage = mapper.readValue(mockOutputStream.toString(), Message.class);

        //Test
        Assert.assertEquals(testMessage.getFrom().getId(), sendedMessage.getFrom().getId());




    }
}