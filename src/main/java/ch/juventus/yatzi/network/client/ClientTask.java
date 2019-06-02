package ch.juventus.yatzi.network.client;

import ch.juventus.yatzi.network.handler.MessageHandler;
import ch.juventus.yatzi.network.helper.Commands;
import ch.juventus.yatzi.network.model.Transfer;
import ch.juventus.yatzi.ui.interfaces.ViewContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Date;
import java.util.UUID;

import static ch.juventus.yatzi.network.helper.Commands.PLAYER_NEW;

public class ClientTask implements Runnable {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    private final Socket clientSocket;
    private ViewContext viewContext;
    private UUID userId;
    private MessageHandler messageHandler;
    private ObjectMapper objectMapper;
    private PrintWriter out;
    private Boolean isRunning;

    ClientTask(Socket clientSocket, ViewContext viewContext, UUID userId, MessageHandler messageHandler) {
        this.clientSocket = clientSocket;
        this.viewContext = viewContext;
        this.userId = userId;
        this.messageHandler = messageHandler;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        this.isRunning = true;
    }

    @Override
    public void run() {
        try {
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            String fromServer;
            ObjectMapper objectMapper = new ObjectMapper();

            // register local client / player at server
            Transfer t = new Transfer();
            t.setSender(userId);
            t.setFunction(PLAYER_NEW);
            t.setBody(objectMapper.writeValueAsString(viewContext.getYatziGame().getUserMe()));
            String transferData = objectMapper.writeValueAsString(t);

            // send registration to server
            out.println(transferData);

            while ((fromServer = in.readLine()) != null && isRunning) {
                LOGGER.debug("got message from server: {}", fromServer);

                // try to parse the input message (only messages of type Transfer will be checked)
                try {
                    Transfer transfer = objectMapper.readValue(fromServer, Transfer.class);
                    messageHandler.put(transfer);
                } catch (Exception e) {
                    LOGGER.error("received message could not be parsed: {} because of {}", fromServer, e.getMessage());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            LOGGER.error("failed to connect to server ip {} on port {} because of {}", clientSocket.getRemoteSocketAddress(), clientSocket.getPort(), e.getMessage());
        }
    }

    public void stop() {
        // send exit message to server
        send(new Transfer(userId, Commands.PLAYER_EXIT));
        try {
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        isRunning = false;
    }

    public void send(Transfer transfer) {
        LOGGER.debug("send message to client {}", clientSocket.getInetAddress());
        try {
            if (transfer.getSender() != null) {
                transfer.setSentTime(new Date());
                out.println(objectMapper.writeValueAsString(transfer));
            } else {
                LOGGER.error("failed to send message because the sender was not set");
            }

        } catch (Exception e) {
            LOGGER.error("failed to send message to client {}", e.getMessage());
        }
    }
}
