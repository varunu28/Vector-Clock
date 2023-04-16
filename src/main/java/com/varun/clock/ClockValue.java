package com.varun.clock;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

/**
 * ClockValue stores a pair of value associated with a database key & VectorClock at which the
 * record was persisted.
 */
public record ClockValue(String value, VectorClock vectorClock) {

    /**
     * Deserializes a string representation of ClockValue to a ClockValue instance.
     *
     * @param json string representation of ClockValue
     * @return ClockValue instance
     * @throws JsonProcessingException if deserialization fails
     */
    public static ClockValue deserialize(String json) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(json, ClockValue.class);
    }

    /**
     * Given a list of ClockValue, finds the highest clock value. If there are multiple concurrent or identical
     * ClockValue, this returns all the concurrent and identical ClockValue.
     *
     * @param clockValues List of ClockValue
     * @return List<ClockValue> list of highest ClockValue
     */
    public static List<ClockValue> findHighestClocks(List<ClockValue> clockValues) {
        List<ClockValue> highestClockValues = new ArrayList<>();
        for (ClockValue clockValue : clockValues) {
            if (clockValue != null) {
                if (highestClockValues.isEmpty()) {
                    highestClockValues.add(clockValue);
                } else {
                    VectorClock currVectorClock = clockValue.vectorClock();
                    VectorClock highestVectorClock = highestClockValues.get(0).vectorClock();
                    if (currVectorClock.isConcurrent(highestVectorClock) ||
                            currVectorClock.equals(highestVectorClock)) {
                        highestClockValues.add(clockValue);
                    } else {
                        highestClockValues.clear();
                        highestClockValues.add(clockValue);
                    }
                }
            }
        }
        return highestClockValues;
    }

    /**
     * Serializes a ClockValue to a string representation
     */
    public String serialize() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(this);
    }
}
