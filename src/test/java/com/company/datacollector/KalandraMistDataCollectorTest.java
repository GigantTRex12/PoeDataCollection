package com.company.datacollector;

import com.company.datasets.KalandraMistDataSet;
import com.company.datasets.loot.LootType;
import com.company.datasets.metadata.Strategy;
import com.company.utils.IOUtils;
import com.company.utils.Utils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class KalandraMistDataCollectorTest {

    private Map<String, String> actions;

    private KalandraMistDatacollectorNew collector;

    @BeforeEach
    void setup() {
        collector = new KalandraMistDatacollectorNew();
        actions = DataCollectorNew.getActions();
    }

    @Test
    void add_oneDataSet() throws IOException {
        // given
        Path tempfile = Files.createTempFile("test", ".txt");
        tempfile.toFile().deleteOnExit();

        String inputs = "3" + System.lineSeparator() +
                        actions.get("AddData") + System.lineSeparator() +
                        "in map" + System.lineSeparator() +
                        "2/3" + System.lineSeparator() +
                        "1.9" + System.lineSeparator() +
                        "r" + System.lineSeparator() +
                        "This is a dummy text." + System.lineSeparator() + "This is only for tests." + System.lineSeparator() + System.lineSeparator() +
                        actions.get("Exit");
        IOUtils.setInputStream(inputs);

        KalandraMistDataSet dataSet = new KalandraMistDataSet(
                new Strategy(3, "3.25", null, null, null, null, null),
                KalandraMistDataSet.MistType.IN_MAP, null,
                2, 3, 0,
                "This is a dummy text.\nThis is only for tests.",
                LootType.RARE_JEWELLRY_RING,
                "1.9"
        );

        // when
        collector.collectData(tempfile.toString());

        // then
        List<String> content = Files.readAllLines(tempfile).stream()
                .filter(s -> s.trim().length() > 0).collect(Collectors.toList());
        Assertions.assertEquals(1, content.size());
        Assertions.assertEquals(Utils.toJson(dataSet), content.get(0).trim());
    }

    @Test
    void add_multipleDataSets() throws IOException {
        // given
        Path tempfile = Files.createTempFile("test", ".txt");
        tempfile.toFile().deleteOnExit();

        String inputs ="3" + System.lineSeparator() +
                actions.get("AddData") + System.lineSeparator() +
                "in map" + System.lineSeparator() +
                "2/3" + System.lineSeparator() +
                "1.9" + System.lineSeparator() +
                "r" + System.lineSeparator() +
                "This is a dummy text." + System.lineSeparator() + "This is only for tests." + System.lineSeparator() + System.lineSeparator() +
                actions.get("AddData") + System.lineSeparator() +
                "itemized" + System.lineSeparator() +
                "2/1/1" + System.lineSeparator() +
                "2.1" + System.lineSeparator() +
                "a" + System.lineSeparator() +
                "This is another dummy text." + System.lineSeparator() + "This is only for tests." + System.lineSeparator() + System.lineSeparator() +
                actions.get("Exit");
        IOUtils.setInputStream(inputs);

        KalandraMistDataSet dataSet1 = new KalandraMistDataSet(
                new Strategy(3, "3.25", null, null, null, null, null),
                KalandraMistDataSet.MistType.IN_MAP, null,
                2, 3, 0,
                "This is a dummy text.\nThis is only for tests.",
                LootType.RARE_JEWELLRY_RING,
                "1.9"
        );
        KalandraMistDataSet dataSet2 = new KalandraMistDataSet(
                new Strategy(3, "3.25", null, null, null, null, null),
                KalandraMistDataSet.MistType.ITEMIZED, null,
                2, 1, 1,
                "This is another dummy text.\nThis is only for tests.",
                LootType.RARE_JEWELLRY_AMULET,
                "2.1"
        );

        // when
        collector.collectData(tempfile.toString());

        // then
        List<String> content = Files.readAllLines(tempfile).stream()
                .filter(s -> s.trim().length() > 0).collect(Collectors.toList());
        Assertions.assertEquals(2, content.size());
        Assertions.assertEquals(Utils.toJson(dataSet1), content.get(0).trim());
        Assertions.assertEquals(Utils.toJson(dataSet2), content.get(1).trim());
    }
}
