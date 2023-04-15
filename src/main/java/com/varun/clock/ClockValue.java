package com.varun.clock;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

public record ClockValue(String value, VectorClock vectorClock) {

    public String serialize() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(this);
    }

    public static ClockValue deserialize(String json) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(json, ClockValue.class);
    }

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
}
