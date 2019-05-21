package ch.juventus.yatzi.network.client;

import ch.juventus.yatzi.ui.enums.ScreenType;
import ch.juventus.yatzi.ui.enums.StatusType;
import ch.juventus.yatzi.ui.interfaces.ViewContext;
import javafx.application.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientTask implements Runnable {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());


    private final Socket clientSocket;
    private ViewContext viewContext;
    private String userId;

    public ClientTask(Socket clientSocket, ViewContext viewContext, String userId) {
        this.clientSocket = clientSocket;
        this.viewContext = viewContext;
        this.userId = userId;
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
