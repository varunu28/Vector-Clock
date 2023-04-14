package com.varun.model;

import com.varun.clock.ClockValue;
import com.varun.storage.Database;

public record SyncSetRequest(String key, ClockValue clockValue) implements DatabaseRequest {
    @Override
    public void process(Database database) {
        database.sync(key, clockValue);
    }
}
