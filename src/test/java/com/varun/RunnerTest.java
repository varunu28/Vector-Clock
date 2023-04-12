package com.varun;

import org.junit.Test;

import static org.junit.Assert.*;

public class RunnerTest {

    @Test
    public void runnerWithNoArguments_success() throws Exception {
       Exception e = assertThrows(IllegalArgumentException.class, () -> Runner.main(new String[]{}));
       assertEquals("Runner should called with processCount", e.getMessage());
    }

    @Test
    public void runnerWithNonIntegerArg_success() throws Exception {
        Exception e = assertThrows(IllegalArgumentException.class, () -> Runner.main(new String[]{"A"}));
        assertTrue(e.getMessage().contains("Error while parsing the processCount "));
    }
}