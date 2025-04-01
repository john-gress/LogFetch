package me.jgbco.logfetch.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.mockito.Mockito.doThrow;

public class LogReaderTest {
    @Test
    public void readLogFile_givenKnownTestFileLessThan8KB_readsEntireFile()
            throws IOException, URISyntaxException {
        long expectedEndOffset = 143; // Just happen to know it is that long
        Path testPath = Paths.get(getClass().getClassLoader().getResource("log").toURI());
        LogReader logReader = new LogReader(testPath.toString());
        logReader.readLogFile("logfetch.log", 0);
        // Perform assertions
        Assertions.assertEquals(0, logReader.getStartOffset());
        Assertions.assertEquals(expectedEndOffset, logReader.getEndOffset());
    }

    @Test
    public void readLogFile_givenNonExistentFile_throwsNoSuchFileException()
            throws IOException, URISyntaxException {
        Path testPath = Paths.get(getClass().getClassLoader().getResource("log").toURI());
        LogReader logReader = new LogReader(testPath.toString());
        String missingFile = "nonexistent.log";

        Assertions.assertThrows(NoSuchFileException.class, () -> {
            logReader.readLogFile(missingFile, 0);
        }, "Expected NoSuchFileException when reading a nonexistent file");
    }

    @Test
    public void readLogFile_givenEmptyFile_setsCorrectState() throws IOException, URISyntaxException {
        Path testPath = Paths.get(getClass().getClassLoader().getResource("log").toURI());
        LogReader logReader = new LogReader(testPath.toString());
        logReader.readLogFile("emptyFile.log", 0);
        // Perform assertions
        Assertions.assertEquals(-1, logReader.getEndOffset());
        Assertions.assertNull(logReader.nextLog());
    }

    @Test
    public void nextLog_givenKnownTestFileLessThan8KB_returnsAllLogs() throws IOException, URISyntaxException {
        String expectedLog10 = "4 Info, 3 Error, 2 Warning";
        String expectedLog9 = "Info: Log 9";
        String expectedLog8 = "Error: Log 8";
        String expectedLog7 = "Info: Log 7";
        String expectedLog6 = "Info: Log 6";
        String expectedLog5 = "Error: Log 5";
        String expectedLog4 = "Warning: Log 4";
        String expectedLog3 = "Info: Log 3";
        String expectedLog2 = "Warning: Log 2";
        String expectedLog1 = "Error: Log 1";
        Path testPath = Paths.get(getClass().getClassLoader().getResource("log").toURI());
        LogReader logReader = new LogReader(testPath.toString());
        logReader.readLogFile("logfetch.log", 0);
        // Perform assertions
        Assertions.assertEquals(expectedLog1, logReader.nextLog());
        Assertions.assertEquals(expectedLog2, logReader.nextLog());
        Assertions.assertEquals(expectedLog3, logReader.nextLog());
        Assertions.assertEquals(expectedLog4, logReader.nextLog());
        Assertions.assertEquals(expectedLog5, logReader.nextLog());
        Assertions.assertEquals(expectedLog6, logReader.nextLog());
        Assertions.assertEquals(expectedLog7, logReader.nextLog());
        Assertions.assertEquals(expectedLog8, logReader.nextLog());
        Assertions.assertEquals(expectedLog9, logReader.nextLog());
        Assertions.assertEquals(expectedLog10, logReader.nextLog());
        Assertions.assertNull(logReader.nextLog());
        Assertions.assertEquals(-1, logReader.getEndOffset());
    }

    @Test
    public void readLogFile_givenOffsetBeyondEndOfFile_readsFromEndOfFile() throws IOException, URISyntaxException {
        String expectedLog10 = "4 Info, 3 Error, 2 Warning";
        String expectedLog9 = "Info: Log 9";
        String expectedLog8 = "Error: Log 8";
        String expectedLog7 = "Info: Log 7";
        String expectedLog6 = "Info: Log 6";
        String expectedLog5 = "Error: Log 5";
        String expectedLog4 = "Warning: Log 4";
        String expectedLog3 = "Info: Log 3";
        String expectedLog2 = "Warning: Log 2";
        String expectedLog1 = "Error: Log 1";
        Path testPath = Paths.get(getClass().getClassLoader().getResource("log").toURI());
        LogReader logReader = new LogReader(testPath.toString());
        logReader.readLogFile("logfetch.log", 1000);
        // Perform assertions
        Assertions.assertEquals(expectedLog1, logReader.nextLog());
        Assertions.assertEquals(expectedLog2, logReader.nextLog());
        Assertions.assertEquals(expectedLog3, logReader.nextLog());
        Assertions.assertEquals(expectedLog4, logReader.nextLog());
        Assertions.assertEquals(expectedLog5, logReader.nextLog());
        Assertions.assertEquals(expectedLog6, logReader.nextLog());
        Assertions.assertEquals(expectedLog7, logReader.nextLog());
        Assertions.assertEquals(expectedLog8, logReader.nextLog());
        Assertions.assertEquals(expectedLog9, logReader.nextLog());
        Assertions.assertEquals(expectedLog10, logReader.nextLog());
        Assertions.assertNull(logReader.nextLog());
        Assertions.assertEquals(-1, logReader.getEndOffset());
    }

    @Test
    public void nextLog_givenBlankLines_parsesLogs() throws URISyntaxException {
        ByteBuffer buffer = ByteBuffer.allocate(LogReader.MAX_READ_SIZE);
        String bufferContent = "Log size 11\nLog 3\nLog 2\n\n\nLog 1";
        buffer.put(bufferContent.getBytes());
        Path testPath = Paths.get(getClass().getClassLoader().getResource("log").toURI());
        LogReader logReader = new LogReader(testPath.toString());
        logReader.setBuffer(buffer);
        long startOffset = 10L;
        long endOffset = bufferContent.length() + startOffset;
        logReader.setStartOffset(10);
        logReader.setEndOffset(endOffset);
        long expectedEndOffset = startOffset + 11; // first 11 chars in buffer
        String expectedLog1 = "Log 1"; // last log is first
        String expectedLog2 = "Log 2";
        String expectedLog3 = "Log 3";

        Assertions.assertEquals(expectedLog1, logReader.nextLog());
        Assertions.assertEquals(expectedLog2, logReader.nextLog());
        Assertions.assertEquals(expectedLog3, logReader.nextLog());
        Assertions.assertNull(logReader.nextLog());
        Assertions.assertEquals(expectedEndOffset, logReader.getEndOffset());
    }
}
