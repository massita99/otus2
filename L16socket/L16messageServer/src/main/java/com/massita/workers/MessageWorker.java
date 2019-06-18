package com.massita.workers;

import com.massita.annotation.Blocks;
import com.massita.service.messaging.message.Message;

import java.io.IOException;

public interface MessageWorker {

    Message pool();

    void send(Message message);

    @Blocks
    Message take() throws InterruptedException;

    void close() throws IOException;
}
