package com.massita.service.messaging;

import com.massita.service.messaging.message.Address;
import com.massita.service.messaging.message.Message;
import com.massita.service.messaging.message.TextMessage;
import lombok.SneakyThrows;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class MessageServiceTest {

    MessageService messageService;
    Address from;
    Address to;

    @Mock
    MessageListener fromListener;

    @Mock
    MessageListener toListener;

    @Before
    public void setUp(){
        MockitoAnnotations.initMocks(this);
        messageService = new MessageServiceImpl();
        messageService.start();
        from = new Address();
        to = new Address();
        messageService.subscribe(to, toListener);
        messageService.subscribe(from, fromListener);

    }

    @Test
    public void simpleSendMessage() {
        Message message = new TextMessage(from, to, "test");
        messageService.sendMessage(message);
    }

    @Test
    @SneakyThrows
    public void simpleReceiveMessage() {
         //Prepare and send message
        Message message = new TextMessage(from, to, "test");
        messageService.sendMessage(message);
        //Just to be sure that message will be send by MS
        Thread.sleep(10);
        //Check that listenet get Message
        verify(toListener).onMessage(message);
        verify(fromListener, times(0)).onMessage(message);
    }

    @Test
    @SneakyThrows
    public void unsubscribe() {
        //Prepare and send message
        Message message = new TextMessage(from, to, "test");
        messageService.unsubscribe(to, toListener);
        messageService.sendMessage(message);
        //Just to be sure that message will be send by MS
        Thread.sleep(10);
        //Check that listenet do get Message
        verify(toListener, times(0)).onMessage(message);
    }
}