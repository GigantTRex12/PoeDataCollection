package com.company.datacollector;

import com.company.datasets.datasets.UltimatumDataSet;
import com.company.datasets.other.loot.GemLoot;
import com.company.datasets.other.loot.ImplicitCorruptedItem;
import com.company.datasets.other.loot.Loot;
import com.company.datasets.other.loot.StackableLoot;
import com.company.utils.IOUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.company.datasets.other.loot.LootType.*;
import static com.company.utils.Utils.toJson;

public class UltimatumDataCollectorTest extends DataCollectorTest{

    private UltimatumDataCollector collector;

    @BeforeEach
    void setup() {
        collector = new UltimatumDataCollector();
    }

    @Test
    void add_oneDataSet() throws IOException {
        // given
        String inputs = "0" + LINEBREAK +
                actions.get("AddData") + LINEBREAK +
                "Honour Wrap Necromancer Silks;rarearmorcorrupted;1" +
                LINEBREAK + "Whispering Bite Murderous Eye Jewel;rare" +
                LINEBREAK + "Instrinsic Catalyst;catalyst;3" +
                LINEBREAK + "Bramble Locket Rusted Sash;rarejewellrycorrupted;2" +
                LINEBREAK + "Splinter of Xoph;breachsplinter;15" +
                LINEBREAK + "Accelerating Catalyst;catalyst;3" +
                LINEBREAK + "Abrasive Catalyst;catalyst;5" +
                LINEBREAK + "Arctic Armour;gem;20" +
                LINEBREAK + "Orb of Regret;currency;8" +
                LINEBREAK + "The Brass Dome Gladiator Plate;corruptedunique;1" +
                LINEBREAK + LINEBREAK +
                "y" + LINEBREAK +
                "Mahuxotl's Machination;bossunique" +
                LINEBREAK + "Intrinsic Catalyst;catalyst;10" +
                LINEBREAK + "Tainted Catalyst;catalyst;9" +
                LINEBREAK + LINEBREAK +
                actions.get("Exit");
        IOUtils.setInputStream(inputs);

        UltimatumDataSet dataSet = new UltimatumDataSet(
                nullStrat,
                List.of(
                        new ImplicitCorruptedItem("Honour Wrap Necromancer Silks", RARE_ARMOUR_IMPLICIT_CORRUPTED, 1),
                        new Loot("Whispering Bite Murderous Eye Jewel", RARE_ITEM),
                        new StackableLoot("Instrinsic Catalyst", CATALYSTS, 3),
                        new ImplicitCorruptedItem("Bramble Locket Rusted Sash", RARE_JEWELLRY_IMPLICIT_CORRUPTED, 2),
                        new StackableLoot("Splinter of Xoph", SPLINTERS_BREACH, 15),
                        new StackableLoot("Accelerating Catalyst", CATALYSTS, 3),
                        new StackableLoot("Abrasive Catalyst", CATALYSTS, 5),
                        new GemLoot("Arctic Armour", GEM, 20, 0),
                        new StackableLoot("Orb of Regret", CURRENCY, 8),
                        new ImplicitCorruptedItem("The Brass Dome Gladiator Plate", UNIQUE_ITEM_IMPLICIT_CORRUPTED, 1)
                ),
                true,
                List.of(
                        new Loot("Mahuxotl's Machination", BOSS_UNIQUE_ITEM),
                        new StackableLoot("Intrinsic Catalyst", CATALYSTS, 10),
                        new StackableLoot("Tainted Catalyst", CATALYSTS, 9)
                )
        );

        // when
        collector.collectData(tempfile.toString());

        // then
        List<String> content = getContent();
        Assertions.assertEquals(1, content.size());
        Assertions.assertEquals(toJson(dataSet), content.get(0).trim());

        // validate console outputs
        List<String> output = getOutputs();
        int pos = output.indexOf("What would you like to do?");
        Assertions.assertTrue(pos > 2);
        Assertions.assertEquals("Enter rewards from Ultimatum in order. (one line per reward)", output.get(pos + 2));
        Assertions.assertEquals("Enter \"-\" to skip reward.", output.get(pos + 3));
        Assertions.assertEquals("Was a boss encountered?", output.get(pos + 4));
        Assertions.assertEquals("Enter drops from boss.", output.get(pos + 6));
        Assertions.assertEquals("What would you like to do?", output.get(pos + 7));
        Assertions.assertEquals(pos + 9, output.size());
    }

