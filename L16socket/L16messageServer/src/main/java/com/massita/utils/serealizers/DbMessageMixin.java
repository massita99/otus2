package com.massita.utils.serealizers;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.massita.service.messaging.message.Address;
import com.massita.service.messaging.message.DbMessage;

public class DbMessageMixin {

    @JsonCreator
    public DbMessageMixin(@JsonProperty("from") Address from,
                          @JsonProperty("to") Address to,
                          @JsonProperty("messageType") DbMessage.DbMessageType messageType,
                          @JsonProperty("body") Object body,
                          @JsonProperty("objectType") Class<?> objectType) {
    }
}
