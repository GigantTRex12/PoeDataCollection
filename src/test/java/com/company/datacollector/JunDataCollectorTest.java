package com.company.datacollector;

import com.company.datasets.datasets.jundataset.JunDataSet;
import com.company.testutils.InputBuilder;
import com.company.utils.FileUtils;
import com.company.utils.Utils;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class JunDataCollectorTest {

    @Test
    @Disabled
    void debuggingTest() throws FileNotFoundException, JsonProcessingException {
        InputBuilder.start()
                .line(0)
                .line("y")
                .line(5)
                .line("a")
                .line("m")
                .line("m")
                .line("Transportation: Vagan(2B);Gravicius(2);Riker(1);Cameria(1)")
                .multiLine(new String[]{
                        "Gravicius kill",
                        "Riker kill",
                        "Cameria kill",
                        "Vagan Bargain(Destroy(T))"
                })
                .multiLine()
                .line("Intervention: Haku(3B);Hillock(1);Tora(2)")
                .multiLine(new String[]{
                        "Hillock kill",
                        "Tora Interrogate",
                        "Haku Bargain(Map)"
                })
                .multiLine()
                .line("Research: Elreon(3B);Janus(2);Haku(3)")
                .multiLine(new String[]{
                        "Janus kill",
                        "Elreon kill",
                        "Haku Bargain(14R)"
                })
                .multiLine()
                .set();

        String filename = "Data/junEncounters.txt";

        DataCollector<JunDataSet> collector = new JunDataCollector(filename);
        Assertions.assertDoesNotThrow(() -> collector.collectData(filename));
    }

    @Test
    @Disabled
    void debuggingTest2() throws FileNotFoundException, JsonProcessingException {
        String filename = "Data/junEncounters.txt";

        List<String> previousReps = Arrays.stream(FileUtils.read(filename).split("\\n"))
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());

        List<JunDataSet> previousDataSets = new ArrayList<>();
        for (String previousRep : previousReps) {
            previousDataSets.add(Utils.parseJson(previousRep, JunDataSet.class));
        }

        Assertions.assertEquals(6, previousDataSets.size());
    }

}
