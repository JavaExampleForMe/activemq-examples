package queue.imp;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import queue.JmsConfiguration;
import queue.interfaces.QueueMessageConsumer;

import javax.jms.Message;
import javax.jms.TextMessage;

@Slf4j
public class QueueMessageConsumerImpl<T> implements QueueMessageConsumer<Message> {
    private ObjectMapper objectMapper;

    public QueueMessageConsumerImpl(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @JmsListener(destination = JmsConfiguration.EXAMPLE_QUEUE)
    @Override
    public void consumeMessage(final Message message) {
        Policy policy;
        String stringMessage = "";
        try {
            TextMessage textMessage = (TextMessage) message;
            log.debug("Received message: " + textMessage);
            stringMessage = textMessage.getText();
            policy = objectMapper.readValue(stringMessage, Policy.class);
            log.info("Consumed MEssage: " + policy.toString());
            message.acknowledge();
            log.info("Acknowledge command was sent.");

        } catch (Exception e) {
            log.error("Exception occurred when trying to get message " + stringMessage + "from queue:" + JmsConfiguration.EXAMPLE_QUEUE, e);
        }
    }

}
