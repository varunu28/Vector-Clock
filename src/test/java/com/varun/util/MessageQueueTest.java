package com.varun.util;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class MessageQueueTest {

    private static final int PROCESS_COUNT = 5;

    private static final String FAKE_MESSAGE = "fake message";

    private MessageQueue messageQueue;

    @Before
    public void before() {
        messageQueue = new MessageQueue(PROCESS_COUNT);
    }

    @Test
    public void messageQueue_success() throws Exception {
        messageQueue.publishMessage(1, FAKE_MESSAGE);

        String message = messageQueue.consumeMessage(1);
        assertEquals(message, FAKE_MESSAGE);
    }

    @Test
    public void threadBlockedIfNoMessage_success() throws InterruptedException {
        Thread consumerThread = new Thread(() -> {
            try {
                messageQueue.consumeMessage(1);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        consumerThread.start();
        Thread.sleep(100);

        assertEquals(Thread.State.WAITING, consumerThread.getState());
    }

    @Test
    public void outOfBoundProcessMessageConsumption_exception() {
        Exception exception = assertThrows(Exception.class, () -> messageQueue.consumeMessage(10));
        assertTrue(exception.getMessage().contains("Message queue does not contain processId: "));
    }

}