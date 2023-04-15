package com.varun.storage;

import com.varun.clock.ClockValue;

import java.util.HashMap;
import java.util.Map;

public class GetResponseConditionUtil {

    private final Map<String, GetResponseCondition> getResponseConditionMap;

    public GetResponseConditionUtil() {
        this.getResponseConditionMap = new HashMap<>();
    }

    public void initializeCondition(String key, ClockValue selfClockValue, int processCount) {
        GetResponseCondition getResponseCondition = new GetResponseCondition(processCount, selfClockValue);
        this.getResponseConditionMap.put(key, getResponseCondition);
    }

    public void updateCondition(String key, ClockValue clockValue) {
        this.getResponseConditionMap.get(key).addClockValue(clockValue);
    }

    public GetResponseCondition getResponseCondition(String key) {
        return this.getResponseConditionMap.get(key);
    }
}
