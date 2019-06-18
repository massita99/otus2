package com.massita;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.massita.service.messaging.MessageListener;
import com.massita.service.messaging.MessageService;
import com.massita.service.messaging.message.Address;
import com.massita.service.messaging.message.Message;
import com.massita.service.messaging.message.TextMessage;
import com.massita.workers.ClientSocketMessageWorker;
import org.springframework.beans.factory.InitializingBean;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.massita.SocketMessageServer.SUBSCRIBE;
import static com.massita.SocketMessageServer.UNSUBSCRIBE;

/**
 * The {@link SocketMessageClient} messageService client that should be
 * instantiate on client to handle message from/to {@link SocketMessageServer}
 */
public class SocketMessageClient implements MessageService, InitializingBean {

    private final static Logger logger = Logger.getLogger(SocketMessageClient.class.getName());

    private static final String HOST = "localhost";
    private static final int PORT = 5050;
    private static final int THREADS_COUNT = 1;


    private ClientSocketMessageWorker clientWorker;
    private final ExecutorService executorService;
    private Multimap<String, MessageListener> addressMessageListeners;


    public SocketMessageClient() throws IOException {
        executorService = Executors.newFixedThreadPool(THREADS_COUNT);
        addressMessageListeners = HashMultimap.create();
        clientWorker = new ClientSocketMessageWorker(HOST, PORT);


    }

    @Override
    public void sendMessage(Message message) {
        clientWorker.send(message);
    }

    @Override
    public void subscribe(Address address, MessageListener listener) {
        addressMessageListeners.put(address.getId(), listener);
        clientWorker.send(new TextMessage(address, null, SUBSCRIBE));

    }

    @Override
    public void unsubscribe(Address address, MessageListener listener) {
        addressMessageListeners.remove(address.getId(), listener);
        clientWorker.send(new TextMessage(address, null, UNSUBSCRIBE));


    }

    @Override
    public void start() {
        executorService.submit(this::work);
        clientWorker.init();
    }

    @SuppressWarnings("InfiniteLoopStatement")
    private void work() {
        while (true) {
            if (clientWorker != null) {
                Message message = null;
                try {
                    message = clientWorker.take();
                    redirectMessageToAddress(message);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    logger.log(Level.INFO, "Thread interrupted");

                }

            }
        }
    }

    private void redirectMessageToAddress(Message message) {
        addressMessageListeners.get(message.getTo().getId()).forEach(l -> l.onMessage(message));
    }


    @Override
    public void stop() {
        try {
            clientWorker.close();
        } catch (IOException e) {
            e.printStackTrace();
            logger.log(Level.WARNING, "Error during socket close");

        }
        executorService.shutdown();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.start();
    }
}