    @Test
    void add_multipleDataSets() throws IOException {
        // given
        String inputs = "0" + LINEBREAK +
                actions.get("AddData") + LINEBREAK +
                "Honour Wrap Necromancer Silks;rarearmorcorrupted;1" +
                LINEBREAK + "Whispering Bite Murderous Eye Jewel;rare" +
                LINEBREAK + "Instrinsic Catalyst;catalyst;3" +
                LINEBREAK + "Bramble Locket Rusted Sash;rarejewellrycorrupted;2" +
                LINEBREAK + "Splinter of Xoph;breachsplinter;15" +
                LINEBREAK + "Accelerating Catalyst;catalyst;3" +
                LINEBREAK + "Abrasive Catalyst;catalyst;5" +
                LINEBREAK + "Arctic Armour;gem;20" +
                LINEBREAK + "Orb of Regret;currency;8" +
                LINEBREAK + "The Brass Dome Gladiator Plate;corruptedunique;1" +
                LINEBREAK + LINEBREAK +
                "y" + LINEBREAK +
                "Mahuxotl's Machination;bossunique" +
                LINEBREAK + "Intrinsic Catalyst;catalyst;10" +
                LINEBREAK + "Tainted Catalyst;catalyst;9" +
                LINEBREAK + LINEBREAK +
                actions.get("AddData") + LINEBREAK +
                "-" +
                LINEBREAK + "-" +
                LINEBREAK + "Instrinsic Catalyst;catalyst;3" +
                LINEBREAK + "Kalandras Touch;corruptedunique;1" +
                LINEBREAK + "-" +
                LINEBREAK + "-" +
                LINEBREAK + "Abrasive Catalyst;catalyst;5" +
                LINEBREAK + "-" +
                LINEBREAK + "-" +
                LINEBREAK + "-" +
                LINEBREAK + LINEBREAK +
                "n" + LINEBREAK +
                actions.get("AddData") + LINEBREAK +
                "Honour Wrap Necromancer Silks;rarearmorcorrupted;1" +
                LINEBREAK + "Whispering Bite Murderous Eye Jewel;rare" +
                LINEBREAK + "Instrinsic Catalyst;catalyst;3" +
                LINEBREAK + LINEBREAK +
                actions.get("AddData") + LINEBREAK +
                "Honour Wrap Necromancer Silks;rarearmorcorrupted;1" +
                LINEBREAK + "Whispering Bite Murderous Eye Jewel;rare" +
                LINEBREAK + "Instrinsic Catalyst;catalyst;3" +
                LINEBREAK + "Bramble Locket Rusted Sash;rarejewellrycorrupted;2" +
                LINEBREAK + "Splinter of Xoph;breachsplinter;15" +
                LINEBREAK + "Accelerating Catalyst;catalyst;3" +
                LINEBREAK + "Abrasive Catalyst;catalyst;5" +
                LINEBREAK + "Arctic Armour;gem;20" +
                LINEBREAK + "Orb of Regret;currency;8" +
                LINEBREAK + "The Brass Dome Gladiator Plate;corruptedunique;1" +
                LINEBREAK + "Fertile Catalyst;catalyst;2" +
                LINEBREAK + "Prismatic Catalyst;catalyst;3" +
                LINEBREAK + "Mageblood Heavy Belt;unique" +
                LINEBREAK + LINEBREAK +
                actions.get("Exit");
        IOUtils.setInputStream(inputs);

        UltimatumDataSet dataSet1 = new UltimatumDataSet(
                nullStrat,
                List.of(
                        new ImplicitCorruptedItem("Honour Wrap Necromancer Silks", RARE_ARMOUR_IMPLICIT_CORRUPTED, 1),
                        new Loot("Whispering Bite Murderous Eye Jewel", RARE_ITEM),
                        new StackableLoot("Instrinsic Catalyst", CATALYSTS, 3),
                        new ImplicitCorruptedItem("Bramble Locket Rusted Sash", RARE_JEWELLRY_IMPLICIT_CORRUPTED, 2),
                        new StackableLoot("Splinter of Xoph", SPLINTERS_BREACH, 15),
                        new StackableLoot("Accelerating Catalyst", CATALYSTS, 3),
                        new StackableLoot("Abrasive Catalyst", CATALYSTS, 5),
                        new GemLoot("Arctic Armour", GEM, 20, 0),
                        new StackableLoot("Orb of Regret", CURRENCY, 8),
                        new ImplicitCorruptedItem("The Brass Dome Gladiator Plate", UNIQUE_ITEM_IMPLICIT_CORRUPTED, 1)
                ),
                true,
                List.of(
                        new Loot("Mahuxotl's Machination", BOSS_UNIQUE_ITEM),
                        new StackableLoot("Intrinsic Catalyst", CATALYSTS, 10),
                        new StackableLoot("Tainted Catalyst", CATALYSTS, 9)
                )
        );

        UltimatumDataSet dataSet2 = new UltimatumDataSet(
                nullStrat,
                Stream.of(
                        null, null,
                        new StackableLoot("Instrinsic Catalyst", CATALYSTS, 3),
                        new ImplicitCorruptedItem("Kalandras Touch", UNIQUE_ITEM_IMPLICIT_CORRUPTED, 1),
                        null, null,
                        new StackableLoot("Abrasive Catalyst", CATALYSTS, 5),
                        null, null, null
                ).collect(Collectors.toList()),
                false,
                null
        );

        UltimatumDataSet dataSet3 = new UltimatumDataSet(
                nullStrat,
                List.of(
                        new ImplicitCorruptedItem("Honour Wrap Necromancer Silks", RARE_ARMOUR_IMPLICIT_CORRUPTED, 1),
                        new Loot("Whispering Bite Murderous Eye Jewel", RARE_ITEM),
                        new StackableLoot("Instrinsic Catalyst", CATALYSTS, 3)
                ),
                false,
                null
        );

        UltimatumDataSet dataSet4 = new UltimatumDataSet(
                nullStrat,
                List.of(
                        new ImplicitCorruptedItem("Honour Wrap Necromancer Silks", RARE_ARMOUR_IMPLICIT_CORRUPTED, 1),
                        new Loot("Whispering Bite Murderous Eye Jewel", RARE_ITEM),
                        new StackableLoot("Instrinsic Catalyst", CATALYSTS, 3),
                        new ImplicitCorruptedItem("Bramble Locket Rusted Sash", RARE_JEWELLRY_IMPLICIT_CORRUPTED, 2),
                        new StackableLoot("Splinter of Xoph", SPLINTERS_BREACH, 15),
                        new StackableLoot("Accelerating Catalyst", CATALYSTS, 3),
                        new StackableLoot("Abrasive Catalyst", CATALYSTS, 5),
                        new GemLoot("Arctic Armour", GEM, 20, 0),
                        new StackableLoot("Orb of Regret", CURRENCY, 8),
                        new ImplicitCorruptedItem("The Brass Dome Gladiator Plate", UNIQUE_ITEM_IMPLICIT_CORRUPTED, 1),
                        new StackableLoot("Fertile Catalyst", CATALYSTS, 2),
                        new StackableLoot("Prismatic Catalyst", CATALYSTS, 3),
                        new Loot("Mageblood Heavy Belt", UNIQUE_ITEM)
                ),
                false,
                null
        );

        // when
        collector.collectData(tempfile.toString());

        // then
        List<String> content = getContent();
        Assertions.assertEquals(4, content.size());
        Assertions.assertEquals(toJson(dataSet1), content.get(0).trim());
        Assertions.assertEquals(toJson(dataSet2), content.get(1).trim());
        Assertions.assertEquals(toJson(dataSet3), content.get(2).trim());
        Assertions.assertEquals(toJson(dataSet4), content.get(3).trim());

        // validate console outputs
        List<String> output = getOutputs();
        int pos = output.indexOf("What would you like to do?");
        Assertions.assertTrue(pos > 2);
        Assertions.assertEquals("Enter rewards from Ultimatum in order. (one line per reward)", output.get(pos + 2));
        Assertions.assertEquals("Enter \"-\" to skip reward.", output.get(pos + 3));
        Assertions.assertEquals("Was a boss encountered?", output.get(pos + 4));
        Assertions.assertEquals("Enter drops from boss.", output.get(pos + 6));
        Assertions.assertEquals("What would you like to do?", output.get(pos + 7));
        Assertions.assertEquals("Enter rewards from Ultimatum in order. (one line per reward)", output.get(pos + 9));
        Assertions.assertEquals("Enter \"-\" to skip reward.", output.get(pos + 10));
        Assertions.assertEquals("Was a boss encountered?", output.get(pos + 11));
        Assertions.assertEquals("What would you like to do?", output.get(pos + 13));
        Assertions.assertEquals("Enter rewards from Ultimatum in order. (one line per reward)", output.get(pos + 15));
        Assertions.assertEquals("Enter \"-\" to skip reward.", output.get(pos + 16));
        Assertions.assertEquals("What would you like to do?", output.get(pos + 17));
        Assertions.assertEquals("Enter rewards from Ultimatum in order. (one line per reward)", output.get(pos + 19));
        Assertions.assertEquals("Enter \"-\" to skip reward.", output.get(pos + 20));
        Assertions.assertEquals("What would you like to do?", output.get(pos + 21));
        Assertions.assertEquals(pos + 23, output.size());
    }

