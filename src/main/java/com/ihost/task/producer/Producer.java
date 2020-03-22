package com.ihost.task.producer;

import java.util.Random;
import java.util.concurrent.BlockingQueue;

/**
 * Producer class generates random numbers and adds them to queue
 */
public class Producer implements Runnable {

    private final BlockingQueue<Integer> blockingQueue;

    /**
     * Constructor
     */
    public Producer(BlockingQueue<Integer> blockingQueue) {
        this.blockingQueue = blockingQueue;
    }

    @Override
    public void run() {
        try {
            this.produce();
        } catch (InterruptedException e) {
            System.out.println("Producer interrupted");
        }
    }

    @SuppressWarnings("InfiniteLoopStatement")
    private void produce() throws InterruptedException {
        Random r = new Random();
        while (true) {
            Thread.sleep(100L);
            synchronized (blockingQueue) {

                if (blockingQueue.size() == 98) {
                    while (blockingQueue.size() > 60) {
                        blockingQueue.wait();
                    }
                }
                int n = r.nextInt(101);
                System.out.println("Produced: " + n);
                blockingQueue.add(n);
                blockingQueue.notifyAll();
            }
        }
    }
}
