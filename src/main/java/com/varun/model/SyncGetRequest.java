package com.varun.model;

import com.varun.storage.Database;

public record SyncGetRequest(String key) implements DatabaseRequest {
    @Override
    public void process(Database database) {
        database.sync(key);
    }
}
