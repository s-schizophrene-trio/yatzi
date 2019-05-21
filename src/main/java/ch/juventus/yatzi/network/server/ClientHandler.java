package ch.juventus.yatzi.network.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Each new Client will have its own ClientTask. The Connection to the client will be always open.
 */
public class ClientHandler implements Runnable {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    Socket socket;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try (
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))
        ) {
            LOGGER.debug("got a client with ip {} on port {}", socket.getInetAddress(), socket.getLocalPort());

            String fromClient, fromServer;

            out.println("REGISTRATION_SUCCESS");

            while ((fromClient = in.readLine()) != null) {
                LOGGER.debug("got a message from a client: {}", fromClient);
            }

            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
            LOGGER.error("Failed to process incoming or outgoing traffic to client");
        }
    }
}
