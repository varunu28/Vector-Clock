package com.varun.clock;

import org.junit.Test;

import static org.junit.Assert.*;

public class VectorClockTest {

    @Test
    public void tick_success() {
        // Arrange
        VectorClock vectorClock = new VectorClock(1);

        // Act
        vectorClock.tick(1);

        // Assert
        assertArrayEquals(new int[]{1}, vectorClock.getClock());
    }

    @Test
    public void copy_success() {
        // Arrange
        VectorClock vectorClock = new VectorClock(1);
        vectorClock.tick(1);

        // Act
        VectorClock otherVectorClock = vectorClock.copy();

        // Assert
        assertArrayEquals(otherVectorClock.getClock(), vectorClock.getClock());
    }

    @Test
    public void receive_success() {
        // Arrange
        VectorClock vectorClockOne = new VectorClock(3);
        VectorClock vectorClockTwo = new VectorClock(3, new int[]{1, 2, 3});

        // Act
        vectorClockOne.receive(vectorClockTwo);

        // Assert
        assertArrayEquals(new int[]{1, 2, 3}, vectorClockOne.getClock());
    }

    @Test
    public void concurrent_success() {
        // Arrange
        VectorClock vectorClockOne = new VectorClock(3, new int[]{3, 2, 1});
        VectorClock vectorClockTwo = new VectorClock(3, new int[]{1, 2, 3});
        VectorClock vectorClockThree = new VectorClock(3, new int[]{10, 10, 10});


        // Assert
        assertTrue(vectorClockOne.isConcurrent(vectorClockTwo));
        assertTrue(vectorClockTwo.isConcurrent(vectorClockOne));

        assertFalse(vectorClockOne.isConcurrent(vectorClockThree));
        assertFalse(vectorClockTwo.isConcurrent(vectorClockThree));
    }
}