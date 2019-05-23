package com.massita.service.messaging;


import com.massita.service.messaging.message.Address;
import com.massita.service.messaging.message.Message;
import lombok.Setter;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Define a messaging service that broadcast messages for listeners
 */
public class MessageService {
    private final static Logger logger = Logger.getLogger(MessageService.class.getName());
    private static final int DEFAULT_POOL_SIZE = 4;

    private LinkedBlockingQueue<Message> messages;
    private MultiValuedMap<Address, MessageListener> addressMessageListeners;
    private ScheduledExecutorService executorService;

    @Setter
    private int poolSize = DEFAULT_POOL_SIZE;

    public MessageService() {
        this.messages = new LinkedBlockingQueue<>();
        this.addressMessageListeners = new ArrayListValuedHashMap<>();
    }

    public void sendMessage(Message message) {
        if (!addressMessageListeners.containsKey(message.getTo())) {
            logger.log(Level.WARNING, "There are no listeners for address: " + message.getTo());
        } else {
            messages.add(message);
        }
    }

    public void subscribe(Address address, MessageListener listener){
        addressMessageListeners.put(address, listener);
    }

    public void unsubscribe(Address address, MessageListener listener){
        addressMessageListeners.removeMapping(address, listener);
    }


    public void start() {
        executorService = Executors.newScheduledThreadPool(poolSize);
        Runnable messageListener = () -> {
            try {
                Message message = messages.take();
                //Broadcast message
                addressMessageListeners.get(message.getTo()).forEach(l -> l.onMessage(message));

            } catch (InterruptedException e) {
                logger.log(Level.INFO, "Thread interrupted");
                            }
        };
        executorService.scheduleAtFixedRate(messageListener, 0, 5,  TimeUnit.MILLISECONDS);
    }

    public void stop() {
        executorService.shutdown();
    }
}