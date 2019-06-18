package ch.juventus.yatzi.network.client;

import ch.juventus.yatzi.config.ApplicationConfig;
import ch.juventus.yatzi.network.handler.MessageHandler;
import ch.juventus.yatzi.network.model.Transfer;
import ch.juventus.yatzi.ui.interfaces.ViewContext;
import lombok.Getter;
import org.aeonbits.owner.ConfigFactory;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Client {

    private static final Integer MAX_RECONNECTS = 30;
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    private final ExecutorService clientConnectionExecutor;
    private final ExecutorService clientExecutor;
    private ApplicationConfig config;

    @Getter
    private Socket clientSocket;

    @Getter
    private String remoteIp;

    @Getter
    private Integer remotePort;

    @Getter
    private UUID userId;

    private Integer reconnects;

    @Getter
    private MessageHandler messageHandler;

    private ClientTask clientTask;

    /**
     * Creates a new Client and initialize all the static values.
     *
     * @param remoteIp       Network IP of the Server to connect
     * @param remotePort     Network Port of the Server to connect
     * @param userId         The User ID of the current playing user
     * @param messageHandler An instance of the message handler, where the client task can write its messages in
     */
    public Client(String remoteIp, Integer remotePort, UUID userId, MessageHandler messageHandler) {
        this.remoteIp = remoteIp;
        this.remotePort = remotePort;
        this.userId = userId;
        this.reconnects = 0;
        this.messageHandler = messageHandler;
        this.config = ConfigFactory.create(ApplicationConfig.class);


        BasicThreadFactory clientPoolFactory = new BasicThreadFactory.Builder()
                .namingPattern("Local Client #%d")
                .daemon(true)
                .priority(Thread.MAX_PRIORITY)
                .build();

        BasicThreadFactory clientExecutorFactory = new BasicThreadFactory.Builder()
                .namingPattern("Local Client Handler #%d")
                .daemon(true)
                .priority(Thread.MAX_PRIORITY)
                .build();

        clientExecutor = Executors.newSingleThreadExecutor(clientPoolFactory);
        clientConnectionExecutor = Executors.newSingleThreadExecutor(clientExecutorFactory);
    }

    /**
     * Connects to a Server Socket. If the Server is not reachable, the client will sleep for one second and try again
     * until the MAX_RECONNECTS is reached.
     *
     * @param viewContext An instance of a ViewContext to access the yatzi game
     */
    public void connect(ViewContext viewContext) {

        Runnable clientConnectTask = () -> {
            if (reconnects < MAX_RECONNECTS) {
                if (remoteIp != null && remotePort != null) {
                    try {
                        clientSocket = new Socket();
                        clientSocket.connect(new InetSocketAddress(remoteIp, remotePort), 30000);

                        LOGGER.debug("client - remote address is {}", clientSocket.getRemoteSocketAddress().toString());

                        clientTask = new ClientTask(clientSocket,
                                viewContext,
                                viewContext.getYatziGame().getUserMe().getUserId(),
                                messageHandler);

                        clientExecutor.submit(clientTask);
                    } catch (ConnectException e) {
                        LOGGER.debug("failed to establish a connection to server {}", e.getMessage());
                        try {
                            // calculate timeout in ms
                            Integer timeoutInMs = config.clientTimeout() * 1000;
                            Thread.sleep(timeoutInMs / MAX_RECONNECTS);
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }
                        reconnects++;
                        LOGGER.debug("try to reconnect({}) to server...", reconnects);
                        connect(viewContext);
                    } catch (IOException e) {
                        LOGGER.error("failed to establish server connection: {}", e.getMessage());
                    }
                } else {
                    LOGGER.error("please provide the remote ip and port to connect to a server socket");
                }
            } else {
                LOGGER.error("aborted connection. reached max reconnect tries of {} to connect to the server", reconnects);
            }
        };

        clientConnectionExecutor.submit(new Thread(clientConnectTask));
    }

    /**
     * Sends a message to the server
     *
     * @param transfer A transfer object to send
     */
    public void send(Transfer transfer) {
        clientTask.send(transfer);
    }

    /**
     * Sign-Out from Server and Stop the local client.
     */
    public void stop() {
        try {
            clientTask.stop();
            clientConnectionExecutor.shutdown();
        } catch (Exception e) {
            LOGGER.error("failed to stop connection to server {}", e.getMessage());
        }
    }
}
