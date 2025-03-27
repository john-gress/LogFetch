package me.jgbco.logfetch.service;

import org.springframework.stereotype.Service;
import me.jgbco.logfetch.util.LogReader;

import java.io.IOException;

@Service
public class LogService {

    private final LogReader logReader;

    public LogService(LogReader logReader) {
        this.logReader = logReader;
    }

    public String getLogs(String logFilePath, int limit) throws IOException {
        return logReader.readLogFile(logFilePath);
    }
}
