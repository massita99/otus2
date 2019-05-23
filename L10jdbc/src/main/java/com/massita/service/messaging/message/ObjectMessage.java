package com.massita.service.messaging.message;

import lombok.Getter;

public class ObjectMessage extends Message {

    @Getter
    private Object body;

    public ObjectMessage(Address from, Address to, Object body) {
        super(from, to);
        this.body = body;
    }
}
