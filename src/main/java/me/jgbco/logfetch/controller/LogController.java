package me.jgbco.logfetch.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import me.jgbco.logfetch.service.LogService;

import java.io.IOException;

@RestController
public class LogController {

    private final LogService logService;

    @Autowired
    public LogController(LogService logService) {
        this.logService = logService;
    }

    @GetMapping("/logs")
    public ResponseEntity<String> getLogs(@RequestParam(defaultValue = "/var/log/system.log") String logFilePath,
                                  @RequestParam(defaultValue = "10") int limit) {
        try {
            String logs = logService.getLogs(logFilePath, limit);
            return ResponseEntity.ok(logs);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error");
        }
    }
}

