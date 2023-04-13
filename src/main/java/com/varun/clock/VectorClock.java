package com.varun.clock;

import java.util.Arrays;

public class VectorClock {

    private final int totalProcessCount;

    private final int[] clock;

    public VectorClock(int totalProcessCount) {
        this.totalProcessCount = totalProcessCount;
        this.clock = new int[totalProcessCount];
    }

    public VectorClock(int totalProcessCount, final int[] clock) {
        this.totalProcessCount = totalProcessCount;
        this.clock = clock;
    }

    public static VectorClock copy(VectorClock otherVectorClock) {
        return new VectorClock(otherVectorClock.getTotalProcessCount(), otherVectorClock.getClock());
    }

    public void tick(int processId) {
        this.clock[processId - 1]++;
    }

    public void receive(VectorClock otherVectorClock) {
        int[] otherClock = otherVectorClock.getClock();
        for (int i = 0; i < totalProcessCount; i++) {
            this.clock[i] = Math.max(this.clock[i], otherClock[i]);
        }
    }

    public static VectorClock merge(VectorClock vectorClockOne, VectorClock vectorClockTwo) {
        int[] mergedClock = new int[vectorClockOne.getTotalProcessCount()];
        int[] clockOne = vectorClockOne.getClock();
        int[] clockTwo = vectorClockTwo.getClock();
        for (int i = 0; i < mergedClock.length; i++) {
            mergedClock[i] = Math.max(clockTwo[i], clockOne[i]);
        }
        return new VectorClock(vectorClockOne.getTotalProcessCount(), mergedClock);
    }

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
