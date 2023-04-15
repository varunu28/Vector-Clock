package com.varun.model;

import com.varun.storage.Database;
import com.varun.util.MessageQueue;

public record SyncGetRequest(String key, MessageQueue messageQueue) implements DatabaseRequest {
    @Override
    public void process(Database database) {
        database.sync(key);
    }
}
