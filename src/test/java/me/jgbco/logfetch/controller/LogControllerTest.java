package me.jgbco.logfetch.controller;

import me.jgbco.logfetch.service.LogService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.IOException;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@SpringBootTest
@AutoConfigureMockMvc
public class LogControllerTest {

    @Autowired
    private LogController logController; // Autowire the controller

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



    @BeforeEach
    public void setup() {
        // Set up MockMvc with the LogController
        mockMvc = MockMvcBuilders.standaloneSetup(logController).build();
    }

    @Test
    public void testGetLogs() throws Exception {
        // Mock the logReader behavior (simulate reading the log)
        String mockLogContent = "Log #1.\nLog #2.\n";
        when(logService.getLogs("/var/log/system.log", 10)).thenReturn(mockLogContent);

        // Perform the GET request on the /logs endpoint and verify the response
        mockMvc.perform(get("/logs"))
                .andExpect(status().isOk()) // Expect HTTP 200 OK
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN)) // Expect text content type
                .andExpect(content().string(mockLogContent)); // Verify that the content matches the mock response
    }

    @Test
    public void testGetLogs_fileNotFound() throws Exception {
        // Simulate the case where the file cannot be read (e.g., file not found)
        when(logService.getLogs("/var/log/system.log", 10)).thenThrow(new IOException("File not found"));

        // Perform the GET request and verify the response for an error
        mockMvc.perform(get("/logs"))
                .andExpect(status().isInternalServerError()) // Expect HTTP 500
                .andExpect(content().string("Internal Server Error"));
    }
}