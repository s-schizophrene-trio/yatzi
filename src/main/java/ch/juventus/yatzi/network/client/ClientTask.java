package ch.juventus.yatzi.network.client;

import ch.juventus.yatzi.engine.user.User;
import ch.juventus.yatzi.network.handler.MessageHandler;
import ch.juventus.yatzi.network.helper.MessageType;
import ch.juventus.yatzi.network.model.Message;
import ch.juventus.yatzi.network.model.Transfer;
import ch.juventus.yatzi.ui.enums.ScreenType;
import ch.juventus.yatzi.ui.enums.StatusType;
import ch.juventus.yatzi.ui.interfaces.ViewContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class ClientTask implements Runnable {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());


    private final Socket clientSocket;
    private ViewContext viewContext;
    private UUID userId;
    private MessageHandler messageHandler;

    public ClientTask(Socket clientSocket, ViewContext viewContext, UUID userId, MessageHandler messageHandler) {
        this.clientSocket = clientSocket;
        this.viewContext = viewContext;
        this.userId = userId;
        this.messageHandler = messageHandler;
    }

    @Override
        public void run() {
            try (
                    PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                    BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))
            ) {
                String fromServer;

                Transfer t = new Transfer();
                t.setSender(userId);
                t.setContext("registration");
                t.setQuery("register");

                List<User> players = viewContext.getYatziGame().getPlayers();
                ObjectMapper objectMapper = new ObjectMapper();
                t.setBody(objectMapper.writeValueAsString(players));
                t.setSentTime(new Date());
                String transferData = objectMapper.writeValueAsString(t);

                out.println(transferData);

                while ((fromServer = in.readLine()) != null) {
                    LOGGER.debug("got message from server: {}", fromServer);

                    // try to parse the input message (only messages of type Transfer will be checked)
                    try {
                        Transfer transfer = objectMapper.readValue(fromServer, Transfer.class);

                        // registration handling
                        if (transfer.getContext().contains("registration")) {
                            messageHandler.receive(new Message("registration_successful", MessageType.INFO));
                        }

                    } catch (Exception e) {
                        LOGGER.error("received message could not be parsed: {} because of {}", fromServer, e.getMessage());
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                LOGGER.error("failed to connect to server ip {} on port {} because of {}", clientSocket.getRemoteSocketAddress(), clientSocket.getPort(), e.getMessage());
            }
        }
}
