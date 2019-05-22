package ch.juventus.yatzi.network.client;

import ch.juventus.yatzi.engine.user.User;
import ch.juventus.yatzi.network.handler.MessageHandler;
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
            // register game at server
            t.setBody(transferData);
            out.println(transferData);

            while ((fromServer = in.readLine()) != null) {
                LOGGER.debug("got message from server: {}", fromServer);

                if (fromServer.contains("REGISTRATION_SUCCESS")) {
                    LOGGER.debug("successfully registered at yatzi server. show ui now.");
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
