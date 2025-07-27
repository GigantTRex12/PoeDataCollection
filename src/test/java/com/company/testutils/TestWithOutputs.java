package com.company.testutils;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class TestWithOutputs {

    private Path outputFile;

    private PrintStream oldStream;

    @BeforeEach
    void setup_Output() throws IOException {
        oldStream = System.out;
        outputFile = Files.createTempFile("output", ".txt");
        outputFile.toFile().deleteOnExit();
        System.setOut(new PrintStream(outputFile.toFile()));
    }

    @AfterEach
    void cleanup_Output() {
        System.setOut(oldStream);
    }

    protected List<String> getOutputs() throws IOException {
        return Files.readAllLines(outputFile);
    }

    protected void validateOutputs(String[] expectedOutputs, int minPos) throws IOException {
        List<String> output = getOutputs();
        int pos = output.indexOf("What would you like to do?");
        Assertions.assertTrue(pos > minPos);
        pos += 2;
        for (String expected : expectedOutputs) {
            if (expected != null) {
                Assertions.assertEquals(expected, output.get(pos));
            }
            pos++;
        }
        pos++;
        Assertions.assertEquals(pos, output.size());
    }

    protected void validateOutputs(String[] expectedOutputs) throws IOException {
        validateOutputs(expectedOutputs, -1);
    }

}
