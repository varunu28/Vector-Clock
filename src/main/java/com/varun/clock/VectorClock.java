package com.varun.clock;

import java.util.Arrays;

/**
 * Class representing a vector clock which will be associated with storage operations.
 */
public class VectorClock {

    private int totalProcessCount;

    private int[] clock;

    public VectorClock() {}

    public VectorClock(int totalProcessCount) {
        this.totalProcessCount = totalProcessCount;
        this.clock = new int[totalProcessCount];
    }

    public VectorClock(int totalProcessCount, int[] clock) {
        this.totalProcessCount = totalProcessCount;
        this.clock = clock;
    }

    /**
     * Method to create a deep copy of a VectorClock instance
     *
     * @return copy of current VectorClock instance
     */
    public VectorClock copy() {
        return new VectorClock(this.getTotalProcessCount(), this.getClock());
    }

    /**
     * Increments the monotonically increasing clock associated with the processId
     *
     * @param processId process for which the clock needs to be incremented
     */
    public void tick(int processId) {
        this.clock[processId - 1]++;
    }

    /**
     * @param otherVectorClock Increment
     */
    public void receive(VectorClock otherVectorClock) {
        int[] otherClock = otherVectorClock.getClock();
        for (int i = 0; i < totalProcessCount; i++) {
            this.clock[i] = Math.max(this.clock[i], otherClock[i]);
        }
    }

    /**
     * Check if this instance of VectorClock is concurrent with another VectorClock instance
     *
     * @param otherVectorClock VectorClock instance with which concurrency needs to be checked
     * @return boolean returns a true/false based on if clock is concurrent or not.
     */
    public boolean isConcurrent(VectorClock otherVectorClock) {
        boolean smallerFound = false;
        boolean higherFound = false;
        int[] otherClock = otherVectorClock.getClock();
        for (int i = 0; i < totalProcessCount; i++) {
            smallerFound = this.clock[i] < otherClock[i] || smallerFound;
            higherFound = this.clock[i] > otherClock[i] || higherFound;
        }
        return smallerFound && higherFound;
    }

    public int getTotalProcessCount() {
        return totalProcessCount;
    }

    public int[] getClock() {
        return Arrays.copyOf(this.clock, this.totalProcessCount);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        VectorClock that = (VectorClock) o;

        if (totalProcessCount != that.totalProcessCount) return false;
        return Arrays.equals(clock, that.clock);
    }

    @Override
    public int hashCode() {
        int result = totalProcessCount;
        result = 31 * result + Arrays.hashCode(clock);
        return result;
    }
}
