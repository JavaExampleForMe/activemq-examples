package queue;


import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.boot.autoconfigure.jms.JmsProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.core.JmsTemplate;


@Configuration
public class JmsConfiguration {

    public static final String EXAMPLE_QUEUE = "exampleQueuq.q";

    @Bean("exampleCachingConnectionFactory")
    public CachingConnectionFactory cachingConnectionFactory()  {
        ActiveMQConnectionFactory activeMQConnectionFactory = activeMQConnectionFactory();
        CachingConnectionFactory cachingConnectionFactory = new CachingConnectionFactory(activeMQConnectionFactory);
        return cachingConnectionFactory;
    }

    @Bean
    public DefaultJmsListenerContainerFactory jmsListenerContainerFactory(CachingConnectionFactory cachingConnectionFactory) {
        DefaultJmsListenerContainerFactory factory =
                new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(cachingConnectionFactory);
        //Messages are acknowledged once the message listener implementation has called Message.acknowledge().
        factory.setSessionAcknowledgeMode(JmsProperties.AcknowledgeMode.CLIENT.getMode());
        //max number of concurrent users/consumers  (“lowwe-upper”)
        factory.setConcurrency("1-1");
        return factory;
    }

    @Bean
    public JmsTemplate jmsTemplate() {
        JmsTemplate jmsTemplate= new JmsTemplate(cachingConnectionFactory());
        return jmsTemplate;
    }

    private ActiveMQConnectionFactory activeMQConnectionFactory()  {
        ActiveMQConnectionFactory activeMQConnectionFactory =
                new ActiveMQConnectionFactory();
        activeMQConnectionFactory.setBrokerURL("nio://localhost:61629");
        return activeMQConnectionFactory;
    }

}
