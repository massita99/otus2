package com.massita.workers;

import java.io.IOException;
import java.net.Socket;

public class ClientSocketMessageWorker extends SocketMessageWorker {

    private final Socket socket;

    public ClientSocketMessageWorker(String host, int port) throws IOException {
        this(new Socket(host, port));
    }

    public ClientSocketMessageWorker(Socket socket) {
        super(socket);
        this.socket = socket;
    }

    @Override
    public void close() throws IOException {
        super.close();
        socket.close();
    }
}
