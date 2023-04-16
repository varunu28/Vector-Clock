package com.varun.storage;

import com.varun.clock.ClockValue;

import java.util.HashMap;
import java.util.Map;

/**
 * A util class for managing conditions for all processes
 */
public class GetResponseConditionUtil {

    private final Map<String, GetResponseCondition> getResponseConditionMap;

    public GetResponseConditionUtil() {
        this.getResponseConditionMap = new HashMap<>();
    }

    /**
     * Creates & persists a new {@link GetResponseCondition}
     *
     * @param key            key for the condition
     * @param selfClockValue ClockValue associated with process that is creating the condition
     * @param processCount   total number of processes participating in the condition
     */
    public void initializeCondition(String key, ClockValue selfClockValue, int processCount) {
        GetResponseCondition getResponseCondition = new GetResponseCondition(processCount, selfClockValue);
        this.getResponseConditionMap.put(key, getResponseCondition);
    }

    /**
     * Update the condition associated with a key
     *
     * @param key        key for which the condition is updated
     * @param clockValue clock value associated with given condition update
     */
    public void updateCondition(String key, ClockValue clockValue) {
        this.getResponseConditionMap.get(key).addClockValue(clockValue);
    }

    public GetResponseCondition getResponseCondition(String key) {
        return this.getResponseConditionMap.get(key);
    }
}
