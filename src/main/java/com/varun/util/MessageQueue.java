package com.varun.util;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * A queue implementation to publish & consume messages for all running processes
 */
public class MessageQueue {

    private final ConcurrentHashMap<Integer, BlockingQueue<String>> processQueue;

    public MessageQueue(int processCount) {
        this.processQueue = new ConcurrentHashMap<>();
        for (int i = 1; i <= processCount; i++) {
            this.processQueue.put(i, new LinkedBlockingDeque<>());
        }
    }

    /**
     * Publishes a message for the given process
     *
     * @param processId process for which message needs to be published
     * @param message   String representation of message
     */
    public void publishMessage(int processId, String message) {
        this.processQueue.get(processId).add(message);
    }

    /**
     * Consumes a message for the given process
     *
     * @param processId Process for which message needs to be consumed
     * @return String representation of message from the queue
     * @throws Exception throws an exception if out of range processId is provided for message consumption
     */
    public String consumeMessage(int processId) throws Exception {
        if (!this.processQueue.containsKey(processId)) {
            throw new Exception(String.format("Message queue does not contain processId: %d", processId));
        }
        return this.processQueue.get(processId).take();
    }
}
