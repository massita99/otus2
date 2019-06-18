package com.massita.utils.serealizers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.massita.model.UserDataSet;
import com.massita.service.messaging.message.Address;
import com.massita.service.messaging.message.DbMessage;
import com.massita.service.messaging.message.Message;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class SpecialObjectMapperFactoryTest {

    @Test
    public void messageObjectMapperTest() throws IOException {

        ObjectMapper objectMapper = SpecialObjectMapperFactory.getMessageObjectMapper();
        Message message= new DbMessage(new Address(), new Address(), DbMessage.DbMessageType.SAVE, new UserDataSet("123", 12), UserDataSet.class);

        String serializedMessage = objectMapper.writeValueAsString(message);

        Message deserealizedmMessage = objectMapper.readValue(serializedMessage, Message.class);

        Assert.assertTrue(deserealizedmMessage instanceof DbMessage);
        Assert.assertTrue(((DbMessage) deserealizedmMessage).getBody() instanceof UserDataSet);
        Assert.assertEquals(((DbMessage) message).getBody(), ((DbMessage) deserealizedmMessage).getBody());

    }
}