package com.company.datacollector;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DataCollectorTest {
    
    protected static final String LINEBREAK = System.lineSeparator();

    protected Map<String, String> actions;

    private DataCollectorNew genericCollector;

    protected Path tempfile;

    protected Path outputFile;

    private PrintStream oldStream;

    @BeforeEach
    void setup_All() throws IOException {
        actions = DataCollectorNew.getActions();

        tempfile = Files.createTempFile("test", ".txt");
        tempfile.toFile().deleteOnExit();

        oldStream = System.out;
        outputFile = Files.createTempFile("output", ".txt");
        outputFile.toFile().deleteOnExit();
        System.setOut(new PrintStream(outputFile.toFile()));
    }

    @AfterEach
    void cleanup_All() {
        System.setOut(oldStream);
    }

    protected List<String> getContent() throws IOException {
        return Files.readAllLines(tempfile).stream()
                .filter(s -> s.trim().length() > 0).collect(Collectors.toList());
    }

    protected List<String> getOutputs() throws IOException {
        return Files.readAllLines(outputFile);
    }
}
