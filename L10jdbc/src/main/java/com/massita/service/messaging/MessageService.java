package com.massita.service.messaging;

import com.massita.service.messaging.message.Address;
import com.massita.service.messaging.message.Message;

public interface MessageService {

    void sendMessage(Message message);

    void subscribe(Address address, MessageListener listener);

    void unsubscribe(Address address, MessageListener listener);

    void start();

    void stop();
}
