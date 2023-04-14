package com.varun.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.varun.clock.ClockValue;
import com.varun.exception.InvalidRequestException;
import com.varun.util.MessageQueue;

import static com.varun.model.RequestType.*;

public class RequestFactory {

    public static DatabaseRequest parseRequest(String message, MessageQueue messageQueue) throws InvalidRequestException, JsonProcessingException {
        String[] splits = message.split("\\s+");
        if (splits.length == 0) {
            throw new InvalidRequestException("Request should contain a request type");
        }
        String requestType = splits[0];
        return switch (requestType) {
            case GET_TYPE -> parseGetRequest(splits);
            case SET_TYPE -> parseSetRequest(splits);
            case SYNC_GET_TYPE -> parseSyncGetRequest(splits, messageQueue);
            case SYNC_SET_TYPE -> parseSyncSetRequest(splits);
            case SYNC_GET_RESPONSE_TYPE -> parseSyncGetResponse(splits);
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

    private static DatabaseRequest parseSyncGetRequest(String[] splits, MessageQueue messageQueue) throws InvalidRequestException {
        if (splits.length != 3) {
            throw new InvalidRequestException("SYNC GET request should be of form sync_get {key} {process_id}");
        }
        String key = splits[1];
        int fromProcessId = Integer.parseInt(splits[2]);
        return new SyncGetRequest(key, fromProcessId, messageQueue);
    }

    private static DatabaseRequest parseSyncSetRequest(String[] splits) throws InvalidRequestException, JsonProcessingException {
        if (splits.length != 3) {
            throw new InvalidRequestException("SYNC SET request should be of form sync_set {key} {vector_clock}");
        }
        String key = splits[1];
        ClockValue clockValue = ClockValue.deserialize(splits[2 ]);
        return new SyncSetRequest(key, clockValue);
    }

    private static DatabaseRequest parseSyncGetResponse(String[] splits) throws InvalidRequestException, JsonProcessingException {
        if (splits.length != 3) {
            throw new InvalidRequestException("SYNC GET response should be of form sync_set {key} {value} {vector_clock}");
        }
        String key = splits[1];
        ClockValue clockValue = ClockValue.deserialize(splits[2]);
        return new SyncGetResponse(key, clockValue);
    }

    private RequestFactory() {}
}
