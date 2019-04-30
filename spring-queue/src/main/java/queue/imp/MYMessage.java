package queue.imp;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by mostafab on 01/04/2019.
 */
@Getter
@Setter
public class MYMessage {
    private int id;
    private String text;

    public MYMessage() {
        id = 1011111111;
        text = "working with queues";
    }

    @Override
    public String toString() {
        return String.format("{id: %d , text: %s}",getId(),getText());
    }
}
