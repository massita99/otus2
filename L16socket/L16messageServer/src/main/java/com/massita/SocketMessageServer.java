package com.massita;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.massita.annotation.Blocks;
import com.massita.service.messaging.message.Address;
import com.massita.service.messaging.message.Message;
import com.massita.service.messaging.message.TextMessage;
import com.massita.workers.MessageWorker;
import com.massita.workers.SocketMessageWorker;
import lombok.SneakyThrows;
import org.springframework.beans.factory.InitializingBean;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The {@link SocketMessageServer} is server that can transfer Messages
 * between diffent services by Socket
 */
public class SocketMessageServer implements InitializingBean {

    private static final int PORT = 5050;
    private static final int RESEND_DELAY_MS = 500;
    private static final int THREADS_COUNT = 1;
    public static String SUBSCRIBE = "subscribe";
    public static String UNSUBSCRIBE = "unsubscribe";

    private final static Logger logger = Logger.getLogger(SocketMessageServer.class.getName());


    private final ExecutorService executorService;
    private final ScheduledExecutorService secondaryExcecutorService;
    private final List<MessageWorker> workers;
    private Multimap<String, MessageWorker> addressMessageListeners = HashMultimap.create();
    private BlockingQueue<Message> unAddressedMessages = new LinkedBlockingQueue<>();


    public SocketMessageServer() {
        executorService = Executors.newFixedThreadPool(THREADS_COUNT);
        secondaryExcecutorService = Executors.newScheduledThreadPool(1);
        workers = new CopyOnWriteArrayList<>();
    }


    @Blocks
    public void start() {

        executorService.submit(this::work);
        //Once in RESEND_DELAY_MS try to resend messages
        secondaryExcecutorService.scheduleAtFixedRate(this::unAddressedMessageRetry, 0, RESEND_DELAY_MS, TimeUnit.MICROSECONDS);
        executorService.submit(this::unAddressedMessageRetry);

        System.out.println("Server started");

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (!executorService.isShutdown()) {
                Socket socket = serverSocket.accept();//blocks
                SocketMessageWorker worker = new SocketMessageWorker(socket);
                worker.init();
                workers.add(worker);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Blocks
    @SneakyThrows
    private void unAddressedMessageRetry() {
        Message message = unAddressedMessages.poll();
        while (message != null) {
            Address addressTo = message.getTo();
            if (addressMessageListeners.containsKey(addressTo.getId())) {
                redirectMessageToAddress(message);
            } else {
                Thread.sleep(RESEND_DELAY_MS / 2);
                if (addressMessageListeners.containsKey(addressTo.getId())) {
                    redirectMessageToAddress(message);
                } else {
                    logger.log(Level.WARNING, "There are no listeners for address: " + addressTo +
                            ", Message " + message.toString() + " will be removed");
                }
            }
            message = unAddressedMessages.poll();
        }
    }

    public void stop() {
        executorService.shutdown();
    }

    @SuppressWarnings("InfiniteLoopStatement")
    private void work() {
        while (true) {
            for (MessageWorker worker : workers) {
                Message message = worker.pool();
                if (message != null) {
                    if (isSubscribeMessage(message)) {
                        handleSubscribeMessage(worker, (TextMessage) message);
                    } else {
                        redirectMessageToAddress(message);
                    }
                }
            }
        }
    }

    private boolean isSubscribeMessage(Message message) {
        if (!message.getClass().isAssignableFrom(TextMessage.class)) {
            return false;
        }
        TextMessage textMessage = (TextMessage) message;

        return SUBSCRIBE.equals(textMessage.getBody()) || UNSUBSCRIBE.equals(textMessage.getBody());
    }

    private void handleSubscribeMessage(MessageWorker worker, TextMessage message) {
        TextMessage textMessage = message;
        if (SUBSCRIBE.equals(textMessage.getBody())) {
            addressMessageListeners.put(textMessage.getFrom().getId(), worker);
        }
        if (UNSUBSCRIBE.equals(textMessage.getBody())) {
            addressMessageListeners.remove(textMessage.getFrom().getId(), worker);
        }
    }

    private void redirectMessageToAddress(Message message) {
        Address addressTo = message.getTo();
        Collection<MessageWorker> workersTo = addressMessageListeners.get(addressTo.getId());
        if (workersTo.isEmpty()) {
            logger.log(Level.INFO, "There are no listeners for address: " + addressTo);
            //Save message for second try;
            unAddressedMessages.add(message);
        } else {

            chooseWorker(workersTo).send(message);
        }
    }

    //Naive implementation of balancing
    private MessageWorker chooseWorker(Collection<MessageWorker> workers) {
        return workers.stream()
                .findAny().get();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.start();
    }
}

