package com.massita.service.messaging.message;

/**
 * Define message between two services
 * Contain {@link Address} of Sender and Receiver
 */

public interface Message {

    void setFrom(Address from);

    Address getFrom();

    Address getTo();

}
