package com.varun.storage;

import com.varun.clock.ClockValue;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * A class representing the condition associated with a get request processing. Condition is updated as part of
 * processing {@link com.varun.model.SyncGetRequest}
 */
public class GetResponseCondition {
    private final Lock lock;
    private final Condition condition;
    private final List<ClockValue> clockValues;
    private final int totalProcessCount;

    public GetResponseCondition(int totalProcessCount, ClockValue selfClockValue) {
        this.totalProcessCount = totalProcessCount;
        this.lock = new ReentrantLock();
        this.condition = lock.newCondition();
        this.clockValues = new ArrayList<>();
        this.clockValues.add(selfClockValue);
    }

    /**
     * Checks if the condition is fulfilled or else blocks the current thread.
     *
     * @throws InterruptedException If the current thread is interrupted
     */
    public void isConditionMet() throws InterruptedException {
        lock.lock();
        try {
            while (this.clockValues.size() < totalProcessCount) {
                condition.await();
            }
        } finally {
            lock.unlock();
        }
        System.out.println("Condition fulfilled");
    }

    /**
     * Updates the condition by adding a clock value
     *
     * @param clockValue clock value associated with processing a {@link com.varun.model.SyncGetRequest}
     */
    public void addClockValue(ClockValue clockValue) {
        lock.lock();
        try {
            this.clockValues.add(clockValue);
            condition.signalAll();
        } finally {
            lock.unlock();
        }
    }

    public List<ClockValue> getClockValues() {
        return clockValues;
    }
}