    @Test
    void add_attemptInvalidInputs() throws IOException {
        // given
        String inputs = "0" + LINEBREAK +
                actions.get("AddData") + LINEBREAK +
                LINEBREAK +
                actions.get("AddData") + LINEBREAK +
                "invalid" +
                LINEBREAK + "Honour Wrap Necromancer Silks;rarearmorcorrupted;1" +
                LINEBREAK + "also invalid" +
                LINEBREAK + "Whispering Bite Murderous Eye Jewel;rare" +
                LINEBREAK + "Instrinsic Catalyst;catalyst;3" +
                LINEBREAK + "Bramble Locket Rusted Sash;rarejewellrycorrupted;2" +
                LINEBREAK + "not;even;close" +
                LINEBREAK + "Splinter of Xoph;breachsplinter;15" +
                LINEBREAK + "Accelerating Catalyst;catalyst;3" +
                LINEBREAK + "Abrasive Catalyst;catalyst;5" +
                LINEBREAK + "Arctic Armour;gem;20" +
                LINEBREAK + "Orb of Regret;currency;8" +
                LINEBREAK + "The Brass Dome Gladiator Plate;corruptedunique;1" +
                LINEBREAK + "eh" +
                LINEBREAK + LINEBREAK +
                "not a boolean" + LINEBREAK +
                "n" + LINEBREAK +
                actions.get("Exit");
        IOUtils.setInputStream(inputs);

        UltimatumDataSet dataSet = new UltimatumDataSet(
                nullStrat,
                List.of(
                        new ImplicitCorruptedItem("Honour Wrap Necromancer Silks", RARE_ARMOUR_IMPLICIT_CORRUPTED, 1),
                        new Loot("Whispering Bite Murderous Eye Jewel", RARE_ITEM),
                        new StackableLoot("Instrinsic Catalyst", CATALYSTS, 3),
                        new ImplicitCorruptedItem("Bramble Locket Rusted Sash", RARE_JEWELLRY_IMPLICIT_CORRUPTED, 2),
                        new StackableLoot("Splinter of Xoph", SPLINTERS_BREACH, 15),
                        new StackableLoot("Accelerating Catalyst", CATALYSTS, 3),
                        new StackableLoot("Abrasive Catalyst", CATALYSTS, 5),
                        new GemLoot("Arctic Armour", GEM, 20, 0),
                        new StackableLoot("Orb of Regret", CURRENCY, 8),
                        new ImplicitCorruptedItem("The Brass Dome Gladiator Plate", UNIQUE_ITEM_IMPLICIT_CORRUPTED, 1)
                ),
                false,
                null
        );

        // when
        collector.collectData(tempfile.toString());

        // then
        List<String> content = getContent();
        Assertions.assertEquals(1, content.size());
        Assertions.assertEquals(toJson(dataSet), content.get(0).trim());

        // validate console outputs
        List<String> output = getOutputs();
        int pos = output.indexOf("What would you like to do?");
        Assertions.assertTrue(pos > 2);
        String invalid = "Invalid input, try again";
        Assertions.assertEquals("Enter rewards from Ultimatum in order. (one line per reward)", output.get(pos + 2));
        Assertions.assertEquals("Enter \"-\" to skip reward.", output.get(pos + 3));
        Assertions.assertEquals("Reward list should not be empty", output.get(pos + 4));
        Assertions.assertEquals("What would you like to do?", output.get(pos + 5));
        Assertions.assertEquals("Enter rewards from Ultimatum in order. (one line per reward)", output.get(pos + 7));
        Assertions.assertEquals("Enter \"-\" to skip reward.", output.get(pos + 8));
        Assertions.assertEquals("Couldn't parse \"invalid\" to Loot. (skipped)", output.get(pos + 9));
        Assertions.assertEquals("Couldn't parse \"also invalid\" to Loot. (skipped)", output.get(pos + 10));
        Assertions.assertEquals("Couldn't parse \"not;even;close\" to Loot. (skipped)", output.get(pos + 11));
        Assertions.assertEquals("Couldn't parse \"eh\" to Loot. (skipped)", output.get(pos + 12));
        Assertions.assertEquals("Was a boss encountered?", output.get(pos + 13));
        Assertions.assertEquals(invalid, output.get(pos + 15));
        Assertions.assertEquals("Was a boss encountered?", output.get(pos + 16));
        Assertions.assertEquals("What would you like to do?", output.get(pos + 18));
        Assertions.assertEquals(pos + 20, output.size());
    }

}
