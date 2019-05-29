package com.massita.service.messaging.message;

import lombok.Getter;
import lombok.Setter;

public class TextMessage implements Message {

    @Getter
    private String body;

    @Getter
    @Setter
    private Address from;
    @Getter
    private Address to;

    public TextMessage(Address from, Address to, String body) {
        this.from = from;
        this.to = to;
        this.body = body;
    }
}
