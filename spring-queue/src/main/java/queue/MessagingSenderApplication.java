package queue;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.connection.CachingConnectionFactory;
import queue.imp.QueueMessageConsumerImpl;
import queue.imp.QueueMessageProducerImpl;
import queue.interfaces.QueueMessageProducer;

@SpringBootApplication
@EnableJms
@Configuration
@Import(JmsConfiguration.class)
public class MessagingSenderApplication {


    public static void main(String[] args) {
        AbstractApplicationContext context = new AnnotationConfigApplicationContext(
                JmsConfiguration.class);
        SpringApplication.run(MessagingSenderApplication.class, args);
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(MapperFeature.DEFAULT_VIEW_INCLUSION, true);
        return mapper;
    }
    @Bean
    public QueueMessageProducer getQueueMessageProducer(ObjectMapper objectMapper,
                                                        @Qualifier("exampleCachingConnectionFactory") CachingConnectionFactory connectionFactory) {
        return new QueueMessageProducerImpl(objectMapper, connectionFactory);
    }

    @Bean
    QueueMessageConsumerImpl getQueueMessageConsumer(ObjectMapper objectMapper) {
        return new QueueMessageConsumerImpl(objectMapper);
    }
}
