package com.varun;

import org.junit.Test;

import static org.junit.Assert.*;

public class RunnerTest {

    @Test
    public void runnerWithNoProcessCount_success() {
        Exception e = assertThrows(IllegalArgumentException.class, () -> Runner.main(new String[]{}));
        assertEquals("Runner should called with processCount", e.getMessage());
    }

    @Test
    public void runnerWithNonIntegerProcessCount_success() {
        System.setProperty("processCount", "string_value");
        Exception e = assertThrows(IllegalArgumentException.class, () -> Runner.main(new String[]{}));
        assertTrue(e.getMessage().contains("Error while parsing the processCount "));
    }
}