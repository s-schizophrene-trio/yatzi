package ch.juventus.yatzi.network;

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
                    clientPoolExecutor.submit(new ClientTask(clientSocket));
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
            this.serverPoolExecutor.shutdown();
            this.clientPoolExecutor.shutdown();
        }  catch (Exception e) {
            LOGGER.error("failed to stop server because of: {}", e.getMessage());
        }
    }

    /**
     * Each new Client will have its own ClientTask. The Connection to the client will be always open.
     */
    private class ClientTask implements Runnable {

        Socket socket;

        private ClientTask(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try (
                    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))
            ) {
                LOGGER.debug("got a client with ip {} on port {}", socket.getInetAddress(), socket.getLocalPort());

                String fromClient, fromServer;

                out.println("REGISTRATION_SUCCESS");

                while ((fromClient = in.readLine()) != null) {
                    LOGGER.debug("got a message from a client: {}", fromClient);
                }

                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
                LOGGER.error("Failed to process incoming or outgoing traffic to client");
            }
        }
    }

}
