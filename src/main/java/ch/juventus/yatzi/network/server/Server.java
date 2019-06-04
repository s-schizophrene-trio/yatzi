package ch.juventus.yatzi.network.server;

import ch.juventus.yatzi.engine.YatziGame;
import ch.juventus.yatzi.network.handler.MessageHandler;
import ch.juventus.yatzi.network.helper.Commands;
import ch.juventus.yatzi.network.model.Transfer;
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

import static ch.juventus.yatzi.network.helper.Commands.*;

public class Server {

    private static final Integer MAX_CLIENTS = 5;
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
    private final ExecutorService clientPoolExecutor;
    private final ExecutorService serverPoolExecutor;
    private final ExecutorService messageHandlerPool;
    @Getter
    List<ClientHandler> clients;
    @Getter
    private ServerSocket serverSocket;
    private Socket clientSocket;
    @Getter
    private Integer localPort;
    @Getter
    @Setter
    private Boolean isRunning;
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
        this.messageHandlerPool = Executors.newSingleThreadExecutor(messagePoolFactory);

        this.messageHandler = new MessageHandler();
        this.clients = new ArrayList<>();

        this.isRunning = true;
    }

    /**
     * Starts a new Server Socket Thread. The Thread will create a new Thread for each Client.
     *
     * @param port The port, the server should run.
     * @param yatziGame An instance of the YatziGame.
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
     * @param yatziGame An instance of the YatziGame
     */
    public void listenToClients(YatziGame yatziGame) {

        Runnable messageListener = () -> {

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

            LOGGER.debug("start message handler for server messages..");

            while (listen) {
                try {
                    if (!messageHandler.getQueue().isEmpty()) {

                        Transfer transfer = messageHandler.getQueue().poll();
                        LOGGER.debug("server-message handler [incoming]: {}", transfer.toString());

                        switch (transfer.getFunction()) {
                            case PLAYER_NEW:

                                if (clients.size() <= MAX_CLIENTS) {
                                    // tell the main client, that a new user is registered
                                    sendMessageToClientByUserId(yatziGame.getUserMe().getUserId(), transfer);
                                    // tell the other clients, the have to wait until the main client gives the OK
                                    broadcastMessage(new Transfer(Commands.WAIT_FOR_GAME_READY), false);
                                } else {
                                    sendMessageToClientByUserId(transfer.getSender(), new Transfer(
                                            serverUserId,
                                            MAX_PLAYERS_REACHED
                                    ));
                                }

                                break;
                            case CLIENT_READY: // the client has loaded its board and is ready to get the updated game
                                // Trigger the client to start the party
                                String game = objectMapper.writeValueAsString(yatziGame);
                                sendMessageToClientByUserId(transfer.getSender(), new Transfer(serverUserId, Commands.ROUND_START, game));
                                break;
                            case GAME_CHANGED: // A client has updated the game
                                // Trigger the client to start the party
                                YatziGame changedGame = objectMapper.readValue(transfer.getBody(), YatziGame.class);
                                yatziGame.updateGame(changedGame);
                                yatziGame.nextUserInCircle();

                                // update all clients with the new game state
                                broadcastMessage(new Transfer(
                                                yatziGame.getUserMe().getUserId(),
                                                GAME_CHANGED,
                                                objectMapper.writeValueAsString(yatziGame)),
                                        true);
                                break;
                            case PLAYER_EXIT:

                                UUID playerToKick = transfer.getSender();
                                yatziGame.kickUserFromGame(playerToKick);
                                this.kickClientFromClientHandlers(playerToKick);

                                // tell the other clients, the have to wait until the main client gives the OK
                                broadcastMessage(new Transfer(
                                                serverUserId, Commands.GAME_CHANGED,
                                                objectMapper.writeValueAsString(yatziGame)),
                                        true);
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
     * Sends a message to a client identified by its user id
     *
     * @param userId   The unique id ot the user
     * @param transfer The transfer object
     */
    public void sendMessageToClientByUserId(UUID userId, Transfer transfer) {

        List<ClientHandler> clientHandlers = clients.stream().filter(ch -> ch.getOwner().equals(userId)).collect(Collectors.toList());

        if (!clientHandlers.isEmpty()) {
            // client found. the message will be sent
            ClientHandler ch = clientHandlers.get(0);
            ch.send(transfer);
        } else {
            // client not found.
            LOGGER.warn("client with the user id {} was not found on server", userId);
        }
    }

    public void kickClientFromClientHandlers(UUID userId) {

        ClientHandler clientHandlerToKick = null;

        for (ClientHandler ch : clients) {
            if (ch.getOwner().equals(userId)) {
                clientHandlerToKick = ch;
            }
        }

        if (clientHandlerToKick != null) {
            clients.remove(clientHandlerToKick);
        }
    }

    /**
     * Sends a Transfer Message to all Clients (without the main client)
     *
     * @param transfer            The transfer object
     * @param includeServerClient Should the local client also be informed? sometimes the server mode has more
     *                            privileges and different ui components.
     */
    public void broadcastMessage(Transfer transfer, Boolean includeServerClient) {

        if (includeServerClient) {
            clients.parallelStream()
                    .forEach(ch -> ch.send(transfer));
        } else {
            clients.parallelStream()
                    .filter(ch -> !ch.getOwner().equals(serverUserId))
                    .forEach(ch -> ch.send(transfer));
        }

    }

    /**
     * Stops all running Threads
     */
    public void stop() {
        LOGGER.debug("stop the server socket");
        try {

            // exit the game for all clients
            broadcastMessage(new Transfer(
                    serverUserId,
                    SERVER_EXIT
            ), false);

            this.isRunning = false;

            for (ClientHandler ch : clients) {
                ch.stop();
            }

            this.serverPoolExecutor.shutdownNow();
            this.clientPoolExecutor.shutdownNow();

            this.serverPoolExecutor.awaitTermination(500, TimeUnit.MILLISECONDS);
            this.clientPoolExecutor.awaitTermination(500, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            LOGGER.error("failed to stop server because of: {}", e.getMessage());
        }
    }

}
