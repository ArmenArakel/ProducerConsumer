package com.ihost.task.consumer;

import java.util.concurrent.BlockingQueue;

/**
 * Consumer class takes numbers from the queue and prints them on the console
 */
public class Consumer implements Runnable {

    private final BlockingQueue<Integer> blockingQueue;

    /**
     * Constructor
     */
    public Consumer(BlockingQueue<Integer> blockingQueue) {
        this.blockingQueue = blockingQueue;
    }

    @Override
    public void run() {
        try {
            this.consume();
        } catch (InterruptedException e) {
            System.out.println("Consumer stopped.");
        }
    }

    @SuppressWarnings("InfiniteLoopStatement")
    private void consume() throws InterruptedException {
        while (true) {
            Thread.sleep(100L);
            synchronized (blockingQueue) {
                while (blockingQueue.isEmpty()) {
                    blockingQueue.wait();
                }

                System.out.println("Consumed: " + blockingQueue.poll());
            }
        }
    }
}
