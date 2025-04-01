package me.jgbco.logfetch.controller;


import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import me.jgbco.logfetch.service.LogService;

import java.util.List;
import java.util.Map;

@RestController
public class LogController {

    private final LogService logService;
    static final int MAX_LIMIT = 10000;
    static final String MAX_LIMIT_ERROR_MSG = "Max limit is " + MAX_LIMIT;
    static final int MIN_OFFSET = 0;
    static final String MIN_OFFSET_ERROR_MSG = "Offset can not be negative";
    static final String INVALID_FILENAME_ERROR_MSG = "Invalid log file name";

    @Autowired
    public LogController(LogService logService) {
        this.logService = logService;
    }

    @GetMapping("/logs")
    public ResponseEntity<Map<String, Object>> getLogs (
            @RequestParam("logFile") @Pattern(regexp = "^[\\.a-zA-Z0-9_-]+$", message = INVALID_FILENAME_ERROR_MSG) String logFile,
            @RequestParam(name = "offset", defaultValue = "0") @Min(value = MIN_OFFSET, message = MIN_OFFSET_ERROR_MSG) long offset,
            @RequestParam(name = "limit", defaultValue = "10") @Max(value = MAX_LIMIT, message = MAX_LIMIT_ERROR_MSG) int limit,
            @RequestParam(name = "filter", required = false) String filter)
            throws Exception {

            //System.out.println("logFile: " + logFile + ", offset: " + offset + ", limit: " + limit + ", filter: " + filter);
            logService.readLogFile(logFile, offset, limit, filter);
            List<String> logs = logService.getLogs();
            long nextOffset = logService.getEndOffset();
            Map<String, Object> responseBody = Map.of("logs", logs, "nextOffset", nextOffset);

            return ResponseEntity.ok(responseBody);
    }
}