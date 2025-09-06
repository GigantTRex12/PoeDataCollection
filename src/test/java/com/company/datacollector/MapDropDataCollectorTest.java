package com.company.datacollector;

import com.company.datasets.datasets.MapDropDataSet;
import com.company.testutils.InputBuilder;
import com.company.utils.IOUtils;
import com.company.utils.Utils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.util.List;

import static com.company.datasets.other.loot.LootType.*;

public class MapDropDataCollectorTest extends DataCollectorTest {

    private MapDropDataCollector collector;

    @BeforeEach
    void setup() {
        collector = new MapDropDataCollector();
    }

    @ParameterizedTest
    @MethodSource("provideAddActions")
    void add_oneDataSet(String addAction) throws IOException {
        // given
        InputBuilder.start()
                .line(0)
                .line(addAction)
                .line("20e")
                .line("r-r-e-o-t-r")
                .line("r,r")
                .line(actions.get("Exit"))
                .set();

        MapDropDataSet dataSet = new MapDropDataSet(
                nullStrat,
                20,
                ELDER_MAP,
                List.of(MAP, MAP, ELDER_MAP, ORIGINATOR_MAP, T17_MAP, MAP),
                List.of(MAP, MAP)
        );

        // when
        collector.collectData(tempfile.toString());

        // then
        List<String> content = getContent();
        Assertions.assertEquals(1, content.size());
        Assertions.assertEquals(Utils.toJson(dataSet), content.get(0).trim());

        validateOutputs(new String[]{
                "Enter the chance for converting maps from Influencing Scarab of conversion and conversion type.",
                "s:shaper, e:elder, c:conqueror, y:synthesis",
                "Format: ^\\s*$|^[1-9]\\d*[secy]$",
                "Enter maps dropped.",
                "r:regular, s:shaper, e:elder, c:conqueror, y:synthesis, t:t17, u:unique, o: originator",
                "Format: ^$|^[rsecytuo\\-]*$",
                "Enter maps dropped by boss.",
                "Empty for not killing boss, - for no drops",
                "Format: ^-?$|^[rsecytuo,]*$",
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
                .line("20e")
                .line("r-eo-e-r-t-r")
                .line("r,o")
                .line(addAction)
                .line("15y")
                .emptyLine()
                .line("-")
                .line(addAction)
                .emptyLine()
                .line("r")
                .emptyLine()
                .line(addAction)
                .line("21s")
                .line("sor-ors-o-so-os-sr-rs")
                .line("o")
                .line(actions.get("Exit"))
                .set();

        MapDropDataSet dataSet1 = new MapDropDataSet(
                nullStrat,
                20,
                ELDER_MAP,
                List.of(MAP, ORIGINATOR_ELDER_MAP, ELDER_MAP, MAP, T17_MAP, MAP),
                List.of(MAP, ORIGINATOR_MAP)
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
        MapDropDataSet dataSet4 = new MapDropDataSet(
                nullStrat,
                21,
                SHAPER_MAP,
                List.of(ORIGINATOR_NON_GUARDIAN_SHAPER_MAP, ORIGINATOR_NON_GUARDIAN_SHAPER_MAP, ORIGINATOR_MAP, ORIGINATOR_SHAPER_MAP, ORIGINATOR_SHAPER_MAP, NON_GUARDIAN_SHAPER_MAP, NON_GUARDIAN_SHAPER_MAP),
                List.of(ORIGINATOR_MAP)
        );

        // when
        collector.collectData(tempfile.toString());

        // then
        List<String> content = getContent();
        Assertions.assertEquals(4, content.size());
        Assertions.assertEquals(Utils.toJson(dataSet1), content.get(0).trim());
        Assertions.assertEquals(Utils.toJson(dataSet2), content.get(1).trim());
        Assertions.assertEquals(Utils.toJson(dataSet3), content.get(2).trim());
        Assertions.assertEquals(Utils.toJson(dataSet4), content.get(3).trim());

        validateOutputs(new String[]{
                "Enter the chance for converting maps from Influencing Scarab of conversion and conversion type.",
                "s:shaper, e:elder, c:conqueror, y:synthesis",
                "Format: ^\\s*$|^[1-9]\\d*[secy]$",
                "Enter maps dropped.",
                "r:regular, s:shaper, e:elder, c:conqueror, y:synthesis, t:t17, u:unique, o: originator",
                "Format: ^$|^[rsecytuo\\-]*$",
                "Enter maps dropped by boss.",
                "Empty for not killing boss, - for no drops",
                "Format: ^-?$|^[rsecytuo,]*$",
                "What would you like to do?",
                null,
                "Enter the chance for converting maps from Influencing Scarab of conversion and conversion type.",
                "s:shaper, e:elder, c:conqueror, y:synthesis",
                "Format: ^\\s*$|^[1-9]\\d*[secy]$",
                "Enter maps dropped.",
                "r:regular, s:shaper, e:elder, c:conqueror, y:synthesis, t:t17, u:unique, o: originator",
                "Format: ^$|^[rsecytuo\\-]*$",
                "Enter maps dropped by boss.",
                "Empty for not killing boss, - for no drops",
                "Format: ^-?$|^[rsecytuo,]*$",
                "What would you like to do?",
                null,
                "Enter the chance for converting maps from Influencing Scarab of conversion and conversion type.",
                "s:shaper, e:elder, c:conqueror, y:synthesis",
                "Format: ^\\s*$|^[1-9]\\d*[secy]$",
                "Enter maps dropped.",
                "r:regular, s:shaper, e:elder, c:conqueror, y:synthesis, t:t17, u:unique, o: originator",
                "Format: ^$|^[rsecytuo\\-]*$",
                "Enter maps dropped by boss.",
                "Empty for not killing boss, - for no drops",
                "Format: ^-?$|^[rsecytuo,]*$",
                "What would you like to do?",
                null,
                "Enter the chance for converting maps from Influencing Scarab of conversion and conversion type.",
                "s:shaper, e:elder, c:conqueror, y:synthesis",
                "Format: ^\\s*$|^[1-9]\\d*[secy]$",
                "Enter maps dropped.",
                "r:regular, s:shaper, e:elder, c:conqueror, y:synthesis, t:t17, u:unique, o: originator",
                "Format: ^$|^[rsecytuo\\-]*$",
                "Enter maps dropped by boss.",
                "Empty for not killing boss, - for no drops",
                "Format: ^-?$|^[rsecytuo,]*$",
                "What would you like to do?"
        });
    }

    @ParameterizedTest
    @MethodSource("provideAddActions")
    void add_attemptInvalidInputs(String addAction) throws IOException {
        // given
        InputBuilder.start()
                .line(0)
                .line(addAction)
                .line("-20e")
                .line("20")
                .line("e")
                .line("20e")
                .line("r-b")
                .line("rr")
                .line("r--r")
                .line("rob")
                .line("r-es")
                .line("or")
                .line("r-re")
                .line("rf")
                .line("rr")
                .line("r,reso")
                .line("r")
                .line(actions.get("Exit"))
                .set();

        MapDropDataSet dataSet = new MapDropDataSet(
                nullStrat,
                20,
                ELDER_MAP,
                List.of(MAP, NON_GUARDIAN_ELDER_MAP),
                List.of(MAP)
        );

        // when
        collector.collectData(tempfile.toString());

        // then
        List<String> content = getContent();
        Assertions.assertEquals(1, content.size());
        Assertions.assertEquals(Utils.toJson(dataSet), content.get(0).trim());

        validateOutputs(new String[]{
                "Enter the chance for converting maps from Influencing Scarab of conversion and conversion type.",
                "s:shaper, e:elder, c:conqueror, y:synthesis",
                "Format: ^\\s*$|^[1-9]\\d*[secy]$",
                INVALID,
                "Enter the chance for converting maps from Influencing Scarab of conversion and conversion type.",
                "s:shaper, e:elder, c:conqueror, y:synthesis",
                "Format: ^\\s*$|^[1-9]\\d*[secy]$",
                INVALID,
                "Enter the chance for converting maps from Influencing Scarab of conversion and conversion type.",
                "s:shaper, e:elder, c:conqueror, y:synthesis",
                "Format: ^\\s*$|^[1-9]\\d*[secy]$",
                INVALID,
                "Enter the chance for converting maps from Influencing Scarab of conversion and conversion type.",
                "s:shaper, e:elder, c:conqueror, y:synthesis",
                "Format: ^\\s*$|^[1-9]\\d*[secy]$",
                "Enter maps dropped.",
                "r:regular, s:shaper, e:elder, c:conqueror, y:synthesis, t:t17, u:unique, o: originator",
                "Format: ^$|^[rsecytuo\\-]*$",
                INVALID,
                "Enter maps dropped.",
                "r:regular, s:shaper, e:elder, c:conqueror, y:synthesis, t:t17, u:unique, o: originator",
                "Format: ^$|^[rsecytuo\\-]*$",
                INVALID,
                "Enter maps dropped.",
                "r:regular, s:shaper, e:elder, c:conqueror, y:synthesis, t:t17, u:unique, o: originator",
                "Format: ^$|^[rsecytuo\\-]*$",
                INVALID,
                "Enter maps dropped.",
                "r:regular, s:shaper, e:elder, c:conqueror, y:synthesis, t:t17, u:unique, o: originator",
                "Format: ^$|^[rsecytuo\\-]*$",
                INVALID,
                "Enter maps dropped.",
                "r:regular, s:shaper, e:elder, c:conqueror, y:synthesis, t:t17, u:unique, o: originator",
                "Format: ^$|^[rsecytuo\\-]*$",
                INVALID,
                "Enter maps dropped.",
                "r:regular, s:shaper, e:elder, c:conqueror, y:synthesis, t:t17, u:unique, o: originator",
                "Format: ^$|^[rsecytuo\\-]*$",
                INVALID,
                "Enter maps dropped.",
                "r:regular, s:shaper, e:elder, c:conqueror, y:synthesis, t:t17, u:unique, o: originator",
                "Format: ^$|^[rsecytuo\\-]*$",
                "Enter maps dropped by boss.",
                "Empty for not killing boss, - for no drops",
                "Format: ^-?$|^[rsecytuo,]*$",
                INVALID,
                "Enter maps dropped by boss.",
                "Empty for not killing boss, - for no drops",
                "Format: ^-?$|^[rsecytuo,]*$",
                INVALID,
                "Enter maps dropped by boss.",
                "Empty for not killing boss, - for no drops",
                "Format: ^-?$|^[rsecytuo,]*$",
                INVALID,
                "Enter maps dropped by boss.",
                "Empty for not killing boss, - for no drops",
                "Format: ^-?$|^[rsecytuo,]*$",
                "What would you like to do?"
        });
    }
}
