package java.com.javacodegeeks.jms;

import java.net.URISyntaxException;
import java.util.Enumeration;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.QueueBrowser;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;

public class JmsQueueBrowserWithMessageSelectorExample {
	public static void main(String[] args) throws Exception {
		Connection connection = null;
		try {
			// Producer
			ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(
					"tcp://localhost:61629");
			connection = connectionFactory.createConnection();
			Session session = connection.createSession(false,
					Session.CLIENT_ACKNOWLEDGE);
			Queue queue = session.createQueue("retention3.q");

			MessageProducer producer = session.createProducer(queue);
			for (int i = 1; i < 4; i++) {
				String payload = "{\"id\":14,\"name\":\"p1\",\"reason\":null,\"createdBy\":1,\"origin\":\"POLICY_MANAGER_FE\",\"status\":\"AwaitingApproval\",\"definition\":{\"filter\":{\"dateTimeRange\":{\"from\":\"2014-01-01\",\"to\":\"2014-03-30\"},\"mediaTypes\":[\"Screen\",\"Voice\",\"Email\",\"Chat\",\"Video\",\"SMS\",\"OriginalEmail\",\"Survey\",\"Survey_WebComplaint\"],\"groupIds\":[],\"users\":{\"userFilter\":null,\"userIds\":[1,2,3]},\"customFields\":[]},\"policyDefinitionAction\":{\"policyDefinitionActionPayload\":{\"policyDefinitionActionType\":\"Extend\",\"retention\":{\"type\":\"Months\",\"amount\":3,\"allowReduce\":false},\"forceDeletion\":null}}},\"executedBy\":0,\"executeDate\":\"2019-04-21T06:39:42.757\"}";
				Message msg = session.createTextMessage(payload);
				System.out.println("Sending text '" + payload + "'");
				msg.setStringProperty("policyId", String.valueOf(i));
				producer.send(msg);
			}

			connection.start();

			int counter = 0;
			System.out.println("Browse through the elements in queue");
			QueueBrowser browser = session.createBrowser(queue,
					"policyId = '2'");
			Enumeration e = browser.getEnumeration();
			while (e.hasMoreElements()) {
				TextMessage textMessage = (TextMessage) e.nextElement();
				counter++;
				System.out.println(counter + ") Browse [" + textMessage.getText() + "]");
			}
			System.out.println("Done :" + counter);
			browser.close();

			MessageConsumer consumer = session.createConsumer(queue,"policyId = '2'");
			Message msg = consumer.receiveNoWait();
			while (msg != null) {
				msg.acknowledge();
				TextMessage textMsg = (TextMessage)msg;
				System.out.println(textMsg.getText());
				System.out.println("Received: " + textMsg.getText());
				msg = consumer.receiveNoWait();
			}
			session.close();
		} finally {
			if (connection != null) {
				connection.close();
			}
		}
	}

}
