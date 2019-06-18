package com.massita.utils.serealizers;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.massita.service.messaging.message.Address;

public class TextMessageMixin {

    @JsonCreator
    public TextMessageMixin(@JsonProperty("from") Address from,
                            @JsonProperty("to") Address to,
                            @JsonProperty("body") String body) {
    }
}
