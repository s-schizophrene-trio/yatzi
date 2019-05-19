package ch.juventus.yatzi.network;

import ch.juventus.yatzi.ui.enums.ScreenType;
import ch.juventus.yatzi.ui.enums.StatusType;
import ch.juventus.yatzi.ui.interfaces.ViewContext;
import javafx.application.Platform;
import lombok.Getter;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Client {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

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
    private String userId;

    private Integer reconnects;


    /**
     * Creates a new Client and initialize all the static values.
     * @param remoteIp Network IP of the Server to connect
     * @param remotePort Network Port of the Server to connect
     * @param userId The User ID of the current playing user
     */
    public Client(String remoteIp, Integer remotePort, String userId) {
        this.remoteIp = remoteIp;
        this.remotePort = remotePort;
        this.userId = userId;
        this.reconnects = 0;

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

        this.clientExecutor = Executors.newSingleThreadExecutor(clientPoolFactory);
        this.clientConnectionExecutor = Executors.newSingleThreadExecutor(clientExecutorFactory);
    }

    /**
     * Connects to a Server Socket. If the Server is not reachable, the client will sleep for one second and try again
     * until the MAX_RECONNECTS is reached.
     */
    public void connect(ViewContext viewContext) {

        Runnable clientConnectTask = () -> {
            if (reconnects < MAX_RECONNECTS) {
                if (remoteIp != null && this.remotePort != null) {
                    try {
                        this.clientSocket = new Socket();
                        this.clientSocket.connect(new InetSocketAddress(this.remoteIp, this.remotePort), 30000);

                        LOGGER.debug("client - remote address is {}", this.clientSocket.getRemoteSocketAddress().toString());
                        clientExecutor.submit(new Consumer(this.clientSocket, viewContext));

                    } catch (ConnectException e) {
                        LOGGER.debug("failed to establish a connection to server {}", e.getMessage());
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }
                        this.reconnects++;
                        LOGGER.debug("try to reconnect({}) to server...", this.reconnects);
                        this.connect(viewContext);
                    } catch (IOException e) {
                        LOGGER.error("failed to establish server connection: {}", e.getMessage());
                    }
                } else {
                    LOGGER.error("please provide the remote ip and port to connect to a server socket");
                }
            } else {
                LOGGER.error("aborted connection. reached max reconnect tries of {} to connect to the server", this.reconnects);
            }
        };

        this.clientConnectionExecutor.submit(new Thread(clientConnectTask));
    }

    public String sendMessage(String msg) {
        String response = "";
        try {
            //this.out.println(msg);
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

    private class Consumer implements Runnable {

        private final Socket clientSocket;
        private ViewContext viewContext;

        Consumer(Socket clientSocket, ViewContext viewContext) {
            this.clientSocket = clientSocket;
            this.viewContext = viewContext;
        }

        @Override
        public void run() {
            try (
                    PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                    BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))
            ) {
                String fromServer;

                // register game at server
                out.println("REGISTER CLIENT USER " + userId);

                while ((fromServer = in.readLine()) != null) {
                    LOGGER.debug("got message from server: {}", fromServer);

                    if (fromServer.contains("REGISTRATION_SUCCESS")) {
                        LOGGER.debug("Successfully registered at yatzi server. show UI now.");
                        Platform.runLater(() -> {
                            // update the statusbar
                            this.viewContext.getViewHandler().getStatusController().updateStatus("connected to server", StatusType.OK);
                            this.viewContext.getViewHandler().getScreenHelper().showScreen(viewContext, ScreenType.BOARD);
                        });
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                LOGGER.error("failed to connect to server ip {} on port {} because of {}", clientSocket.getRemoteSocketAddress(), clientSocket.getPort(), e.getMessage());
            }
        }
    }

}
