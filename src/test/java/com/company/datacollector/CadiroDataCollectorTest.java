package com.company.datacollector;

import com.company.datasets.datasets.CadiroDataSet;
import com.company.testutils.InputBuilder;
import com.company.utils.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.util.List;

import static com.company.utils.Utils.toJson;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CadiroDataCollectorTest extends DataCollectorTest {

    private CadiroDataCollector collector;

    @BeforeEach
    void setup() {
        collector = new CadiroDataCollector();
    }

    @ParameterizedTest
    @MethodSource("provideAddActions")
    void add_oneDataSet(String addAction) throws IOException {
        // given
        InputBuilder.start()
                .line(0)
                .line(addAction)
                .line(3)
                .multiLine(new String[]{
                        "Mistwall 17932",
                        "Pyre 6545",
                        "Squirming Terror 16531"
                })
                .line(actions.get("Exit"))
                .set();

        CadiroDataSet dataSet = new CadiroDataSet(
                nullStrat,
                3,
                List.of(
                        new Pair<>("Mistwall", 17932),
                        new Pair<>("Pyre", 6545),
                        new Pair<>("Squirming Terror", 16531)
                )
        );

        // when
        collector.collectData(tempfile.toString());

        // then
        List<String> content = getContent();
        assertEquals(1, content.size());
        assertEquals(toJson(dataSet), content.get(0).trim());

        validateOutputs(new String[]{
                "What is the tier?",
                "Format: ^[1-3]$",
                "Enter the prices.",
                "Format: ^.* \\d+$",
                "What would you like to do?"
        });
    }

    @ParameterizedTest
    @MethodSource("provideAddActions")
    @Disabled("Behavior for reflection vs functional is different, both are acceptable. Need to write seperate tests or something")
    void add_attemptInvalidInputs(String addAction) throws IOException {
        // given
        InputBuilder.start()
                .line(0)
                .line(addAction)
                .line(0)
                .line(4)
                .line("two")
                .emptyLine()
                .line(2)
                .multiLine(new String[]{
                        "Agnerod East 12700",
                        "Doomsower",
                        "Kaom's Binding 14182"
                })
                .multiLine(new String[]{
                        "12700",
                        "Doomsower 13969",
                        "Kaom's Binding 14182"
                })
                .multiLine(new String[]{
                        "Agnerod East 12700",
                        "Doomsower 13969",
                        "Kaom's Binding -14182"
                })
                .multiLine(new String[]{
                        "Agnerod East 12700",
                        "Doomsower 13969",
                        "Kaom's Binding 14182"
                })
                .line(actions.get("Exit"))
                .set();

        CadiroDataSet dataSet = new CadiroDataSet(
                nullStrat,
                2,
                List.of(
                        new Pair<>("Agnerod East", 12700),
                        new Pair<>("Doomsower", 13969),
                        new Pair<>("Kaom's Binding", 14182)
                )
        );

        // when
        collector.collectData(tempfile.toString());

        // then
        List<String> content = getContent();
        assertEquals(1, content.size());
        assertEquals(toJson(dataSet), content.get(0).trim());

        validateOutputs(new String[]{
                "What is the tier?",
                "Format: ^[1-3]$",
                INVALID,
                "What is the tier?",
                "Format: ^[1-3]$",
                INVALID,
                "What is the tier?",
                "Format: ^[1-3]$",
                INVALID,
                "What is the tier?",
                "Format: ^[1-3]$",
                INVALID,
                "What is the tier?",
                "Format: ^[1-3]$",
                "Enter the prices.",
                "Format: ^.* \\d+$",
                INVALID,
                "Enter the prices.",
                "Format: ^.* \\d+$",
                INVALID,
                "Enter the prices.",
                "Format: ^.* \\d+$",
                INVALID,
                "Enter the prices.",
                "Format: ^.* \\d+$",
                "What would you like to do?"
        });
    }

}
