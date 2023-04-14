package com.varun.model;

import com.varun.storage.Database;

public record SetRequest(String key, String value) implements DatabaseRequest {
    @Override
    public void process(Database database) {
        database.set(key, value);
    }
}
