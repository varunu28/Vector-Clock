package com.varun.clock;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Class representing a vector clock which will be associated with storage operations.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class VectorClock {

    private int totalProcessCount;

    private int[] clock;

    public VectorClock(int totalProcessCount) {
        this.totalProcessCount = totalProcessCount;
        this.clock = new int[totalProcessCount];
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
}
