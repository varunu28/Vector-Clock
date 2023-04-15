package com.varun.clock;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ClockTest {

    private static final String FAKE_VALUE = "fake_value";

    @Test
    public void clockSerialization_success() throws JsonProcessingException {
        // Arrange
        VectorClock vectorClock = new VectorClock(5);
        ClockValue clockValue = new ClockValue(FAKE_VALUE, vectorClock);

        // Act
        String serializedClock = clockValue.serialize();
        ClockValue deserializedClock = ClockValue.deserialize(serializedClock);

        // Assert
        assertEquals(deserializedClock, clockValue);
        assertEquals(serializedClock, deserializedClock.serialize());
    }

    @Test
    public void highestClock_success() {
        // Arrange
        ClockValue clockValueOne = new ClockValue("1", new VectorClock(3, new int[]{0, 0, 0}));
        ClockValue clockValueTwo = new ClockValue("2", new VectorClock(3, new int[]{1, 1, 1}));
        ClockValue clockValueThree = new ClockValue("3", new VectorClock(3, new int[]{2, 2, 2}));

        // Act
        List<ClockValue> highestClockValues = ClockValue.findHighestClocks(
                Arrays.asList(clockValueOne, clockValueTwo, clockValueThree));

        // Assert
        assertEquals(1, highestClockValues.size());
        assertEquals(clockValueThree, highestClockValues.get(0));
    }

    @Test
    public void highestClockWithConcurrent_success() {
        // Arrange
        ClockValue clockValueOne = new ClockValue("1", new VectorClock(3, new int[]{0, 1, 0}));
        ClockValue clockValueTwo = new ClockValue("2", new VectorClock(3, new int[]{1, 0, 0}));
        ClockValue clockValueThree = new ClockValue("3", new VectorClock(3, new int[]{0, 0, 1}));

        // Act
        List<ClockValue> highestClockValues = ClockValue.findHighestClocks(
                Arrays.asList(clockValueOne, clockValueTwo, clockValueThree));

        // Assert
        assertEquals(3, highestClockValues.size());
        assertTrue(highestClockValues.contains(clockValueOne));
        assertTrue(highestClockValues.contains(clockValueTwo));
        assertTrue(highestClockValues.contains(clockValueThree));
    }

    @Test
    public void highestClockWithEqualClocks_success() {
        // Arrange
        ClockValue clockValueOne = new ClockValue("1", new VectorClock(3, new int[]{0, 0, 0}));
        ClockValue clockValueTwo = new ClockValue("1", new VectorClock(3, new int[]{0, 0, 0}));
        ClockValue clockValueThree = new ClockValue("1", new VectorClock(3, new int[]{0, 0, 0}));

        // Act
        List<ClockValue> highestClockValues = ClockValue.findHighestClocks(
                Arrays.asList(clockValueOne, clockValueTwo, clockValueThree));

        // Assert
        assertEquals(3, highestClockValues.size());
        assertTrue(highestClockValues.contains(clockValueOne));
        assertTrue(highestClockValues.contains(clockValueTwo));
        assertTrue(highestClockValues.contains(clockValueThree));
    }
}
