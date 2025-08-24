package com.company.datacollector;

import com.company.datasets.datasets.BossDropDataSet;
import com.company.datasets.other.loot.GemLoot;
import com.company.datasets.other.loot.Loot;
import com.company.datasets.other.loot.StackableLoot;
import com.company.testutils.InputBuilder;
import com.company.utils.IOUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.util.List;

import static com.company.datasets.other.loot.LootType.*;
import static com.company.utils.Utils.toJson;

public class BossDropDataCollectorTest extends DataCollectorTest{

    private BossDropDataCollector collector;

    @BeforeEach
    void setup() {
        collector = new BossDropDataCollector();
    }

    @ParameterizedTest
    @MethodSource("provideAddActions")
    void add_oneDataSet(String addAction) throws IOException {
        // given
        InputBuilder.start()
                .line(0)
                .line(addAction)
                .line("The Maven")
                .line("n")
                .line("n")
                .line("Graven's Secret")
                .multiLine(new String[]{
                        "Awakened Multistrike Support;gemawakened",
                        "Orb of Conflict;currency;1"
                })
                .emptyLine()
                .line(actions.get("Exit"))
                .set();

        BossDropDataSet dataSet = new BossDropDataSet(
                nullStrat,
                "The Maven",
                false,
                false,
                new Loot("Graven's Secret", BOSS_UNIQUE_ITEM),
                List.of(
                        new GemLoot("Awakened Multistrike Support", GEM_AWAKENED),
                        new StackableLoot("Orb of Conflict", CURRENCY, 1)
                ),
                null
        );

        // when
        collector.collectData(tempfile.toString());

        // then
        List<String> content = getContent();
        Assertions.assertEquals(1, content.size());
        Assertions.assertEquals(toJson(dataSet), content.get(0).trim());

        validateOutputs(new String[]{
                "Enter the name of the boss.",
                "Is the boss uber?",
                null,
                "Was the boss witnessed by the Maven?",
                null,
                "Which unique was the guaranteed drop?",
                "Input extra drops to track.",
                "Enter the area quantity.",
                "Format: ^$|^\\d+$",
                "What would you like to do?"
        });
    }

    @ParameterizedTest
    @MethodSource("provideAddActions")
    void add_multipleDataSets(String addAction) throws IOException {
        // given
        InputBuilder.start()
                .line(0)
                .line(addAction)
                .line("The Maven")
                .line("n")
                .line("n")
                .line("Graven's Secret")
                .multiLine(new String[]{
                        "Awakened Multistrike Support;gemawakened",
                        "Orb of Conflict;currency;1"
                })
                .emptyLine()
                .line(addAction)
                .line("The Infinite Hunger")
                .line("n")
                .line("n")
                .emptyLine()
                .multiLine(new String[]{})
                .line(50)
                .line(actions.get("Exit"))
                .set();

        BossDropDataSet dataSet1 = new BossDropDataSet(
                nullStrat,
                "The Maven",
                false,
                false,
                new Loot("Graven's Secret", BOSS_UNIQUE_ITEM),
                List.of(
                        new GemLoot("Awakened Multistrike Support", GEM_AWAKENED),
                        new StackableLoot("Orb of Conflict", CURRENCY, 1)
                ),
                null
        );
        BossDropDataSet dataSet2 = new BossDropDataSet(
                nullStrat,
                "The Infinite Hunger",
                false,
                false,
                null,
                List.of(),
                50
        );

        // when
        collector.collectData(tempfile.toString());

        // then
        List<String> content = getContent();
        Assertions.assertEquals(2, content.size());
        Assertions.assertEquals(toJson(dataSet1), content.get(0).trim());
        Assertions.assertEquals(toJson(dataSet2), content.get(1).trim());

        validateOutputs(new String[]{
                "Enter the name of the boss.",
                "Is the boss uber?",
                null,
                "Was the boss witnessed by the Maven?",
                null,
                "Which unique was the guaranteed drop?",
                "Input extra drops to track.",
                "Enter the area quantity.",
                "Format: ^$|^\\d+$",
                "What would you like to do?",
                null,
                "Enter the name of the boss.",
                "Is the boss uber?",
                null,
                "Was the boss witnessed by the Maven?",
                null,
                "Which unique was the guaranteed drop?",
                "Input extra drops to track.",
                "Enter the area quantity.",
                "Format: ^$|^\\d+$",
                "What would you like to do?"
        });
    }

    @ParameterizedTest
    @MethodSource("provideAddActions")
    void add_attemptInvalidInputs(String addAction) throws IOException {
        // given
        String inputs = "0" + LINEBREAK +
                addAction + LINEBREAK +
                "The Maven" + LINEBREAK +
                "x" + LINEBREAK + "y" + LINEBREAK +
                "n" + LINEBREAK +
                "Impossible Escape" + LINEBREAK +
                "invalid" + LINEBREAK + "  " + LINEBREAK + "invalid;invalid" + LINEBREAK + "Awakened Enlighten Support;gemawakened" + LINEBREAK + "Orb of Conflict;currency;zwei" + LINEBREAK + LINEBREAK +
                "NaN" + LINEBREAK + LINEBREAK +
                actions.get("Exit");
        IOUtils.setInputStream(inputs);

        BossDropDataSet dataSet = new BossDropDataSet(
                nullStrat,
                "The Maven",
                true,
                false,
                new Loot("Impossible Escape", BOSS_UNIQUE_ITEM),
                List.of(
                        new GemLoot("Awakened Enlighten Support", GEM_AWAKENED)
                ),
                null
        );

        // when
        collector.collectData(tempfile.toString());

        // then
        List<String> content = getContent();
        Assertions.assertEquals(1, content.size());
        Assertions.assertEquals(toJson(dataSet), content.get(0).trim());

        validateOutputs(new String[]{
                "Enter the name of the boss.",
                "Is the boss uber?",
                null,
                INVALID,
                "Is the boss uber?",
                null,
                "Was the boss witnessed by the Maven?",
                null,
                "Which unique was the guaranteed drop?",
                "Input extra drops to track.",
                "Couldn't parse \"invalid\" to Loot. (skipped)",
                "Couldn't parse \"  \" to Loot. (skipped)",
                "Couldn't parse \"invalid;invalid\" to Loot. (skipped)",
                "Couldn't parse \"Orb of Conflict;currency;zwei\" to Loot. (skipped)",
                "Enter the area quantity.",
                "Format: ^$|^\\d+$",
                INVALID,
                "Enter the area quantity.",
                "Format: ^$|^\\d+$",
                "What would you like to do?"
        });
    }
}
