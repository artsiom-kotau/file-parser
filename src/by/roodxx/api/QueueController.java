package by.roodxx.api;

import java.util.Queue;

public class QueueController<E> implements Controller<E> {
    private final Queue<E> queue;

    public QueueController(Queue<E> queue) {
        this.queue = queue;
    }

    @Override
    public void add(E element) {
        queue.add(element);
    }
}
