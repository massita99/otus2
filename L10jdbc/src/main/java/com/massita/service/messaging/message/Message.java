package com.massita.service.messaging.message;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public abstract class Message {

    @Getter
    private Address from;
    @Getter
    private Address to;

}
