package ch.juventus.yatzi.network.handler;

import ch.juventus.yatzi.network.model.Transfer;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.LinkedList;
import java.util.Queue;

@Getter
@Setter
@NoArgsConstructor
public class MessageHandler {

    private Queue<Transfer> queue = new LinkedList<>();

    public void put(Transfer transfer) {
        this.queue.add(transfer);
    }
}
