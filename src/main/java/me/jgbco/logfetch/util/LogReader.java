package me.jgbco.logfetch.util;

import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

@Component
public class LogReader {

    public String readLogFile(String logFilePath) throws IOException {
        return "testing";
    }
}
