package com.company.datacollector;

import com.company.datasets.datasets.BossDropDataSet;
import com.company.datasets.other.loot.GemLoot;
import com.company.datasets.other.loot.Loot;
import com.company.datasets.other.loot.StackableLoot;
import com.company.utils.IOUtils;
import com.company.utils.Utils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static com.company.datasets.other.loot.LootType.*;

public class BossDropDataCollectorTest extends DataCollectorTest{

    private BossDropDataCollectorNew collector;

    @BeforeEach
    void setup() {
        collector = new BossDropDataCollectorNew();
    }

    @Test
    void add_oneDataSet() throws IOException {
        // given
        String inputs = "0" + LINEBREAK +
                actions.get("AddData") + LINEBREAK +
                "The Maven" + LINEBREAK +
                "n" + LINEBREAK +
                "y" + LINEBREAK +
                "Graven's Secret;bossunique" + LINEBREAK +
                "Awakened Multistrike Support;gemawakened" + LINEBREAK + "Orb of Conflict;currency;1" + LINEBREAK + LINEBREAK +
                actions.get("Exit");
        IOUtils.setInputStream(inputs);

        BossDropDataSet dataSet = new BossDropDataSet(
                nullStrat,
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
        List<String> content = getContent();
        Assertions.assertEquals(1, content.size());
        Assertions.assertEquals(Utils.toJson(dataSet), content.get(0).trim());

        // validate console outputs
        List<String> output = getOutputs();
        int pos = output.indexOf("What would you like to do?");
        Assertions.assertTrue(pos > 2);
        Assertions.assertEquals("Enter the name of the boss.", output.get(pos + 2));
        Assertions.assertEquals("Is the boss uber?", output.get(pos + 3));
        Assertions.assertEquals("Is the boss a pinnacle boss?", output.get(pos + 5));
        Assertions.assertEquals("What was the guaranteed drop?", output.get(pos + 7));
        Assertions.assertEquals("Input extra drops to track.", output.get(pos + 8));
        Assertions.assertEquals("What would you like to do?", output.get(pos + 9));
        Assertions.assertEquals(pos + 11, output.size());
    }

    @Test
    void add_multipleDataSets() throws IOException {
        // given
        String inputs = "0" + LINEBREAK +
                actions.get("AddData") + LINEBREAK +
                "The Maven" + LINEBREAK +
                "n" + LINEBREAK +
                "y" + LINEBREAK +
                "Graven's Secret;bossunique" + LINEBREAK +
                "Awakened Multistrike Support;gemawakened" + LINEBREAK + "Orb of Conflict;currency" + LINEBREAK + LINEBREAK +
                actions.get("AddData") + LINEBREAK +
                "The Infinite Hunger" + LINEBREAK +
                "n" + LINEBREAK +
                "y" + LINEBREAK +
                LINEBREAK +
                LINEBREAK +
                actions.get("Exit");
        IOUtils.setInputStream(inputs);

        BossDropDataSet dataSet1 = new BossDropDataSet(
                nullStrat,
                "The Maven",
                false,
                true,
                new Loot("Graven's Secret", BOSS_UNIQUE_ITEM),
                List.of(
                        new GemLoot("Awakened Multistrike Support", GEM_AWAKENED),
                        new StackableLoot("Orb of Conflict", CURRENCY, 1)
                )
        );
        BossDropDataSet dataSet2 = new BossDropDataSet(
                nullStrat,
                "The Infinite Hunger",
                false,
                true,
                null,
                List.of()
        );

        // when
        collector.collectData(tempfile.toString());

        // then
        List<String> content = getContent();
        Assertions.assertEquals(2, content.size());
        Assertions.assertEquals(Utils.toJson(dataSet1), content.get(0).trim());
        Assertions.assertEquals(Utils.toJson(dataSet2), content.get(1).trim());

        // validate console outputs
        List<String> output = getOutputs();
        int pos = output.indexOf("What would you like to do?");
        Assertions.assertTrue(pos > 2);
        Assertions.assertEquals("Enter the name of the boss.", output.get(pos + 2));
        Assertions.assertEquals("Is the boss uber?", output.get(pos + 3));
        Assertions.assertEquals("Is the boss a pinnacle boss?", output.get(pos + 5));
        Assertions.assertEquals("What was the guaranteed drop?", output.get(pos + 7));
        Assertions.assertEquals("Input extra drops to track.", output.get(pos + 8));
        Assertions.assertEquals("What would you like to do?", output.get(pos + 9));
        Assertions.assertEquals("Enter the name of the boss.", output.get(pos + 11));
        Assertions.assertEquals("Is the boss uber?", output.get(pos + 12));
        Assertions.assertEquals("Is the boss a pinnacle boss?", output.get(pos + 14));
        Assertions.assertEquals("What was the guaranteed drop?", output.get(pos + 16));
        Assertions.assertEquals("Input extra drops to track.", output.get(pos + 17));
        Assertions.assertEquals("What would you like to do?", output.get(pos + 18));
        Assertions.assertEquals(pos + 20, output.size());
    }

    @Disabled("Functionality not yet implemented so test would fail")
    @Test
    void add_attemptInvalidInputs() throws IOException {
        // given
        String inputs = "0" + LINEBREAK +
                actions.get("AddData") + LINEBREAK +
                "The Maven" + LINEBREAK +
                "x" + LINEBREAK + "y" + LINEBREAK +
                "y" + LINEBREAK +
                "randomthing" + LINEBREAK + "Impossible Escape;bossunique" + LINEBREAK +
                "invalid" + LINEBREAK + "  " + LINEBREAK + "invalid;invalid" + LINEBREAK + "Awakened Enlighten Support;gemawakened" + LINEBREAK + "Orb of Conflict;currency;zwei" + LINEBREAK + LINEBREAK +
                actions.get("Exit");
        IOUtils.setInputStream(inputs);

        BossDropDataSet dataSet = new BossDropDataSet(
                nullStrat,
                "The Maven",
                true,
                true,
                new Loot("Graven's Secret", BOSS_UNIQUE_ITEM),
                List.of(
                        new GemLoot("Awakened Enlighten Support", GEM_AWAKENED)
                )
        );

        // when
        collector.collectData(tempfile.toString());

        // then
        List<String> content = getContent();
        Assertions.assertEquals(1, content.size());
        Assertions.assertEquals(Utils.toJson(dataSet), content.get(0).trim());

        // validate console outputs
        List<String> output = getOutputs();
        int pos = output.indexOf("What would you like to do?");
        Assertions.assertTrue(pos > 2);
        String invalid = "Invalid input, try again";
        Assertions.assertEquals("Enter the name of the boss.", output.get(pos + 2));
        Assertions.assertEquals("Is the boss uber?", output.get(pos + 3));
        Assertions.assertEquals(invalid, output.get(pos + 5));
        Assertions.assertEquals("Is the boss uber?", output.get(pos + 6));
        Assertions.assertEquals("Is the boss a pinnacle boss?", output.get(pos + 8));
        Assertions.assertEquals("What was the guaranteed drop?", output.get(pos + 10));
        Assertions.assertEquals(invalid, output.get(pos + 11));
        Assertions.assertEquals("What was the guaranteed drop?", output.get(pos + 12));
        Assertions.assertEquals("Input extra drops to track.", output.get(pos + 13));
        Assertions.assertEquals("Couldn't parse \"invalid\" to Loot. (skipped)", output.get(pos + 14));
        Assertions.assertEquals("Couldn't parse \"  \" to Loot. (skipped)", output.get(pos + 15));
        Assertions.assertEquals("Couldn't parse \"invalid;invalid\" to Loot. (skipped)", output.get(pos + 16));
        Assertions.assertEquals("Couldn't parse \"Orb of Conflict;currency;zwei\" to Loot. (skipped)", output.get(pos + 17));
        Assertions.assertEquals("What would you like to do?", output.get(pos + 18));
        Assertions.assertEquals(pos + 20, output.size());
    }
}
