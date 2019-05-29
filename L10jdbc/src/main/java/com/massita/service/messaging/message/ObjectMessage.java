package com.massita.service.messaging.message;

import lombok.Getter;
import lombok.Setter;

public class ObjectMessage implements Message {

    @Getter
    private Object body;

    @Getter
    @Setter
    private Address from;
    @Getter
    private Address to;

    public ObjectMessage(Address from, Address to, Object body) {
        this.from = from;
        this.to = to;
        this.body = body;
    }
}
