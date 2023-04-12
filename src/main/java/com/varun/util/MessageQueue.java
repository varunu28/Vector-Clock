package com.varun.util;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;

public class MessageQueue {

    private final ConcurrentHashMap<Integer, BlockingQueue<String>> processQueue;

    public MessageQueue(int processCount) {
        this.processQueue = new ConcurrentHashMap<>();
        for (int i = 1; i <= processCount; i++) {
            this.processQueue.put(i, new LinkedBlockingDeque<>());
        }
    }

    public void publishMessage(int processId, String message) {
        this.processQueue.get(processId).add(message);
    }

    public String getMessage(int processId) throws InterruptedException {
        return this.processQueue.get(processId).take();
    }
}
