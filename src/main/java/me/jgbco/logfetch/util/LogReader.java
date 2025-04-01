package me.jgbco.logfetch.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

@Component
public class LogReader {
    static final int MAX_READ_SIZE = 1048576; // 1 MB reads
    static final char LOG_SEPARATOR = '\n';
    final String path;
    boolean beginningOfFileReached = false;
    ByteBuffer buffer = ByteBuffer.allocate(MAX_READ_SIZE);
    long startOffset = 0L;
    long endOffset = 0L;

    public LogReader(@Value("${log.directory}") String path) {
        this.path = path;
    }

    // For testing
    void setBuffer(ByteBuffer buffer) {
        this.buffer = buffer;
    }

    public void setStartOffset(long startOffset) {
        this.startOffset = startOffset;
    }

    public long getStartOffset() {
        return startOffset;
    }

    public void setEndOffset(long endOffset) {
        this.endOffset = endOffset;
    }

    public long getEndOffset() {
        return endOffset;
    }

    public void readLogFile(String filename, long offset) throws IOException {
        Path logFilePath = Paths.get(path, filename);
        try (FileChannel fileChannel = FileChannel.open(logFilePath, StandardOpenOption.READ)) {
            if (offset == 0) {
                // Start at end of file
                endOffset = fileChannel.size();
            } else {
                // If offset is beyond the end of file, clamp the offset to file size.
                endOffset = Math.min(offset, fileChannel.size());
            }
            if (endOffset < MAX_READ_SIZE) {
                // start at beginning of file
                startOffset = fileChannel.position();
                beginningOfFileReached = true;
            } else {
                startOffset = endOffset - MAX_READ_SIZE;
                beginningOfFileReached = false;
            }
            if (startOffset == endOffset) {
                buffer.clear();
                beginningOfFileReached = true;
                endOffset = -1;
                return;
            }
            buffer.clear();
            fileChannel.position(startOffset);
            fileChannel.read(buffer);
            buffer.flip();
        } catch (IOException e) {
            // Using try-catch here takes care of auto-closable for the file.
            // Rethrowing the exception causes Spring Boot to return an appropriate error response.
            throw e;
        }
    }

    public String nextLog() {
        String log = null;
        if (endOffset == -1) {
            return log;
        }
        int bufIndex = (int)(endOffset - startOffset - 1);
        // Read past any trailing LOG_SEPARATORs
        while (bufIndex >= 0 && buffer.get(bufIndex) == LOG_SEPARATOR) {
            bufIndex--;
            endOffset--;
        }
        int logEndIndex = bufIndex;

        // Find the next LOG_SEPARATOR
        while (bufIndex >= 0 && buffer.get(bufIndex) != LOG_SEPARATOR) {
            bufIndex--;
        }
        if (bufIndex != -1 && buffer.get(bufIndex) == LOG_SEPARATOR) {
            int logSize = logEndIndex - bufIndex;
            byte[] bytes = new byte[logSize];
            int logStartIndex = bufIndex + 1;
            buffer.position(logStartIndex);
            buffer.get(bytes);
            log = new String(bytes, StandardCharsets.UTF_8);
            endOffset = endOffset - logSize;
        } else if (bufIndex == -1 && beginningOfFileReached) {
            int logSize = logEndIndex+1;
            byte[] bytes = new byte[logSize];
            int logStartIndex = 0;
            buffer.position(logStartIndex);
            buffer.get(bytes);
            log = new String(bytes, StandardCharsets.UTF_8);
            endOffset = -1;
        }

        return log;
    }
}