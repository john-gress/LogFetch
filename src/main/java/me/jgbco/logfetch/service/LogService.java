package me.jgbco.logfetch.service;

import org.springframework.stereotype.Service;
import me.jgbco.logfetch.util.LogReader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class LogService {

    private final LogReader logReader;

    public LogService(LogReader logReader) {
        this.logReader = logReader;
    }

    public List<String> getLogs(String logFilePath, long offset, int limit, String filter) throws IOException {
        String log = logReader.readLogFile(logFilePath);
        List<String> logs = new ArrayList<>();
        logs.add(log);
        return logs;
    }
}
