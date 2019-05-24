package ch.juventus.yatzi.network.client;

import ch.juventus.yatzi.network.handler.MessageHandler;
import ch.juventus.yatzi.network.model.Transfer;
import ch.juventus.yatzi.ui.interfaces.ViewContext;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Client {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    private static final Integer MAX_RECONNECTS = 30;

    private final ExecutorService clientConnectionExecutor;
    private final ExecutorService clientExecutor;

    @Getter
    private Socket clientSocket;

    @Getter
    private String remoteIp;

    @Getter
    private Integer remotePort;

    @Getter
    private UUID userId;

    private Integer reconnects;

    @Getter @Setter
    MessageHandler messageHandler;

    /**
     * Creates a new Client and initialize all the static values.
     * @param remoteIp Network IP of the Server to connect
     * @param remotePort Network Port of the Server to connect
     * @param userId The User ID of the current playing user
     */
    public Client(String remoteIp, Integer remotePort, UUID userId, MessageHandler messageHandler) {
        this.remoteIp = remoteIp;
        this.remotePort = remotePort;
        this.userId = userId;
        this.reconnects = 0;
        this.messageHandler = messageHandler;

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
     */
    public void connect(ViewContext viewContext) {

        Runnable clientConnectTask = () -> {
            if (reconnects < MAX_RECONNECTS) {
                if (remoteIp != null && remotePort != null) {
                    try {
                        clientSocket = new Socket();
                        clientSocket.connect(new InetSocketAddress(remoteIp, remotePort), 30000);

                        LOGGER.debug("client - remote address is {}", clientSocket.getRemoteSocketAddress().toString());
                        clientExecutor.submit(new ClientTask(clientSocket,
                                viewContext,
                                viewContext.getYatziGame().getUserMe().getUserId(),
                                messageHandler)
                        );
                    } catch (ConnectException e) {
                        LOGGER.debug("failed to establish a connection to server {}", e.getMessage());
                        try {
                            Thread.sleep(1000);
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

    public String sendMessage(String msg) {
        String response = "";
        try {
            Transfer t = new Transfer();
            t.setSender(getUserId());
        } catch (Exception e) {
            LOGGER.error("failed to send message to server: {}", e.getMessage());
        }
        return response;
    }

    public void sendAsyncMessage(String message) {
        Runnable sendMessageTask = () -> {
            try {

                if (clientSocket == null) {
                    LOGGER.error("Fucking client socket is NULL!");
                }

                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                out.println(message);

            } catch (Exception e) {
                LOGGER.error("unprocessable client request {}", e.getMessage());
                e.printStackTrace();
            }
        };

        // start the server thread
        Thread sendMessageThread = new Thread(sendMessageTask);
        sendMessageThread.start();
    }

    public void stopConnection() {
        try {
            //in.close();
            //out.close();
            clientSocket.close();
        } catch (Exception e) {
            LOGGER.error("failed to stop connection to server", e.getMessage());
        }

    }

}
