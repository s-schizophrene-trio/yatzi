package ch.juventus.yatzi.network;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;

    public void startConnection(String ip, int port) {
        try {
        clientSocket = new Socket(ip, port);
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } catch (Exception e) {
            LOGGER.error("failed to connect to server ip {} on port {} because of {}", ip, port, e.getMessage());
        }
    }

    public String sendMessage(String msg) {
        String response = "";
        try {
        out.println(msg);
        response = in.readLine();
        } catch (Exception e) {
            LOGGER.error("failed to send message to server", e.getMessage());
        }
        return response;
    }

    public void stopConnection() {
        try {
            in.close();
            out.close();
            clientSocket.close();
        } catch (Exception e) {
            LOGGER.error("failed to stop connection to server", e.getMessage());
        }

    }
}
