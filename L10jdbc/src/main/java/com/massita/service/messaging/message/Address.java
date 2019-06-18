package com.massita.service.messaging.message;

import lombok.Getter;

import java.util.Random;

/**
 * Define address that identify {@link Message} receiver and sender
 */
public class Address {

    private static final Random ID_GENERATOR = new Random();

    @Getter
    private final String id;

    public Address(){
        id = String.valueOf(ID_GENERATOR.nextInt(10000));
    }

    public Address(String id) {
        this.id = id;
    }
}
