package com.company.datacollector;

import com.company.datasets.datasets.MapDropDataSet;
import com.company.utils.IOUtils;
import com.company.utils.Utils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static com.company.datasets.other.loot.LootType.*;

public class MapDropDataCollectorTest extends DataCollectorTest {

    private MapDropDataCollectorNew collector;

    @BeforeEach
    void setup() {
        collector = new MapDropDataCollectorNew();
    }

    @Test
    void add_oneDataSet() throws IOException {
        // given
        String inputs = "0" + LINEBREAK +
                actions.get("AddData") + LINEBREAK +
                "20e" + LINEBREAK +
                "r-r-e-r-t-r" + LINEBREAK +
                "r,r" + LINEBREAK +
                actions.get("Exit");
        IOUtils.setInputStream(inputs);

        MapDropDataSet dataSet = new MapDropDataSet(
                nullStrat,
                20,
                ELDER_MAP,
                List.of(MAP, MAP, ELDER_MAP, MAP, T17_MAP, MAP),
                List.of(MAP, MAP)
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
        Assertions.assertEquals("Enter the chance for converting maps from Influencing Scarab of conversion and conversion type.", output.get(pos + 2));
        Assertions.assertEquals("s:shaper, e:elder, c:conqueror, y:synthesis", output.get(pos + 3));
        Assertions.assertEquals("Format: ^\\s*$|^[1-9]\\d*[secy]$", output.get(pos + 4));
        Assertions.assertEquals("Enter maps dropped.", output.get(pos + 5));
        Assertions.assertEquals("r:regular, s:shaper, e:elder, c:conqueror, y:synthesis, t:t17, u:unique", output.get(pos + 6));
        Assertions.assertEquals("Format: ^$|^[rsecytu](-[rsecytu])*$", output.get(pos + 7));
        Assertions.assertEquals("Enter maps dropped by boss.", output.get(pos + 8));
        Assertions.assertEquals("Empty for not killing boss, - for no drops", output.get(pos + 9));
        Assertions.assertEquals("Format: ^-?$|^[rsecytu](,[rsecytu])*$", output.get(pos + 10));
        Assertions.assertEquals("What would you like to do?", output.get(pos + 11));
        Assertions.assertEquals(pos + 13, output.size());
    }

    @Test
    void add_multipleDataSets() throws IOException {
        // given
        String inputs = "0" + LINEBREAK +
                actions.get("AddData") + LINEBREAK +
                "20e" + LINEBREAK +
                "r-r-e-r-t-r" + LINEBREAK +
                "r,r" + LINEBREAK +
                actions.get("AddData") + LINEBREAK +
                "15y" + LINEBREAK +
                LINEBREAK +
                "-" + LINEBREAK +
                actions.get("AddData") + LINEBREAK +
                LINEBREAK +
                "r" + LINEBREAK +
                LINEBREAK +
                actions.get("Exit");
        IOUtils.setInputStream(inputs);

        MapDropDataSet dataSet1 = new MapDropDataSet(
                nullStrat,
                20,
                ELDER_MAP,
                List.of(MAP, MAP, ELDER_MAP, MAP, T17_MAP, MAP),
                List.of(MAP, MAP)
        );
        MapDropDataSet dataSet2 = new MapDropDataSet(
                nullStrat,
                15,
                SYNTH_MAP,
                List.of(),
                List.of()
        );
        MapDropDataSet dataSet3 = new MapDropDataSet(
                nullStrat,
                0,
                null,
                List.of(MAP),
                null
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
        Assertions.assertEquals("Enter the chance for converting maps from Influencing Scarab of conversion and conversion type.", output.get(pos + 2));
        Assertions.assertEquals("s:shaper, e:elder, c:conqueror, y:synthesis", output.get(pos + 3));
        Assertions.assertEquals("Format: ^\\s*$|^[1-9]\\d*[secy]$", output.get(pos + 4));
        Assertions.assertEquals("Enter maps dropped.", output.get(pos + 5));
        Assertions.assertEquals("r:regular, s:shaper, e:elder, c:conqueror, y:synthesis, t:t17, u:unique", output.get(pos + 6));
        Assertions.assertEquals("Format: ^$|^[rsecytu](-[rsecytu])*$", output.get(pos + 7));
        Assertions.assertEquals("Enter maps dropped by boss.", output.get(pos + 8));
        Assertions.assertEquals("Empty for not killing boss, - for no drops", output.get(pos + 9));
        Assertions.assertEquals("Format: ^-?$|^[rsecytu](,[rsecytu])*$", output.get(pos + 10));
        Assertions.assertEquals("What would you like to do?", output.get(pos + 11));
        Assertions.assertEquals("Enter the chance for converting maps from Influencing Scarab of conversion and conversion type.", output.get(pos + 13));
        Assertions.assertEquals("s:shaper, e:elder, c:conqueror, y:synthesis", output.get(pos + 14));
        Assertions.assertEquals("Format: ^\\s*$|^[1-9]\\d*[secy]$", output.get(pos + 15));
        Assertions.assertEquals("Enter maps dropped.", output.get(pos + 16));
        Assertions.assertEquals("r:regular, s:shaper, e:elder, c:conqueror, y:synthesis, t:t17, u:unique", output.get(pos + 17));
        Assertions.assertEquals("Format: ^$|^[rsecytu](-[rsecytu])*$", output.get(pos + 18));
        Assertions.assertEquals("Enter maps dropped by boss.", output.get(pos + 19));
        Assertions.assertEquals("Empty for not killing boss, - for no drops", output.get(pos + 20));
        Assertions.assertEquals("Format: ^-?$|^[rsecytu](,[rsecytu])*$", output.get(pos + 21));
        Assertions.assertEquals("What would you like to do?", output.get(pos + 22));
        Assertions.assertEquals("Enter the chance for converting maps from Influencing Scarab of conversion and conversion type.", output.get(pos + 24));
        Assertions.assertEquals("s:shaper, e:elder, c:conqueror, y:synthesis", output.get(pos + 25));
        Assertions.assertEquals("Format: ^\\s*$|^[1-9]\\d*[secy]$", output.get(pos + 26));
        Assertions.assertEquals("Enter maps dropped.", output.get(pos + 27));
        Assertions.assertEquals("r:regular, s:shaper, e:elder, c:conqueror, y:synthesis, t:t17, u:unique", output.get(pos + 28));
        Assertions.assertEquals("Format: ^$|^[rsecytu](-[rsecytu])*$", output.get(pos + 29));
        Assertions.assertEquals("Enter maps dropped by boss.", output.get(pos + 30));
        Assertions.assertEquals("Empty for not killing boss, - for no drops", output.get(pos + 31));
        Assertions.assertEquals("Format: ^-?$|^[rsecytu](,[rsecytu])*$", output.get(pos + 32));
        Assertions.assertEquals("What would you like to do?", output.get(pos + 33));
        Assertions.assertEquals(pos + 35, output.size());
    }

    @Test
    void add_attemptInvalidInputs() throws IOException {
        // given
        String inputs = "0" + LINEBREAK +
                actions.get("AddData") + LINEBREAK +
                "-20e" + LINEBREAK + "20" + LINEBREAK + "e" + LINEBREAK + "20e" + LINEBREAK +
                "r-b" + LINEBREAK + "rr" + LINEBREAK + "r--r" + LINEBREAK + "r-r" + LINEBREAK +
                "rf" + LINEBREAK + "rr" + LINEBREAK + "r" + LINEBREAK +
                actions.get("Exit");
        IOUtils.setInputStream(inputs);

        MapDropDataSet dataSet = new MapDropDataSet(
                nullStrat,
                20,
                ELDER_MAP,
                List.of(MAP, MAP),
                List.of(MAP)
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
        Assertions.assertEquals("Enter the chance for converting maps from Influencing Scarab of conversion and conversion type.", output.get(pos + 2));
        Assertions.assertEquals("s:shaper, e:elder, c:conqueror, y:synthesis", output.get(pos + 3));
        Assertions.assertEquals("Format: ^\\s*$|^[1-9]\\d*[secy]$", output.get(pos + 4));
        Assertions.assertEquals(invalid, output.get(pos + 5));
        Assertions.assertEquals("Enter the chance for converting maps from Influencing Scarab of conversion and conversion type.", output.get(pos + 6));
        Assertions.assertEquals("s:shaper, e:elder, c:conqueror, y:synthesis", output.get(pos + 7));
        Assertions.assertEquals("Format: ^\\s*$|^[1-9]\\d*[secy]$", output.get(pos + 8));
        Assertions.assertEquals(invalid, output.get(pos + 9));
        Assertions.assertEquals("Enter the chance for converting maps from Influencing Scarab of conversion and conversion type.", output.get(pos + 10));
        Assertions.assertEquals("s:shaper, e:elder, c:conqueror, y:synthesis", output.get(pos + 11));
        Assertions.assertEquals("Format: ^\\s*$|^[1-9]\\d*[secy]$", output.get(pos + 12));
        Assertions.assertEquals(invalid, output.get(pos + 13));
        Assertions.assertEquals("Enter the chance for converting maps from Influencing Scarab of conversion and conversion type.", output.get(pos + 14));
        Assertions.assertEquals("s:shaper, e:elder, c:conqueror, y:synthesis", output.get(pos + 15));
        Assertions.assertEquals("Format: ^\\s*$|^[1-9]\\d*[secy]$", output.get(pos + 16));
        Assertions.assertEquals("Enter maps dropped.", output.get(pos + 17));
        Assertions.assertEquals("r:regular, s:shaper, e:elder, c:conqueror, y:synthesis, t:t17, u:unique", output.get(pos + 18));
        Assertions.assertEquals("Format: ^$|^[rsecytu](-[rsecytu])*$", output.get(pos + 19));
        Assertions.assertEquals(invalid, output.get(pos + 20));
        Assertions.assertEquals("Enter maps dropped.", output.get(pos + 21));
        Assertions.assertEquals("r:regular, s:shaper, e:elder, c:conqueror, y:synthesis, t:t17, u:unique", output.get(pos + 22));
        Assertions.assertEquals("Format: ^$|^[rsecytu](-[rsecytu])*$", output.get(pos + 23));
        Assertions.assertEquals(invalid, output.get(pos + 24));
        Assertions.assertEquals("Enter maps dropped.", output.get(pos + 25));
        Assertions.assertEquals("r:regular, s:shaper, e:elder, c:conqueror, y:synthesis, t:t17, u:unique", output.get(pos + 26));
        Assertions.assertEquals("Format: ^$|^[rsecytu](-[rsecytu])*$", output.get(pos + 27));
        Assertions.assertEquals(invalid, output.get(pos + 28));
        Assertions.assertEquals("Enter maps dropped.", output.get(pos + 29));
        Assertions.assertEquals("r:regular, s:shaper, e:elder, c:conqueror, y:synthesis, t:t17, u:unique", output.get(pos + 30));
        Assertions.assertEquals("Format: ^$|^[rsecytu](-[rsecytu])*$", output.get(pos + 31));
        Assertions.assertEquals("Enter maps dropped by boss.", output.get(pos + 32));
        Assertions.assertEquals("Empty for not killing boss, - for no drops", output.get(pos + 33));
        Assertions.assertEquals("Format: ^-?$|^[rsecytu](,[rsecytu])*$", output.get(pos + 34));
        Assertions.assertEquals(invalid, output.get(pos + 35));
        Assertions.assertEquals("Enter maps dropped by boss.", output.get(pos + 36));
        Assertions.assertEquals("Empty for not killing boss, - for no drops", output.get(pos + 37));
        Assertions.assertEquals("Format: ^-?$|^[rsecytu](,[rsecytu])*$", output.get(pos + 38));
        Assertions.assertEquals(invalid, output.get(pos + 39));
        Assertions.assertEquals("Enter maps dropped by boss.", output.get(pos + 40));
        Assertions.assertEquals("Empty for not killing boss, - for no drops", output.get(pos + 41));
        Assertions.assertEquals("Format: ^-?$|^[rsecytu](,[rsecytu])*$", output.get(pos + 42));
        Assertions.assertEquals("What would you like to do?", output.get(pos + 43));
        Assertions.assertEquals(pos + 45, output.size());
    }
}
