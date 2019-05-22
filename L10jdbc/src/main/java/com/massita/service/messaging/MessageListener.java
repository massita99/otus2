package com.massita.service.messaging;

import com.massita.service.messaging.message.Message;

public interface MessageListener {

    void onMessage(Message message);
}
