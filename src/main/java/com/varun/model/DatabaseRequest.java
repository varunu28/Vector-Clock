package com.varun.model;

import com.varun.storage.Database;

public interface DatabaseRequest {

    void process(Database database);
}
