package com.example.batallanavalfpoe.model;

public interface IPlainTextFileHandler {
    void writetoFile(String filePath, String content);
    String[] readFromFile(String fileName);

}
