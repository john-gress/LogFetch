package me.jgbco.logfetch.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;

@Component
public class LogReader {
    static final int MAX_READ_SIZE = 8192; // 8 KB reads
    byte[] buffer = new byte[MAX_READ_SIZE];
    final String path;
    long startOffset = 0L;
    long endOffset = 0L;

    public LogReader(@Value("${log.directory}") String path) {
        this.path = path;
    }

    public long getEndOffset() {
        return endOffset;
    }

    public void readLogFile(String filename, long offset) throws IOException {
        /*
        String logFilePath = path + filename;
        try (RandomAccessFile file = new RandomAccessFile(logFilePath, "r")) {
            if (offset == 0) {
                endOffset = file.length();
            } else {
                endOffset = offset;
            }
            if (file.length() < MAX_READ_SIZE) {
                startOffset = file.getFilePointer();
            } else {
                startOffset = endOffset - MAX_READ_SIZE;
            }
            if (startOffset == endOffset) {
                return;
            }
            file.seek(startOffset);
            file.read(buffer);
        } catch (IOException e) {
            throw e;
        }
        */
    }

    public String nextLog() {
        return "testing";
    }
}