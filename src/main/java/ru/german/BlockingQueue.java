package ru.german;

import java.util.LinkedList;
import java.util.Queue;

public class BlockingQueue <T> {
    private final Queue<T> queue;
    private final int capacity;

    public BlockingQueue(int capacity) {
        if (capacity < 0) throw new IllegalArgumentException("Capacity must be positive");
        this.capacity = capacity;
        this.queue = new LinkedList<>();
    }

    public synchronized void enqueue(T task) throws InterruptedException {
        while (queue.size() == capacity) {
            wait();
        }
        queue.offer(task);
        notifyAll();
    }

    public synchronized T dequeue() throws InterruptedException {
        while (queue.isEmpty()) {
            wait();
        }
        T item = queue.poll();
        notifyAll();
        return item;
    }

    public int size() {
        return queue.size();
    }
}
