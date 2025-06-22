package com.company.datacollector;

import com.company.datasets.BossDropDataSet;
import com.company.datasets.loot.GemLoot;
import com.company.datasets.loot.Loot;
import com.company.datasets.loot.StackableLoot;
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

import static com.company.datasets.loot.LootType.*;

public class BossDropDataCollectorTest {

    private Map<String, String> actions;

    private BossDropDataCollectorNew collector;

    @BeforeEach
    void setup() {
        collector = new BossDropDataCollectorNew();
        actions = DataCollectorNew.getActions();
    }

    @Test
    void add_oneDataSet() throws IOException {
        Path tempfile = Files.createTempFile("test", ".txt");
        tempfile.toFile().deleteOnExit();

        String inputs = "3" + System.lineSeparator() +
                actions.get("AddData") + System.lineSeparator() +
                "The Maven" + System.lineSeparator() +
                "x" + System.lineSeparator() + "n" + System.lineSeparator() +
                "y" + System.lineSeparator() +
                "Graven's Secret;bossunique" + System.lineSeparator() +
                "Awakened Multistrike Support;gemawakened" + System.lineSeparator() + "Orb of Conflict;currency;1" + System.lineSeparator() + System.lineSeparator() +
                actions.get("Exit");
        IOUtils.setInputStream(inputs);

        BossDropDataSet dataSet = new BossDropDataSet(
                new Strategy(3, "3.25", null, null, null, null, null),
                "The Maven",
                false,
                true,
                new Loot("Graven's Secret", BOSS_UNIQUE_ITEM),
                List.of(
                        new GemLoot("Awakened Multistrike Support", GEM_AWAKENED),
                        new StackableLoot("Orb of Conflict", CURRENCY, 1)
                )
        );

        // when
        collector.collectData(tempfile.toString());

        // then
        List<String> content = Files.readAllLines(tempfile).stream()
                .filter(s -> s.trim().length() > 0).collect(Collectors.toList());
        Assertions.assertEquals(1, content.size());
        Assertions.assertEquals(Utils.toJson(dataSet), content.get(0).trim());
    }
}
