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
import java.util.List;

public class KalandraMistDataCollectorTest extends DataCollectorTest {

    private KalandraMistDatacollectorNew collector;

    @BeforeEach
    void setup() {
        collector = new KalandraMistDatacollectorNew();
    }

    @Test
    void add_oneDataSet() throws IOException {
        // given
        String inputs = "3" + LINEBREAK +
                        actions.get("AddData") + LINEBREAK +
                        "in map" + LINEBREAK +
                        "2/3" + LINEBREAK +
                        "1.9" + LINEBREAK +
                        "r" + LINEBREAK +
                        "This is a dummy text." + LINEBREAK + "This is only for tests." + LINEBREAK + LINEBREAK +
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
        List<String> content = getContent();
        Assertions.assertEquals(Utils.toJson(dataSet), content.get(0).trim());

        // validate console outputs
        List<String> output = getOutputs();
        int pos = output.indexOf("What would you like to do?");
        Assertions.assertTrue(pos > 2);
        Assertions.assertEquals("What type of mist does this item come from?", output.get(pos + 2));
        Assertions.assertEquals("Format: ^in map$|^itemized$|^lake \\d+$", output.get(pos + 3));
        Assertions.assertEquals("Enter how many mods are positive, negative or neutral", output.get(pos + 4));
        Assertions.assertEquals("Format: ^\\d+\\/\\d+(\\/\\d+)?$", output.get(pos + 5));
        Assertions.assertEquals("Enter the multiplier. Leave Empty to skip", output.get(pos + 6));
        Assertions.assertEquals("Format: ^$|^\\d+\\.?\\d*$", output.get(pos + 7));
        Assertions.assertEquals("What type is the item?", output.get(pos + 8));
        Assertions.assertEquals("Paste the item text", output.get(pos + 10));
        Assertions.assertEquals("What would you like to do?", output.get(pos + 11));
        Assertions.assertEquals(pos + 13, output.size());
    }

    @Test
    void add_multipleDataSets() throws IOException {
        // given
        String inputs ="3" + LINEBREAK +
                actions.get("AddData") + LINEBREAK +
                "in map" + LINEBREAK +
                "2/3" + LINEBREAK +
                LINEBREAK +
                "r" + LINEBREAK +
                "This is a dummy text." + LINEBREAK + "This is only for tests." + LINEBREAK + LINEBREAK +
                actions.get("AddData") + LINEBREAK +
                "itemized" + LINEBREAK +
                "2/1/1" + LINEBREAK +
                "2.1" + LINEBREAK +
                "a" + LINEBREAK +
                LINEBREAK +
                actions.get("AddData") + LINEBREAK +
                "lake 5" + LINEBREAK +
                "3/2" + LINEBREAK +
                "2.05" + LINEBREAK +
                "r" + LINEBREAK +
                "Dummy text nr3" + LINEBREAK + LINEBREAK +
                actions.get("Exit");
        IOUtils.setInputStream(inputs);

        KalandraMistDataSet dataSet1 = new KalandraMistDataSet(
                new Strategy(3, "3.25", null, null, null, null, null),
                KalandraMistDataSet.MistType.IN_MAP, null,
                2, 3, 0,
                "This is a dummy text.\nThis is only for tests.",
                LootType.RARE_JEWELLRY_RING,
                null
        );
        KalandraMistDataSet dataSet2 = new KalandraMistDataSet(
                new Strategy(3, "3.25", null, null, null, null, null),
                KalandraMistDataSet.MistType.ITEMIZED, null,
                2, 1, 1,
                "",
                LootType.RARE_JEWELLRY_AMULET,
                "2.1"
        );
        KalandraMistDataSet dataSet3 = new KalandraMistDataSet(
                new Strategy(3, "3.25", null, null, null, null, null),
                KalandraMistDataSet.MistType.LAKE, 5,
                3, 2, 0,
                "Dummy text nr3",
                LootType.RARE_JEWELLRY_RING,
                "2.05"
        );

        // when
        collector.collectData(tempfile.toString());

        // then
        List<String> content = getContent();
        Assertions.assertEquals(3, content.size());
        Assertions.assertEquals(Utils.toJson(dataSet1), content.get(0).trim());
        Assertions.assertEquals(Utils.toJson(dataSet2), content.get(1).trim());
        Assertions.assertEquals(Utils.toJson(dataSet3), content.get(2).trim());

        // validate console outputs
        List<String> output = getOutputs();
        int pos = output.indexOf("What would you like to do?");
        Assertions.assertTrue(pos > 2);
        Assertions.assertEquals("What type of mist does this item come from?", output.get(pos + 2));
        Assertions.assertEquals("Format: ^in map$|^itemized$|^lake \\d+$", output.get(pos + 3));
        Assertions.assertEquals("Enter how many mods are positive, negative or neutral", output.get(pos + 4));
        Assertions.assertEquals("Format: ^\\d+\\/\\d+(\\/\\d+)?$", output.get(pos + 5));
        Assertions.assertEquals("Enter the multiplier. Leave Empty to skip", output.get(pos + 6));
        Assertions.assertEquals("Format: ^$|^\\d+\\.?\\d*$", output.get(pos + 7));
        Assertions.assertEquals("What type is the item?", output.get(pos + 8));
        Assertions.assertEquals("Paste the item text", output.get(pos + 10));
        Assertions.assertEquals("What would you like to do?", output.get(pos + 11));
        Assertions.assertEquals("What type of mist does this item come from?", output.get(pos + 13));
        Assertions.assertEquals("Format: ^in map$|^itemized$|^lake \\d+$", output.get(pos + 14));
        Assertions.assertEquals("Enter how many mods are positive, negative or neutral", output.get(pos + 15));
        Assertions.assertEquals("Format: ^\\d+\\/\\d+(\\/\\d+)?$", output.get(pos + 16));
        Assertions.assertEquals("Enter the multiplier. Leave Empty to skip", output.get(pos + 17));
        Assertions.assertEquals("Format: ^$|^\\d+\\.?\\d*$", output.get(pos + 18));
        Assertions.assertEquals("What type is the item?", output.get(pos + 19));
        Assertions.assertEquals("Paste the item text", output.get(pos + 21));
        Assertions.assertEquals("What would you like to do?", output.get(pos + 22));
        Assertions.assertEquals("What type of mist does this item come from?", output.get(pos + 24));
        Assertions.assertEquals("Format: ^in map$|^itemized$|^lake \\d+$", output.get(pos + 25));
        Assertions.assertEquals("Enter how many mods are positive, negative or neutral", output.get(pos + 26));
        Assertions.assertEquals("Format: ^\\d+\\/\\d+(\\/\\d+)?$", output.get(pos + 27));
        Assertions.assertEquals("Enter the multiplier. Leave Empty to skip", output.get(pos + 28));
        Assertions.assertEquals("Format: ^$|^\\d+\\.?\\d*$", output.get(pos + 29));
        Assertions.assertEquals("What type is the item?", output.get(pos + 30));
        Assertions.assertEquals("Paste the item text", output.get(pos + 32));
        Assertions.assertEquals("What would you like to do?", output.get(pos + 33));
        Assertions.assertEquals(pos + 35, output.size());
    }

