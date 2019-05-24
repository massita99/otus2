package com.massita.service.messaging.message;

import lombok.Getter;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Define address that identify {@link Message} receiver and sender
 */
public class Address {

    private static final AtomicInteger ID_GENERATOR = new AtomicInteger();

    @Getter
    private final String id;

    public Address(){
        id = String.valueOf(ID_GENERATOR.getAndIncrement());
    }

    public Address(String id) {
        this.id = id;
    }
}
