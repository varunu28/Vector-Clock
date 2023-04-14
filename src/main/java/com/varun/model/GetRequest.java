package com.varun.model;

import com.varun.storage.Database;

public record GetRequest(String key) implements DatabaseRequest {
    @Override
    public void process(Database database) {
        database.get(key);
    }
}
