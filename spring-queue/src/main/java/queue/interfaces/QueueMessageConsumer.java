package queue.interfaces;

public interface QueueMessageConsumer<T> {
    void consumeMessage(final T message);

}
