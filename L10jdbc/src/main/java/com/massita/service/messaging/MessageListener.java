package com.massita.service.messaging;

import com.massita.service.messaging.message.Message;

/**
 * Declare message listener that will receive and handle messages from MessageService
 */
public interface MessageListener {

    void onMessage(Message message);
}
