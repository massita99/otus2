package com.massita.workers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.massita.annotation.Blocks;
import com.massita.service.messaging.message.Message;
import com.massita.utils.serealizers.SpecialObjectMapperFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class SocketMessageWorker implements MessageWorker {
    private static final int WORKER_COUNT = 2;
    private static final ObjectMapper OBJECT_MAPPER = SpecialObjectMapperFactory.getMessageObjectMapper();

    private final ExecutorService executorService;
    private final Socket socket;

    private final BlockingQueue<Message> output = new LinkedBlockingQueue<>();
    private final BlockingQueue<Message> input = new LinkedBlockingQueue<>();

    public SocketMessageWorker(Socket socket) {
        this.socket = socket;
        executorService = Executors.newFixedThreadPool(WORKER_COUNT);
    }

    public void init() {
        executorService.execute(this::sendMessage);
        executorService.execute(this::receiveMessage);
    }

    @Override
    public Message pool() {
        return input.poll();
    }

    @Override
    public void send(Message message) {
        output.add(message);
    }

    @Override
    public Message take() throws InterruptedException {
        return input.take();
    }

    @Override
    public void close() throws IOException {
        socket.close();
        executorService.shutdown();
    }

    @Blocks
    private void sendMessage(){
        try (PrintWriter out = new PrintWriter(socket.getOutputStream(), true)){
            while (socket.isConnected()){
                Message message = output.take();
                String json = OBJECT_MAPPER.writeValueAsString(message);
                out.println(json);
                out.println();
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Blocks
    private void receiveMessage(){
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))){
            String inputLine;
            StringBuilder stringBuilder = new StringBuilder();
            while ((inputLine = reader.readLine()) != null){
                stringBuilder.append(inputLine);
                if (inputLine.isEmpty()){
                    String json = stringBuilder.toString();
                    Message message = getMessageFromJson(json);
                    input.add(message);
                    stringBuilder = new StringBuilder();
                }
            }
        }  catch (Exception e) {
            e.printStackTrace();
        }

    }

    private Message getMessageFromJson(String json) throws IOException {
        return OBJECT_MAPPER.readValue(json, Message.class);

    }
}
