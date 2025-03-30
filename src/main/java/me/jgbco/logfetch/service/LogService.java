package me.jgbco.logfetch.service;

import org.springframework.stereotype.Service;
import me.jgbco.logfetch.util.LogReader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class LogService {

    private final LogReader logReader;

    List<String> logs = new ArrayList<>();
    long endOffset = 0L;

    public LogService(LogReader logReader) {
        this.logReader = logReader;
    }

    public List<String> getLogs() {
        return logs;
    }

    public long getEndOffset() {
        return endOffset;
    }

    public void readLogFile(String logFilePath, long offset, int limit, String filter) throws IOException {
        endOffset = offset;
        logs.clear();
        while (logs.size() < limit && endOffset != -1) {
            logReader.readLogFile(logFilePath, endOffset);
            processChunk(limit, filter);
            endOffset = logReader.getEndOffset();
        }
    }

    void processChunk(int limit, String filter) {
        boolean chunkProcessed = false;
        while (logs.size() < limit && !chunkProcessed) {
            String log = logReader.nextLog();
            if (log == null) {
                chunkProcessed = true;
            } else if (applyFilter(log, filter)) {
                logs.add(log);
            }
        }
    }

    boolean applyFilter(String log, String filter) {
        if (filter == null || filter.isEmpty()) {
            return true;
        }
        return log.contains(filter);
    }
}
