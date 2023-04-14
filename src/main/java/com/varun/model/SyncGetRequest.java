package com.varun.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.varun.clock.ClockValue;
import com.varun.storage.Database;
import com.varun.util.MessageQueue;

import java.util.Optional;

import static com.varun.model.RequestType.SYNC_GET_RESPONSE_TYPE;

public record SyncGetRequest(String key, int fromProcessId, MessageQueue messageQueue) implements DatabaseRequest {
    @Override
    public void process(Database database) {
        Optional<ClockValue> clockValue = database.sync(key);
        if (clockValue.isEmpty()) {
            System.out.println("No clock value found for key: " + key);
            return;
        }
        try {
            String message = String.format("%s %s %s", SYNC_GET_RESPONSE_TYPE, key, clockValue.get().serialize());
            messageQueue.publishMessage(fromProcessId, message);
        } catch (JsonProcessingException e) {
            System.out.println("Exception while serializing message for sync get: " + e.getMessage());
        }
    }
}
