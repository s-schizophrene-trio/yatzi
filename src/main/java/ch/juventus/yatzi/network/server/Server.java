package ch.juventus.yatzi.network.server;

import ch.juventus.yatzi.network.handler.MessageHandler;
import ch.juventus.yatzi.network.helper.Commands;
import ch.juventus.yatzi.network.model.Message;
import ch.juventus.yatzi.network.model.Transfer;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Server {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    private final ExecutorService clientPoolExecutor;
    private final ExecutorService serverPoolExecutor;
    private final ExecutorService messageHandlerPool;

    @Getter
    private ServerSocket serverSocket;
    private Socket clientSocket;

    @Getter
    private Integer localPort;

    @Getter
    @Setter
    private Boolean isRunning;

    @Getter
    List<ClientHandler> clients;

    private MessageHandler messageHandler;
    private Boolean listen = true;

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

        BasicThreadFactory messagePoolFactory = new BasicThreadFactory.Builder()
                .namingPattern("Server Message Handler #%d")
                .daemon(true)
                .priority(Thread.MAX_PRIORITY)
                .build();



        this.clientPoolExecutor = Executors.newFixedThreadPool(7, clientPoolFactory);
        this.serverPoolExecutor = Executors.newSingleThreadExecutor(serverPoolFactory);
        this.messageHandlerPool = Executors.newSingleThreadExecutor( messagePoolFactory);

        this.messageHandler = new MessageHandler();
        this.clients = new ArrayList<>();

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

                listenToClients();

                while (this.isRunning) {
                    this.clientSocket = serverSocket.accept();
                    ClientHandler ch = new ClientHandler(clientSocket, messageHandler);
                    clients.add(ch);
                    clientPoolExecutor.submit(ch);
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
     * Listens to the Input Message Queue from the Server
     */
    public void listenToClients() {

        Runnable messageListener = () -> {

            ObjectMapper objectMapper = new ObjectMapper();
            LOGGER.debug("start message handler for server messages..");

            while(listen) {
                try {
                    if (!messageHandler.getQueue().isEmpty()) {

                        Transfer transfer = messageHandler.getQueue().poll();
                        LOGGER.debug("server-message handler [incoming]: {}", transfer.toString());

                        if (transfer.getFunction().contains(Commands.NEW_PLAYER)) {
                            // tell the main client, that a new user is registered
                            sendMessageToMainClient(transfer);

                            // tell the other clients, the have to wait until the main client gives the OK
                            broadcastMessage(new Transfer(Commands.WAIT_FOR_GAME_READY), false);
                        }
                    }
                } catch (Exception e) {
                    LOGGER.error("failed to extract the last element from queue");
                }

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        Thread messageListenerTask = new Thread(messageListener);
        messageHandlerPool.submit(messageListenerTask);
    }

    public void sendMessageToMainClient(Transfer transfer) {
        this.clients.get(0).send(transfer);
    }

    /**
     * Sends a Transfer Message to all Clients (without the main client)
     * @param transfer
     */
    public void broadcastMessage(Transfer transfer, Boolean includeServerClient) {

        int startIndex = includeServerClient ? 0 : 1;

        for (int i = startIndex; i < clients.size(); i++) {
            clients.get(i).send(transfer);
        }
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
