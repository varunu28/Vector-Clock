package com.varun.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.varun.clock.ClockValue;
import com.varun.exception.InvalidRequestException;

import static com.varun.model.RequestType.*;

public class RequestFactory {

    /**
     * @param message input given by user in string representation
     * @return DatabaseRequest concrete implementation of DatabaseRequest interface
     * @throws InvalidRequestException if validation for input fails
     * @throws JsonProcessingException if deserialization for ClockValue fails
     */
    public static DatabaseRequest parseRequest(String message) throws InvalidRequestException, JsonProcessingException {
        String[] splits = message.split("\\s+");
        if (splits.length == 0) {
            throw new InvalidRequestException("Request should contain a request type");
        }
        String requestType = splits[0];
        return switch (requestType) {
            case GET_TYPE -> parseGetRequest(splits);
            case SET_TYPE -> parseSetRequest(splits);
            case SYNC_GET_TYPE -> parseSyncGetRequest(splits);
            case SYNC_SET_TYPE -> parseSyncSetRequest(splits);
            default -> throw new InvalidRequestException("Invalid request type");
        };
    }

    private static DatabaseRequest parseGetRequest(String[] splits) throws InvalidRequestException {
        if (splits.length != 2) {
            throw new InvalidRequestException("GET request should be of form get {key}");
        }
        String key = splits[1];
        return new GetRequest(key);
    }

    private static DatabaseRequest parseSetRequest(String[] splits) throws InvalidRequestException {
        if (splits.length != 3) {
            throw new InvalidRequestException("SET request should be of form set {key} {value}");
        }
        String key = splits[1];
        String value = splits[2];
        return new SetRequest(key, value);
    }

    private static DatabaseRequest parseSyncGetRequest(String[] splits) throws InvalidRequestException {
        if (splits.length != 3) {
            throw new InvalidRequestException("SYNC GET request should be of form sync_get {key} {process_id}");
        }
        String key = splits[1];
        return new SyncGetRequest(key);
    }

    private static DatabaseRequest parseSyncSetRequest(String[] splits) throws InvalidRequestException, JsonProcessingException {
        if (splits.length != 3) {
            throw new InvalidRequestException("SYNC SET request should be of form sync_set {key} {vector_clock}");
        }
        String key = splits[1];
        ClockValue clockValue = ClockValue.deserialize(splits[2 ]);
        return new SyncSetRequest(key, clockValue);
    }

    private RequestFactory() {}
}
