package com.massita.utils.serealizers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.massita.service.messaging.message.DbMessage;
import com.massita.service.messaging.message.ObjectMessage;
import com.massita.service.messaging.message.TextMessage;

public class SpecialObjectMapperFactory {

    private static ObjectMapper messageObjectMapper;

    private static void initMessageObjectMapper() {
        messageObjectMapper = new ObjectMapper();
        messageObjectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        messageObjectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        messageObjectMapper.addMixIn(DbMessage.class, DbMessageMixin.class);
        messageObjectMapper.addMixIn(ObjectMessage.class, ObjectMessageMixin.class);
        messageObjectMapper.addMixIn(TextMessage.class, TextMessageMixin.class);
    }

    public static ObjectMapper getMessageObjectMapper() {
        if (messageObjectMapper == null) {
            initMessageObjectMapper();
        }
        return messageObjectMapper;
    }

}
