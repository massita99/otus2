package com.massita.service.messaging.message;

import lombok.AllArgsConstructor;
import lombok.Getter;


/**
 * Define message between two services
 * Contain {@link Address} of Sender and Receiver
 */
@AllArgsConstructor
public abstract class Message {

    @Getter
    private Address from;
    @Getter
    private Address to;

}
