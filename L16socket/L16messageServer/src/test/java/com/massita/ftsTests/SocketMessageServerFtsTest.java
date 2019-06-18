package com.massita.ftsTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.massita.SocketMessageServer;
import com.massita.service.messaging.message.Address;
import com.massita.service.messaging.message.Message;
import com.massita.service.messaging.message.ObjectMessage;
import com.massita.service.messaging.message.TextMessage;
import com.massita.utils.serealizers.SpecialObjectMapperFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

import static com.massita.SocketMessageServer.SUBSCRIBE;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(SocketMessageServer.class)
public class SocketMessageServerFtsTest {

    @Mock
    Socket firstSocket;

    @Mock
    Socket secondSocket;

    @Mock
    ServerSocket serverSocket;

    SocketMessageServer server;

    Message testMessage;

    ObjectMapper mapper = SpecialObjectMapperFactory.getMessageObjectMapper();

    ByteArrayOutputStream mockOutputStreamOne;
    ByteArrayOutputStream mockOutputStreamTwo;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        //ServerSocket mock = mock(ServerSocket.class);
        PowerMockito.whenNew(ServerSocket.class).withAnyArguments().thenReturn(serverSocket);

        when(serverSocket.accept()).thenAnswer(new Answer<Socket>() {
            @Override
            public Socket answer(InvocationOnMock invocation) throws Throwable {
                if (i == 0) {
                    i++;
                    return firstSocket;
                } else if (i == 1) {
                    i++;
                    return secondSocket;
                }
                //Blocks
                Thread.sleep(500);
                 server.stop();

                return secondSocket;
            }

            int i = 0;

        });
        when(firstSocket.isConnected()).thenReturn(true);
        when(secondSocket.isConnected()).thenReturn(true);
        testMessage = new ObjectMessage(new Address("from"), new Address("to"), "Tests");

        mockOutputStreamOne = new ByteArrayOutputStream();
        mockOutputStreamTwo = new ByteArrayOutputStream();

        when(firstSocket.getOutputStream()).thenReturn(mockOutputStreamOne);
        when(secondSocket.getOutputStream()).thenReturn(mockOutputStreamTwo);
        InputStream stubInputStream = new ByteArrayInputStream("".getBytes());
        when(firstSocket.getInputStream()).thenReturn(stubInputStream);
        when(secondSocket.getInputStream()).thenReturn(stubInputStream);


    }

    @Test
    public void serverStartTest() throws Exception {
        server = new SocketMessageServer();


        //Step 1: first socket1 worker get message subscribe
        Message subscribeMessage = new TextMessage(new Address("a"), new Address("b"), SUBSCRIBE);
        String serializedSubscribeMessage = mapper.writeValueAsString(subscribeMessage) + "\n\n";
        InputStream firstSocketInputStream = new ByteArrayInputStream(serializedSubscribeMessage.getBytes());
        when(firstSocket.getInputStream()).thenReturn(firstSocketInputStream);

        //Step 2: socket2 get message to socket1
        Message messageToSocket1 = new ObjectMessage(new Address("b"), new Address("a"), "test");
        String serializedToSocket1Message = mapper.writeValueAsString(messageToSocket1) + "\n\n";
        InputStream secondSocketInputStream = new ByteArrayInputStream(serializedToSocket1Message.getBytes());
        when(secondSocket.getInputStream()).thenReturn(secondSocketInputStream);

        //Internally server redirect this message to server
        server.start();

        Message recievedMessage = mapper.readValue(mockOutputStreamOne.toString(), Message.class);
        Assert.assertEquals("test", ((ObjectMessage)recievedMessage).getBody().toString());
    }
}
