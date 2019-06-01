package ch.juventus.yatzi.network.server;

import ch.juventus.yatzi.engine.YatziGame;
import ch.juventus.yatzi.network.handler.MessageHandler;
import ch.juventus.yatzi.network.helper.Commands;
import ch.juventus.yatzi.network.model.Transfer;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
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
import java.util.stream.Collectors;

import static ch.juventus.yatzi.network.helper.Commands.CLIENT_READY;
import static ch.juventus.yatzi.network.helper.Commands.PLAYER_NEW;

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

    @Getter
    private UUID serverUserId;

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
    public void start(int port, YatziGame yatziGame) {

        this.localPort = port;
        this.serverUserId = yatziGame.getUserService().getLocalUser().getUserId();

        Runnable serverTask = () -> {
            try {
                LOGGER.info("starting server socket on port {}", port);
                this.serverSocket = new ServerSocket(port);
                LOGGER.debug("Waiting for clients to connect...");

                listenToClients(yatziGame);

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
    public void listenToClients(YatziGame yatziGame) {

        Runnable messageListener = () -> {

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

            LOGGER.debug("start message handler for server messages..");

            while(listen) {
                try {
                    if (!messageHandler.getQueue().isEmpty()) {

                        Transfer transfer = messageHandler.getQueue().poll();
                        LOGGER.debug("server-message handler [incoming]: {}", transfer.toString());

                        switch (transfer.getFunction()) {
                            case PLAYER_NEW:
                                // tell the main client, that a new user is registered
                                sendMessageToMainClient(transfer);
                                // tell the other clients, the have to wait until the main client gives the OK
                                broadcastMessage(new Transfer(Commands.WAIT_FOR_GAME_READY), false);
                                break;
                            case CLIENT_READY:
                                // Trigger the client to start the party
                                String game = objectMapper.writeValueAsString(yatziGame);
                                sendMessageToClientByUserId(transfer.getSender(), new Transfer(serverUserId, Commands.ROUND_START, game));
                                break;
                        }
                    }
                } catch (Exception e) {
                    LOGGER.error("failed to extract the last element from queue: {}", e.getMessage());
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

    /**
     * Sends a message to the main client (local client in server mode)
     * @param transfer Transfer object to transfer
     */
    public void sendMessageToMainClient(Transfer transfer) {
        this.clients.get(0).send(transfer);
    }

    /**
     * Sends a message to a client identified by its user id
     * @param userId The unique id ot the user
     * @param transfer The transfer object
     */
    public void sendMessageToClientByUserId(UUID userId, Transfer transfer) {

       List<ClientHandler> clientHandlers =  clients.stream().filter(ch -> ch.getOwner().equals(userId)).collect(Collectors.toList());

       if (!clientHandlers.isEmpty()) {
           // client found. the message will be sent
           ClientHandler ch = clientHandlers.get(0);
           ch.send(transfer);
       } else {
           // client not found.
           LOGGER.warn("client with the user id {} was not found on server", userId);
       }
    }

    /**
     * Sends a Transfer Message to all Clients (without the main client)
     * @param transfer The transfer object
     * @param includeServerClient  Should the local client also be informed? sometimes the server mode has more
     *                             privileges and different ui components.
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

            this.isRunning = false;

            for (ClientHandler ch : clients) {
                ch.stop();
            }

            this.serverPoolExecutor.shutdownNow();
            this.clientPoolExecutor.shutdownNow();

            this.serverPoolExecutor.awaitTermination(500, TimeUnit.MILLISECONDS);
            this.clientPoolExecutor.awaitTermination(500, TimeUnit.MILLISECONDS);
        }  catch (Exception e) {
            LOGGER.error("failed to stop server because of: {}", e.getMessage());
        }
    }

}
