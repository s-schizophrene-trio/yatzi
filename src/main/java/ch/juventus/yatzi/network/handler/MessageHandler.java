package ch.juventus.yatzi.network.handler;

import ch.juventus.yatzi.network.model.Transfer;

import java.util.LinkedList;
import java.util.Queue;

public class MessageHandler {

    private Queue<Transfer> inputQueue = new LinkedList<>();
    private Queue<Transfer> outputQueue = new LinkedList<>();

    public void send(Transfer transfer) {
        this.outputQueue.add(transfer);
    }

    public Transfer getMessage() {
        try {
            return inputQueue.element();
        }
        catch (Exception e) {
            return new Transfer();
        }
    }

    public void receive(Transfer transfer) {
        inputQueue.add(transfer);
    }

}
