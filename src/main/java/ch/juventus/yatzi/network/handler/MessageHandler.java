package ch.juventus.yatzi.network.handler;

import ch.juventus.yatzi.network.model.Message;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.LinkedList;
import java.util.Queue;

@Getter
@Setter
@NoArgsConstructor
public class MessageHandler {

    private Queue<Message> inputQueue = new LinkedList<>();
    private Queue<Message> outputQueue = new LinkedList<>();

    public void send(Message message) {
        this.outputQueue.add(message);
    }
    public void receive(Message message) {
        this.inputQueue.add(message);
    }
}
