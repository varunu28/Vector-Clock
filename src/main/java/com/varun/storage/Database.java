package com.varun.storage;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.collect.ImmutableList;
import com.varun.clock.ClockValue;
import com.varun.clock.VectorClock;
import com.varun.util.MessageQueue;

import javax.swing.text.html.Option;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.varun.model.RequestType.SYNC_GET_TYPE;

public class Database {

    private final Map<String, ClockValue> db;

    private final VectorClock vectorClock;

    private final int processId;

    private final int totalProcessCount;

    private final MessageQueue messageQueue;

    public Database(int processId, int totalProcessCount, MessageQueue messageQueue) {
        this.processId = processId;
        this.totalProcessCount = totalProcessCount;
        this.vectorClock = new VectorClock(totalProcessCount);
        this.db = new HashMap<>();
        this.messageQueue = messageQueue;
    }

    public void set(String key, String val) {
        this.vectorClock.tick(this.processId);
        this.db.put(key, new ClockValue(val, VectorClock.copy(this.vectorClock)));
        try {
            String message = buildSyncMessage(key);
            dispatch(message);
        } catch (JsonProcessingException e) {
            System.out.println("Exception while dispatching message: " + e.getMessage());
        }
    }

    public void get(String key) {

    }

    public Optional<ClockValue> sync(String key) {
        if (!db.containsKey(key)) {
            return Optional.empty();
        }
        return Optional.of(db.get(key));
    }

    public void sync(String key, ClockValue clockValue) {

    }

    private String buildSyncMessage(String key) throws JsonProcessingException {
        return String.format("%s %s %s", SYNC_GET_TYPE, key, this.db.get(key).serialize());
    }

    private void dispatch(String message) {
        for (int i = 1; i <= this.totalProcessCount; i++) {
            if (i != this.processId) {
                this.messageQueue.publishMessage(i, message);
            }
        }
    }
}
