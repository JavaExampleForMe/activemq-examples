package queue.imp;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.web.servlet.server.Session;
import org.springframework.jms.connection.CachingConnectionFactory;
import queue.interfaces.QueueException;
import queue.interfaces.QueueMessageProducer;

import javax.jms.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

@Slf4j
public class QueueMessageProducerImpl implements QueueMessageProducer {
    private ObjectMapper objectMapper;
    private CachingConnectionFactory connectionFactory;

    public QueueMessageProducerImpl(ObjectMapper objectMapper, CachingConnectionFactory connectionFactory) {
        this.objectMapper = objectMapper;
        this.connectionFactory = connectionFactory;
    }

    @Override
    public void publishMessage(String queueDestination, Object message, int policyId) {
        try(Connection connection = connectionFactory.createConnection()){
            connection.start();
            try(javax.jms.Session session = connection.createSession(false,javax.jms.Session.CLIENT_ACKNOWLEDGE);) {
                Queue queue = session.createQueue(queueDestination);
                try (MessageProducer producer = session.createProducer(queue)) {
                    Message msg = session.createTextMessage(objectMapper.writeValueAsString(message));
                    msg.setIntProperty("policyId", policyId);
                    producer.send(msg);
                } catch (JMSException e) {
                    throw new QueueException("Failed publishing message "+ message.toString(), e);
                } catch (JsonProcessingException e) {
                    throw new QueueException("Failed converting message to Json "+ message.toString(), e);
                }
            } catch (JMSException e) {
                throw new QueueException("Failed createSession ", e);
            }
            connection.stop();
        } catch (JMSException e) {
            throw new QueueException("Failed createConnection to queue " + queueDestination, e);
        }
    }

    @Override
    public List<TextMessage> getMessagesByPolicyId(String queueDestination, int policyId) {
        return getMessagesByFilter(queueDestination, "policyId = " + policyId, 0);
    }

    private List<TextMessage> getMessagesByFilter(String queueDestination, String filter, int limitMessagesBy) {
        int messageCounter = 0;
        List<TextMessage> messages = new ArrayList<>();
        try (Connection connection = connectionFactory.createConnection()) {
            connection.start();
            try (javax.jms.Session session = connection.createSession(false, javax.jms.Session.CLIENT_ACKNOWLEDGE);) {
                Queue queue = session.createQueue(queueDestination);
                javax.jms.QueueBrowser browser = getQueueBrowser(filter, session, queue);
                Enumeration e = browser.getEnumeration();
                while (e.hasMoreElements()) {
                    TextMessage textMessage = (TextMessage) e.nextElement();
                    messageCounter++;
                    log.info(messageCounter + ") acknowledge [" + textMessage.getText() + "]");
                }
            } catch (JMSException e) {
                throw new QueueException("Failed createSession ", e);
            }
            connection.stop();
        } catch (JMSException e) {
            throw new QueueException("Failed createConnection to queue " + queueDestination, e);
        }
        log.info("Acknowledging queue " + queueDestination + " Done. limitMessagesBy=" + limitMessagesBy + " messageCounter=" + messageCounter);
        return messages;
    }

    private javax.jms.QueueBrowser getQueueBrowser(String filter, javax.jms.Session session, Queue queue) {
        QueueBrowser browser;
        try {
            if (filter == null || filter == "")
                browser = session.createBrowser(queue, filter);
            else {
                browser = session.createBrowser(queue);
                log.info("Browsing queue Filter " + filter);
            }
        } catch (JMSException e) {
            throw new QueueException("Failed creating queueBrowser: "+ queue, e);
        }
        return browser;
    }

}