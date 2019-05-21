package ch.juventus.yatzi.network.server;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Server {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    private final ExecutorService clientPoolExecutor;
    private final ExecutorService serverPoolExecutor;
    @Getter
    private ServerSocket serverSocket;
    private Socket clientSocket;
    @Getter
    private Integer localPort;
    @Getter
    @Setter
    private Boolean isRunning;

    /**
     * Initializes a new Server Object and their Executor Services.
     */
    public Server() {

        BasicThreadFactory clientPoolFactory = new BasicThreadFactory.Builder()
                .namingPattern("Server Socket #%d")
                .daemon(true)
                .priority(Thread.MAX_PRIORITY)
                .build();

        BasicThreadFactory serverPoolFactory = new BasicThreadFactory.Builder()
                .namingPattern("Incoming Client Socket #%d")
                .daemon(true)
                .priority(Thread.MAX_PRIORITY)
                .build();

        this.clientPoolExecutor = Executors.newFixedThreadPool(7, clientPoolFactory);
        this.serverPoolExecutor = Executors.newSingleThreadExecutor(serverPoolFactory);

        this.isRunning = true;
    }

    /**
     * Starts a new Server Socket Thread. The Thread will create a new Thread for each Client.
     *
     * @param port The port, the server should run.
     */
    public void start(int port) {

        this.localPort = port;

        Runnable serverTask = () -> {
            try {
                LOGGER.info("starting server socket on port {}", port);
                this.serverSocket = new ServerSocket(port);
                LOGGER.debug("Waiting for clients to connect...");

                while (this.isRunning) {
                    this.clientSocket = serverSocket.accept();
                    clientPoolExecutor.submit(new ClientHandler(clientSocket));
                }
            } catch (Exception e) {
                LOGGER.error("unprocessable client request {}", e.getMessage());
                e.printStackTrace();
            }
        };

        // start server task
        serverPoolExecutor.submit(new Thread(serverTask));
    }

    /**
     * Stops all running Threads
     */
    public void stop() {
        LOGGER.debug("stop the server socket");
        try {
            this.serverPoolExecutor.shutdownNow();
            this.clientPoolExecutor.shutdownNow();

            this.serverPoolExecutor.awaitTermination(1000, TimeUnit.MILLISECONDS);
            this.clientPoolExecutor.awaitTermination(1000, TimeUnit.MILLISECONDS);
        }  catch (Exception e) {
            LOGGER.error("failed to stop server because of: {}", e.getMessage());
        }
    }

}
