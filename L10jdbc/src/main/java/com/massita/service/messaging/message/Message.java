package com.massita.service.messaging.message;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


/**
 * Define message between two services
 * Contain {@link Address} of Sender and Receiver
 */
@AllArgsConstructor
public abstract class Message {

    @Getter
    @Setter
    private Address from;
    @Getter
    private Address to;

}
