package com.ihost.task.driver;

import com.ihost.task.consumer.Consumer;
import com.ihost.task.producer.Producer;

import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * This program implements the Producer Consumer pattern.
 * User enters the Producers count and Consumers count.
 * Each Producer generates random numbers and add in the queue with size 100 (FIFO).
 * Each Consumer takes the numbers from FIFO and print it on the console.
 * Queue size is 100, Producer blocks adding a new element to the  while queue size reaching to 98,
 * and unblocks once the number of elements are 60.
 * When interrupt the program Consumers should take all the numbers from the queue and print it on the console.
 */
public class Driver {

    private static final int QUEUE_SIZE = 100;
    private static final BlockingQueue<Integer> blockingQueue = new LinkedBlockingQueue<>(QUEUE_SIZE);
    private final int producersCount;
    private final int consumersCount;
    private final List<Thread> producerList;
    private final List<Thread> consumerList;


    public Driver(int producersCount, int consumersCount) {
        this.producersCount = producersCount;
        this.consumersCount = consumersCount;
        this.producerList = new ArrayList<>(producersCount);
        this.consumerList = new ArrayList<>(consumersCount);
    }

    /**
     * Start Producers and Consumers
     */
    public void run() {
        runProducers();
        runConsumers();

        waitForStopSignal();
    }

    /**
     * Run Producer threads
     */
    private void runProducers() {
        final Runnable producer = new Producer(blockingQueue);

        for (int i = 0; i < producersCount; ++i) {
            final Thread t = new Thread(producer);
            producerList.add(t);
            t.start();
        }
    }

    /**
     * Run Consumer threads
     */
    private void runConsumers() {
        final Runnable consumer = new Consumer(blockingQueue);

        for (int i = 0; i < consumersCount; ++i) {
            final Thread t = new Thread(consumer);
            consumerList.add(t);
            t.start();
        }
    }

    /**
     * Interrupts all the threads in case of external interruption
     */
    private  void waitForStopSignal() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {

            stopProducers();
            // wait 1 sec for producers to interrupt before interrupting consumers
            try {
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // wait for queue to flush
            while (!blockingQueue.isEmpty()) {
                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            stopConsumers();

        }));
    }

    /**
     * Interrupts Producer threads
     */
    private void stopProducers() {
        for (Thread thread : producerList) {
            thread.interrupt();
        }
    }

    /**
     * Interrupts Consumer threads
     */
    private void stopConsumers() {
        for (Thread thread : consumerList) {
            thread.interrupt();
        }
    }
}
