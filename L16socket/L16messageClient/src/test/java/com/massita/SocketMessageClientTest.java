package com.massita;

import com.massita.service.messaging.MessageListener;
import com.massita.service.messaging.message.Address;
import com.massita.service.messaging.message.Message;
import com.massita.service.messaging.message.ObjectMessage;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class SocketMessageClientTest {

    Address from;
    Address to;

    @Mock
    MessageListener toListener;

    SocketMessageClient client;
    SocketMessageServer server;

    @Before

    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        server = new SocketMessageServer();

        from = new Address();
        to = new Address();
        ExecutorService executor = Executors.newFixedThreadPool(2);
        executor.submit(() -> server.start());
        //wait while server start

        Thread.sleep(200);
        client = new SocketMessageClient();
        executor.submit(() -> {
                    client.start();
                    client.subscribe(to, toListener);
                }
        );
        //wait client started
        Thread.sleep(200);
    }

    @Test
    public void selfMessageReceiveTest() throws InterruptedException {


        Message message = new ObjectMessage(from, to, "test");
        client.sendMessage(message);
        //Just to be sure that message will be send by MS
        Thread.sleep(100);
        //Check that listenet get Message
        verify(toListener, times(1)).onMessage(any());

    }
}