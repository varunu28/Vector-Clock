package com.varun.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.varun.clock.ClockValue;
import com.varun.clock.VectorClock;
import com.varun.exception.InvalidRequestException;
import com.varun.storage.Database;
import com.varun.util.MessageQueue;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class RequestFactoryTest {

    @Mock
    private MessageQueue messageQueue;

    @Mock
    private Database database;

    @Before
    public void setUp() {
    }

    @Test
    public void parseRequest_emptyString() {
        assertThrows(InvalidRequestException.class, () -> RequestFactory.parseRequest("", messageQueue));
    }

    @Test
    public void getRequestParse_success() throws InvalidRequestException, JsonProcessingException {
        // Arrange
        String request = "get key";
        doNothing().when(database).get(anyString());

        // Act
        DatabaseRequest databaseRequest = RequestFactory.parseRequest(request, messageQueue);

        // Assert
        assertTrue(databaseRequest instanceof GetRequest);
        GetRequest getRequest = (GetRequest) databaseRequest;
        assertEquals("key", getRequest.key());

        getRequest.process(database);
        verify(database, times(1)).get(anyString());
    }

    @Test
    public void getRequestParse_exception() {
        // Arrange
        String requestNoValue = "get";
        String requestMoreThanOneParam = "get key key";

        // Act
        assertThrows(InvalidRequestException.class, () -> RequestFactory.parseRequest(requestNoValue, messageQueue));
        assertThrows(InvalidRequestException.class, () -> RequestFactory.parseRequest(requestMoreThanOneParam, messageQueue));
    }

    @Test
    public void setRequestParse_success() throws InvalidRequestException, JsonProcessingException {
        // Arrange
        String message = "set key value";
        doNothing().when(database).set(anyString(), anyString());

        // Act
        DatabaseRequest databaseRequest = RequestFactory.parseRequest(message, messageQueue);

        // Assert
        assertTrue(databaseRequest instanceof SetRequest);
        SetRequest setRequest = (SetRequest) databaseRequest;
        assertEquals("key", setRequest.key());
        assertEquals("value", setRequest.value());

        setRequest.process(database);
        verify(database, times(1)).set(anyString(), anyString());
    }

    @Test
    public void setRequestParse_exception() {
        // Arrange
        String requestNoValue = "set key";
        String requestNoKey = "set value";
        String requestParam = "set";
        String requestMoreThanTwoParam = "set key value extra";

        // Act
        assertThrows(InvalidRequestException.class, () -> RequestFactory.parseRequest(requestNoValue, messageQueue));
        assertThrows(InvalidRequestException.class, () -> RequestFactory.parseRequest(requestNoKey, messageQueue));
        assertThrows(InvalidRequestException.class, () -> RequestFactory.parseRequest(requestParam, messageQueue));
        assertThrows(InvalidRequestException.class, () -> RequestFactory.parseRequest(requestMoreThanTwoParam, messageQueue));
    }

    @Test
    public void syncGetRequestParse_success() throws InvalidRequestException, JsonProcessingException {
        // Arrange
        String message = "sync_get key 1";
        doNothing().when(database).sync(anyString());

        // Act
        DatabaseRequest databaseRequest = RequestFactory.parseRequest(message, messageQueue);

        // Assert
        assertTrue(databaseRequest instanceof SyncGetRequest);
        SyncGetRequest syncGetRequest = (SyncGetRequest) databaseRequest;
        assertEquals("key", syncGetRequest.key());

        syncGetRequest.process(database);
        verify(database, times(1)).sync(anyString());
    }

    @Test
    public void syncGetRequestParse_exception() {
        // Arrange
        String noProcessId = "sync_get key";
        String noKeyAndProcessId = "sync_get";

        // Assert
        assertThrows(InvalidRequestException.class, () -> RequestFactory.parseRequest(noProcessId, messageQueue));
        assertThrows(InvalidRequestException.class, () -> RequestFactory.parseRequest(noKeyAndProcessId, messageQueue));
    }

    @Test
    public void syncSetRequestParse_success() throws JsonProcessingException, InvalidRequestException {
        // Arrange
        ClockValue clockValue = new ClockValue("value", new VectorClock(1));
        String message = "sync_set key " + clockValue.serialize();
        doNothing().when(database).sync(anyString(), any(ClockValue.class));

        // Act
        DatabaseRequest databaseRequest = RequestFactory.parseRequest(message, messageQueue);

        // Assert
        assertTrue(databaseRequest instanceof SyncSetRequest);
        SyncSetRequest syncSetRequest = (SyncSetRequest)  databaseRequest;
        assertEquals("key", syncSetRequest.key());
        assertEquals(clockValue.value(), syncSetRequest.clockValue().value());
        assertEquals(clockValue.vectorClock(), syncSetRequest.clockValue().vectorClock());

        syncSetRequest.process(database);
        verify(database, times(1)).sync(anyString(), any(ClockValue.class));
    }

    @Test
    public void syncSetRequestParse_exception() {
        // Arrange
        String noClockValue = "sync_set key";
        String noKeyValue = "sync_set clock_value";
        String noKeyOrClockValue = "sync_set";

        // Assert
        assertThrows(InvalidRequestException.class, () -> RequestFactory.parseRequest(noClockValue, messageQueue));
        assertThrows(InvalidRequestException.class, () -> RequestFactory.parseRequest(noKeyValue, messageQueue));
        assertThrows(InvalidRequestException.class, () -> RequestFactory.parseRequest(noKeyOrClockValue, messageQueue));
    }
}