    @Test
    void add_attemptInvalidInputs() throws IOException {
        // given
        String inputs = "3" + LINEBREAK +
                actions.get("AddData") + LINEBREAK +
                "bla" + LINEBREAK + "in map" + LINEBREAK +
                "ble" + LINEBREAK + "15" + LINEBREAK + "-1/3" + LINEBREAK + "4/0" + LINEBREAK +
                "bli" + LINEBREAK + "2" + LINEBREAK +
                "blo" + LINEBREAK +
                "blu" + LINEBREAK + LINEBREAK +
                actions.get("Exit");
        IOUtils.setInputStream(inputs);

        KalandraMistDataSet dataSet = new KalandraMistDataSet(
                new Strategy(3, "3.25", null, null, null, null, null),
                KalandraMistDataSet.MistType.IN_MAP, null,
                4, 0, 0,
                "blu",
                null,
                "2"
        );

        // when
        collector.collectData(tempfile.toString());

        // then
        List<String> content = getContent();
        Assertions.assertEquals(Utils.toJson(dataSet), content.get(0).trim());

        // validate console outputs
        List<String> output = getOutputs();
        int pos = output.indexOf("What would you like to do?");
        Assertions.assertTrue(pos > 2);
        String invalid = "Invalid input, try again";
        Assertions.assertEquals("What type of mist does this item come from?", output.get(pos + 2));
        Assertions.assertEquals("Format: ^in map$|^itemized$|^lake \\d+$", output.get(pos + 3));
        Assertions.assertEquals(invalid, output.get(pos + 4));
        Assertions.assertEquals("What type of mist does this item come from?", output.get(pos + 5));
        Assertions.assertEquals("Format: ^in map$|^itemized$|^lake \\d+$", output.get(pos + 6));
        Assertions.assertEquals("Enter how many mods are positive, negative or neutral", output.get(pos + 7));
        Assertions.assertEquals("Format: ^\\d+\\/\\d+(\\/\\d+)?$", output.get(pos + 8));
        Assertions.assertEquals(invalid, output.get(pos + 9));
        Assertions.assertEquals("Enter how many mods are positive, negative or neutral", output.get(pos + 10));
        Assertions.assertEquals("Format: ^\\d+\\/\\d+(\\/\\d+)?$", output.get(pos + 11));
        Assertions.assertEquals(invalid, output.get(pos + 12));
        Assertions.assertEquals("Enter how many mods are positive, negative or neutral", output.get(pos + 13));
        Assertions.assertEquals("Format: ^\\d+\\/\\d+(\\/\\d+)?$", output.get(pos + 14));
        Assertions.assertEquals(invalid, output.get(pos + 15));
        Assertions.assertEquals("Enter how many mods are positive, negative or neutral", output.get(pos + 16));
        Assertions.assertEquals("Format: ^\\d+\\/\\d+(\\/\\d+)?$", output.get(pos + 17));
        Assertions.assertEquals("Enter the multiplier. Leave Empty to skip", output.get(pos + 18));
        Assertions.assertEquals("Format: ^$|^\\d+\\.?\\d*$", output.get(pos + 19));
        Assertions.assertEquals(invalid, output.get(pos + 20));
        Assertions.assertEquals("Enter the multiplier. Leave Empty to skip", output.get(pos + 21));
        Assertions.assertEquals("Format: ^$|^\\d+\\.?\\d*$", output.get(pos + 22));
        Assertions.assertEquals("What type is the item?", output.get(pos + 23));
        Assertions.assertEquals("Paste the item text", output.get(pos + 25));
        Assertions.assertEquals("What would you like to do?", output.get(pos + 26));
        Assertions.assertEquals(pos + 28, output.size());
    }
}
