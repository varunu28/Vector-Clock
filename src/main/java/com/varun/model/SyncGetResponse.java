package com.varun.model;

import com.varun.clock.ClockValue;
import com.varun.storage.Database;

public record SyncGetResponse(String key, ClockValue clockValue) implements DatabaseRequest {
    @Override
    public void process(Database database) {

    }
}
