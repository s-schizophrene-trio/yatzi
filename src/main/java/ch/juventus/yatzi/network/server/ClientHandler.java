package ch.juventus.yatzi.network.server;

import ch.juventus.yatzi.network.model.Transfer;
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

/**
 * Each new Client will have its own ClientTask. The Connection to the client will be always open.
 */
public class ClientHandler implements Runnable {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    Socket socket;
    UUID userId;
    Boolean isRunning;

    public ClientHandler(Socket socket, UUID userId) {
        this.socket = socket;
        this.userId = userId;
    }

    @Override
    public void run() {
        try (
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))
        ) {
            LOGGER.debug("got a client with ip {} on port {}", socket.getInetAddress(), socket.getLocalPort());

            String fromClient, fromServer;

            Transfer t = new Transfer();
            t.setSender(userId);
            t.setContext("registration");
            t.setQuery("type");
            t.setBody("REGISTRATION_SUCCESS");
            t.setSentTime(new Date());

            ObjectMapper objectMapper = new ObjectMapper();
            String transferData = objectMapper.writeValueAsString(t);

            out.println(transferData);

            while ((fromClient = in.readLine()) != null && isRunning) {
                LOGGER.debug("parse incoming message {}", fromClient);
                LOGGER.debug("got a message from a client: {}", objectMapper.readValue(fromClient, Transfer.class));
            }

            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
            LOGGER.error("Failed to process incoming or outgoing traffic to client");
        }
    }
}
