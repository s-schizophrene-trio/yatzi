package ch.juventus.yatzi.network.server;

import ch.juventus.yatzi.network.handler.MessageHandler;
import ch.juventus.yatzi.network.model.Transfer;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.util.Date;
import java.util.UUID;

/**
 * Each new Client will have its own ClientTask. The Connection to the client will be always open.
 */
public class ClientHandler implements Runnable {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    Socket socket;
    MessageHandler messageHandler;
    Boolean isRunning;

    ObjectMapper objectMapper;
    PrintWriter out;

    UUID owner;

    public ClientHandler(Socket socket, MessageHandler messageHandler) {
        this.socket = socket;
        this.objectMapper = new ObjectMapper();
        this.messageHandler = messageHandler;
        this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        this.isRunning = true;
    }

    @Override
    public void run() {
        try {
            out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            LOGGER.debug("got a client with ip {} on port {}", socket.getInetAddress(), socket.getLocalPort());

            String fromClient;

            while ((fromClient = in.readLine()) != null && isRunning) {
                LOGGER.debug("server got a new message from client: {}", fromClient);
                Transfer transfer = objectMapper.readValue(fromClient, Transfer.class);

                // the first incoming message defines the owner of this connection
                if (owner == null) {
                    owner = transfer.getSender();
                }

                // validate if the request is from the registered user
                if (owner.equals(transfer.getSender())) {
                    messageHandler.put(transfer);
                } else {
                    LOGGER.debug("user validation failed. allowed user is {} and request was  {}", owner, fromClient);
                }

            }
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
            LOGGER.error("Failed to process incoming or outgoing traffic to client");
        }
    }

    /**
     * The owner is the only client allowed to use this thread. The owner will be initialized after
     * the first incoming message.
     * @return The uuid of the user from remote client
     */
    public UUID getOwner() {
        return owner;
    }

    public void stop() {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        isRunning = false;
    }

    public void send(Transfer transfer) {
        LOGGER.debug("send message to client {}", socket.getInetAddress());
        try {
            transfer.setSentTime(new Date());
            out.println(objectMapper.writeValueAsString(transfer));
        } catch (Exception e) {
            LOGGER.error("failed to send message to client {}", e.getMessage());
        }
    }
}
