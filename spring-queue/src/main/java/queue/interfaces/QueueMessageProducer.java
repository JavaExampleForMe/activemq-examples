package queue.interfaces;

import java.util.List;

public interface QueueMessageProducer<T> {
    void publishMessage(String queueDestination, T message, int policyId) ;
    List<T> getMessagesByPolicyId(String queueDestination, int policyId);
}
