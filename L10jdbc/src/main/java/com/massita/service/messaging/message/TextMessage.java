package com.massita.service.messaging.message;

import lombok.Getter;

public class TextMessage extends Message {

    @Getter
    private String body;

    public TextMessage(Address from, Address to, String body) {
        super(from, to);
        this.body = body;
    }
}
