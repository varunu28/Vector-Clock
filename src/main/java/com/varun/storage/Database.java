package com.varun.storage;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.varun.clock.ClockValue;
import com.varun.clock.VectorClock;
import com.varun.util.MessageQueue;

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

    /**
     * Persists a database record for given key & value. Also syncs with other processes for the database operation.
     *
     * @param key for database record
     * @param val for database record
     */
    public void set(String key, String val) {
        this.vectorClock.tick(this.processId);
        this.db.put(key, new ClockValue(val, this.vectorClock.copy()));
        try {
            String message = buildSyncSetMessage(key);
            dispatch(message);
        } catch (JsonProcessingException e) {
            System.out.println("Exception while dispatching message: " + e.getMessage());
        }
    }

    /**
     * Get the value associated with a key. Sync with other processes to find the latest value
     *
     * @param key for which the value needs to be retrieved
     */
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

        // Calculate the highest clocks
        List<ClockValue> highestClockValues = ClockValue.findHighestClocks(
                this.getResponseConditionUtil.getResponseCondition(key).getClockValues());
        if (highestClockValues.isEmpty()) {
            System.out.println("No value found for key: " + key);
        } else {
            System.out.printf("Valid values for key: %s are: {%s}\n",
                    key,
                    highestClockValues
                            .stream()
                            .map(ClockValue::getValue)
                            .collect(Collectors.toList()));
        }
    }

    /**
     * Respond to sync get request for a key
     *
     * @param key for which this process needs to respond for {@link com.varun.model.SyncGetRequest}
     */
    public void sync(String key) {
        ClockValue clockValue = this.db.getOrDefault(key, null);
        this.getResponseConditionUtil.updateCondition(key, clockValue);
    }

    /**
     * Respond to sync set request for a key
     *
     * @param key        key for which this process needs to respond for {@link com.varun.model.SyncSetRequest}
     * @param clockValue value associated with set operation for the key
     */
    public void sync(String key, ClockValue clockValue) {
        if (!this.db.containsKey(key)) {
            this.db.put(key, clockValue);
        } else {
            this.vectorClock.receive(clockValue.getVectorClock());
            this.db.put(key, new ClockValue(clockValue.getValue(), this.vectorClock.copy()));
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
