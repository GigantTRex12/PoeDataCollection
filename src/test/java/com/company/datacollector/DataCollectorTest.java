package com.company.datacollector;

import com.company.datasets.other.metadata.Strategy;
import com.company.testutils.TestWithOutputs;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DataCollectorTest extends TestWithOutputs {

    @Deprecated(forRemoval = true)
    protected static final String LINEBREAK = System.lineSeparator();

    protected static final Strategy nullStrat = new Strategy(null, null, null, null, null, null, null, null);

    protected static final String INVALID = "Invalid input, try again";

    protected Map<String, String> actions;

    protected Path tempfile;

    @BeforeEach
    void setup_Content() throws IOException {
        actions = DataCollector.getActions();

        tempfile = Files.createTempFile("test", ".txt");
        tempfile.toFile().deleteOnExit();
    }

    protected List<String> getContent() throws IOException {
        return Files.readAllLines(tempfile).stream()
                .filter(s -> s.trim().length() > 0).collect(Collectors.toList());
    }

    @Override
    protected void validateOutputs(String[] expectedOutputs) throws IOException {
        validateOutputs(expectedOutputs, 2);
    }

}
