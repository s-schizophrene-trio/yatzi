package ch.juventus.yatzi.network;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    private ServerSocket serverSocket;
    private Socket clientSocket;

    public void start(int port) {

        // create a thread pool (maximum clients to connect is 8 including the current player)
        final ExecutorService clientHandlerPool = Executors.newFixedThreadPool(8);

        Runnable serverTask = () -> {
            try {
                LOGGER.info("starting server socket on port {}", port);
                this.serverSocket = new ServerSocket(port);
                LOGGER.debug("Waiting for clients to connect...");
                while (true) {
                    this.clientSocket = serverSocket.accept();
                    clientHandlerPool.submit(new ClientTask(clientSocket));
                }
            } catch (Exception e) {
                LOGGER.error("unprocessable client request {}", e.getMessage());
                e.printStackTrace();
            }
        };

        // start the server thread
        Thread serverThread = new Thread(serverTask);
        serverThread.start();
    }

    private class ClientTask implements Runnable {
        private final Socket clientSocket;

        private ClientTask(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {
           LOGGER.debug("Got a client !");
            // Do whatever required to process the client's request

            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void stop() {
        LOGGER.debug("stop the server socket");
        try {
            clientSocket.close();
            serverSocket.close();
        } catch (NullPointerException e) {
            LOGGER.debug("found no server to stop");
        } catch (Exception e) {
            LOGGER.error("failed to stop server because of: {}", e.getMessage());
        }
    }
}
