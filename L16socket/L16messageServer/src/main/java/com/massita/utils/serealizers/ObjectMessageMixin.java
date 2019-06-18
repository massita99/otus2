package com.massita.utils.serealizers;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.massita.service.messaging.message.Address;

public class ObjectMessageMixin {

    @JsonCreator
    public ObjectMessageMixin(@JsonProperty("from") Address from,
                              @JsonProperty("to") Address to,
                              @JsonProperty("body") Object body) {
    }
}
