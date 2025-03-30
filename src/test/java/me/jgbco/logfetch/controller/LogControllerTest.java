package me.jgbco.logfetch.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import me.jgbco.logfetch.service.LogService;

import java.util.Arrays;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
public class LogControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @TestConfiguration
    static class LogServiceTestConfig {
        @Bean
        public LogService logService() {
            return mock(LogService.class); // Manually create mock
        }
    }

    @Autowired
    private LogService logService;


    @Test
    void testGetLogs() throws Exception {
        // Mock the service method to return a predefined list of log entries
        String logFile = "syslog";  // Example filename within /var/log
        long offset = 0;
        int limit = 10;
        String filter = "error";  // Example filter keyword

        // Predefined logs to be returned by the mock
        String logEntry1 = "Error: Something went wrong!";
        String logEntry2 = "Error: Another issue occurred.";
        when(logService.getLogs(logFile, offset, limit, filter)).thenReturn(Arrays.asList(logEntry1, logEntry2));

        // Perform a GET request to /logs with parameters
        mockMvc.perform(MockMvcRequestBuilders.get("/logs")
                        .param("logFile", logFile)
                        .param("offset", String.valueOf(offset))
                        .param("limit", String.valueOf(limit))
                        .param("filter", filter))  // Optional filter parameter
                .andExpect(MockMvcResultMatchers.status().isOk())  // Expect a 200 OK status
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))  // Expect JSON response
                .andExpect(MockMvcResultMatchers.jsonPath("$.logs").isArray())  // Ensure the logs are in an array
                .andExpect(MockMvcResultMatchers.jsonPath("$.logs.length()").value(2))  // We mocked 2 logs
                .andExpect(MockMvcResultMatchers.jsonPath("$.logs[0]").value(logEntry1))  // Ensure first log entry is correct
                .andExpect(MockMvcResultMatchers.jsonPath("$.logs[1]").value(logEntry2))  // Ensure second log entry is correct
                .andExpect(MockMvcResultMatchers.jsonPath("$.nextOffset").value(-1));  // Ensure nextOffset logic is correct
    }
}
