package com.varun.storage;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.varun.clock.ClockValue;
import com.varun.clock.VectorClock;
import com.varun.util.MessageQueue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.varun.model.RequestType.SYNC_GET_TYPE;
import static com.varun.model.RequestType.SYNC_SET_TYPE;

public class Database {

    private final Map<String, ClockValue> db;

    private final VectorClock vectorClock;

    private final int processId;

    private final int totalProcessCount;

    private final MessageQueue messageQueue;

    private final GetResponseConditionUtil getResponseConditionUtil;

    public Database(int processId, int totalProcessCount, MessageQueue messageQueue, GetResponseConditionUtil getResponseConditionUtil) {
        this.processId = processId;
        this.totalProcessCount = totalProcessCount;
        this.vectorClock = new VectorClock(totalProcessCount);
        this.db = new HashMap<>();
        this.messageQueue = messageQueue;
        this.getResponseConditionUtil = getResponseConditionUtil;
    }

    public void set(String key, String val) {
        this.vectorClock.tick(this.processId);
        this.db.put(key, new ClockValue(val, VectorClock.copy(this.vectorClock)));
        try {
            String message = buildSyncSetMessage(key);
            dispatch(message);
        } catch (JsonProcessingException e) {
            System.out.println("Exception while dispatching message: " + e.getMessage());
        }
    }

    public void get(String key) {
        // Initialize condition
        ClockValue selfClockValue = this.db.getOrDefault(key, null);
        this.getResponseConditionUtil.initializeCondition(key, selfClockValue, totalProcessCount);

        // Send sync_get request to other nodes
        String message = buildSyncGetMessage(key);
        dispatch(message);

        // Wait for condition to be fulfilled
        try {
            this.getResponseConditionUtil.getResponseCondition(key).isConditionMet();
        } catch (InterruptedException e) {
            System.out.println("Thread interrupted: " + e.getMessage());
        }

        printResponse(this.getResponseConditionUtil.getResponseCondition(key).getClockValues(), key);
    }

    private void printResponse(List<ClockValue> clockValues, String key) {
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
        if (highestClockValues.isEmpty()) {
            System.out.println("No value found for key: " + key);
        } else {
            System.out.printf("Valid values for key: %s are: {%s}\n",
                    key,
                    highestClockValues
                            .stream()
                            .map(ClockValue::value)
                            .collect(Collectors.toList()));
        }
    }

    public void sync(String key) {
        ClockValue clockValue = this.db.getOrDefault(key, null);
        this.getResponseConditionUtil.updateCondition(key, clockValue);
    }

    public void sync(String key, ClockValue clockValue) {
        if (!this.db.containsKey(key)) {
            this.db.put(key, clockValue);
        } else {
            VectorClock mergedClock = VectorClock.merge(this.vectorClock, clockValue.vectorClock());
            this.db.put(key, new ClockValue(clockValue.value(), mergedClock));
        }
    }

    private String buildSyncSetMessage(String key) throws JsonProcessingException {
        return String.format("%s %s %s", SYNC_SET_TYPE, key, this.db.get(key).serialize());
    }

    private String buildSyncGetMessage(String key) {
        return String.format("%s %s %d", SYNC_GET_TYPE, key, this.processId);
    }

    private void dispatch(String message) {
        for (int i = 1; i <= this.totalProcessCount; i++) {
            if (i != this.processId) {
                this.messageQueue.publishMessage(i, message);
            }
        }
    }
}
