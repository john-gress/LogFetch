package me.jgbco.logfetch.service;

import me.jgbco.logfetch.util.LogReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LogServiceTest {
    private LogReader logReader;
    private LogService logService;

    @BeforeEach
    void setUp() {
        logReader = mock(LogReader.class);
        logService = new LogService(logReader);
    }


    @Test
    void applyFilter_whenLogContainsFilter_shouldReturnTrue() {
        LogService logService = new LogService(null);

        boolean result = logService.applyFilter("Error: Something went wrong", "Error");

        assertTrue(result, "Expected applyFilter to return true when log contains the filter.");
    }

    @Test
    void applyFilter_whenLogDoesNotContainFilter_shouldReturnFalse() {
        LogService logService = new LogService(null);

        boolean result = logService.applyFilter("Info: System running smoothly", "Error");

        assertFalse(result, "Expected applyFilter to return false when log does not contain the filter.");
    }

    @Test
    void applyFilter_whenFilterIsNull_shouldReturnTrue() {
        LogService logService = new LogService(null);

        boolean result = logService.applyFilter("Error: Something went wrong", null);

        assertTrue(result, "Expected applyFilter to return true when filter is null (matches all logs).");
    }

    @Test
    void applyFilter_whenFilterIsEmpty_shouldReturnTrue() {
        LogService logService = new LogService(null);

        boolean result = logService.applyFilter("Error: Something went wrong", "");

        assertTrue(result, "Expected applyFilter to return true when filter is empty (matches all logs).");
    }

    @Test
    void processChunk_readsLogsUntilLimitReached() {
        // Mock logReader.nextLog() to return logs, then null when exhausted
        when(logReader.nextLog())
                .thenReturn("Log 1", "Log 2", "Log 3", null);

        // Process with limit 2
        logService.processChunk(2, "");

        // Assert that only 2 logs were stored
        List<String> logs = logService.getLogs();
        assertEquals(2, logs.size());
        assertEquals("Log 1", logs.get(0));
        assertEquals("Log 2", logs.get(1));

        // Verify logReader.nextLog() was called exactly 3 times
        verify(logReader, times(2)).nextLog();
    }

    @Test
    void processChunk_readsLogsUntilNoMoreLogs() {
        // Mock logReader.nextLog() to return logs, then null when exhausted
        when(logReader.nextLog())
                .thenReturn("Log 1", "Log 2", null);

        // Process with limit 2
        logService.processChunk(3, "");

        // Assert that only 2 logs were stored
        List<String> logs = logService.getLogs();
        assertEquals(2, logs.size());
        assertEquals("Log 1", logs.get(0));
        assertEquals("Log 2", logs.get(1));

        // Verify logReader.nextLog() was called exactly 3 times
        verify(logReader, times(3)).nextLog();
    }

    @Test
    void processChunk_withZeroLimit_doesNotProcessLogs() {
        // Mock logReader.nextLog() to return a log, then null when exhausted
        when(logReader.nextLog()).thenReturn("Log A", null);

        // Process with limit 0
        logService.processChunk(0, "");

        // Assert no logs are added
        assertTrue(logService.logs.isEmpty());

        // Verify that nextLog() was not called
        verify(logReader, times(0)).nextLog();
    }

    @Test
    void readLogFile_withMoreLogsThanRequested_readsLogsUntilLimitReached() throws IOException {
        // Mock logReader.nextLog() to return 4 logs, then null when exhausted
        when(logReader.nextLog()).thenReturn("Info: Log A", "Info: Log B", null, "Info: Log C", "Info: Log D", null);
        when(logReader.getEndOffset()).thenReturn(50L, -1L);

        // Process with limit 3
        logService.readLogFile("system.log", 100, 3, "Info");

        // Assert that 3 logs were stored
        List<String> logs = logService.getLogs();
        assertEquals(3, logs.size());
        assertEquals("Info: Log A", logs.get(0));
        assertEquals("Info: Log B", logs.get(1));
        assertEquals("Info: Log C", logs.get(2));

        // Verify logReader.nextLog() was called exactly 4 times
        verify(logReader, times(4)).nextLog();
        // Verify logReader.getEndOffset() was called twice
        verify(logReader, times(2)).getEndOffset();
    }

    @Test
    void readLogFile_withLessLogsThanRequested_readsAllLogs() throws IOException {
        // Mock logReader.nextLog() to return 4 logs, then null when exhausted
        when(logReader.nextLog()).thenReturn("Info: Log A", "Info: Log B", null);
        when(logReader.getEndOffset()).thenReturn(-1L);

        // Process with limit 3
        logService.readLogFile("system.log", 100, 3, "Info");

        // Assert that 2 logs were stored
        List<String> logs = logService.getLogs();
        assertEquals(2, logs.size());
        assertEquals("Info: Log A", logs.get(0));
        assertEquals("Info: Log B", logs.get(1));

        // Verify logReader.nextLog() was called exactly 3 times
        verify(logReader, times(3)).nextLog();
        // Verify logReader.getEndOffset() was called once
        verify(logReader, times(1)).getEndOffset();
    }

}

