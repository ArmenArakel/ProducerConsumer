package com.ihost.task;

import com.ihost.task.driver.Driver;
import org.junit.Test;
import org.mockito.stubbing.Answer;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

/**
 * Unit test for simple App.
 */
public class AppTest {

    @SuppressWarnings("unchecked")
    @Test
    public void testNoDataPointMissed() throws Exception {

        Field blockingQueueField = Driver.class.getDeclaredField("blockingQueue");
        blockingQueueField.setAccessible(true);
        BlockingQueue<Integer> blockingQueue = (BlockingQueue<Integer>) blockingQueueField.get(null);
        BlockingQueue<Integer> spyQueue = spy(blockingQueue);
        blockingQueueField.set(null, spyQueue);

        final List<Integer> polledList = new ArrayList<>();
        final List<Integer> offeredList = new ArrayList<>();

        // Add all the numbers added in queue to offeredList
        Answer<Boolean> answerAdd = invocation -> {
            Object[] args = invocation.getArguments();
            Integer n = (Integer)args[0];
            offeredList.add(n);
            spyQueue.offer(n);
            return true;
        };
        when(spyQueue.add(anyInt())).thenAnswer(answerAdd);

        // Add all the numbers taken from the queue to polledList
        Answer<Integer> answerPoll = invocation -> {
            Integer n = spyQueue.take();
            polledList.add(n);
            return n;
        };
        when(spyQueue.poll()).thenAnswer(answerPoll);

        try {
            execute();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // offeredList and polledList should be the same
        assertEquals(polledList, offeredList);
    }

    /**
     * Executes the program for 4 secs, then stops producers and consumers
     */
    @SuppressWarnings("unchecked")
    private void execute() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, NoSuchFieldException {

        Driver driver = new Driver(10, 5);

        driver.run();
        try {
            // Let work for 4 secs
            Thread.sleep(4000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Method stopProducers = Driver.class.getDeclaredMethod("stopProducers");
        stopProducers.setAccessible(true);
        stopProducers.invoke(driver, null);

        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Field blockingQueue = Driver.class.getDeclaredField("blockingQueue");
        blockingQueue.setAccessible(true);
        BlockingQueue<Integer> blQueue = (BlockingQueue<Integer>) blockingQueue.get(null);
        while (!blQueue.isEmpty()) {
            try {
                Thread.sleep(100L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        Method stopConsumers = Driver.class.getDeclaredMethod("stopConsumers");
        stopConsumers.setAccessible(true);
        stopConsumers.invoke(driver, null);
    }
}
