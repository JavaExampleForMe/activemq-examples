package queue.interfaces;

public class QueueException extends RuntimeException {

    public QueueException(String message, Exception cause)
    {
        super(message, cause);
    }
}
