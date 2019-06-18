package ch.juventus.yatzi.network.handler;

import ch.juventus.yatzi.network.model.Transfer;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.LinkedList;
import java.util.Queue;

/**
 * The Message Handler is used for bi-directional communication between ui thread and another thread
 */
@Getter
@NoArgsConstructor
public class MessageHandler {

    /**
     * The Message Queue to poll by a consumer
     */
    private Queue<Transfer> queue = new LinkedList<>();

    /**
     * Puts a new Object in the message queue
     * @param transfer A transfer object
     */
    public void put(Transfer transfer) {
        this.queue.add(transfer);
    }
}
