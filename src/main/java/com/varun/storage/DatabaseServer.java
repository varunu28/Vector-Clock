package com.varun.storage;

import com.varun.util.MessageQueue;

public class DatabaseServer implements Runnable {

    private final int processId;

    private final int totalProcessCount;

    private final MessageQueue messageQueue;

    public DatabaseServer(int processId, int totalProcessCount, MessageQueue messageQueue) {
        this.processId = processId;
        this.totalProcessCount = totalProcessCount;
        this.messageQueue = messageQueue;
    }

    @SuppressWarnings("InfiniteLoopStatement")
    @Override
    public void run() {
        System.out.printf("Database server started for processId: %d\n", this.processId);
        while (true) {
            try {
                String message = messageQueue.getMessage(this.processId);
                System.out.printf("Received message: %s on process: %d \n", message, this.processId);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
