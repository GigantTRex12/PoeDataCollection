package com.company.dataanalyzer;

import com.company.testutils.InputBuilder;
import com.company.testutils.TestWithOutputs;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class BossDropDataAnalyzerTest extends TestWithOutputs {

    private BossDropDataAnalyzer analyzer;

    // no special grouping, evaluate guaranteed drops
    @Test
    @Disabled("WIP")
    void analyzerTest1() throws IOException {
        // given
        analyzer = new BossDropDataAnalyzer("src/test/resources/bossdrop_examples_1.txt");
        InputBuilder.start()
                .line("a")
                .emptyLine() // don't group by strategy
                .emptyLine() // don't group by league
                .emptyLine() // no filter on boss name
                .line("n") // don't group by witness
                .line("n") // don't group by quant
                .line("guaranteedDrop") // evaluate guaranteed drop
                .line("e")
                .set();

        // when
        analyzer.analyzeData();

        // then
        validateOutputs(new String[]{
                "Do you want to group by strategy?",
                null,
                "Do you want to group by league?",
                null,
                "Pick a lowerCaseBossname to filter by (leave empty to evaluate all)",
                null,
                "Do you want to group by witnessed?",
                null,
                "Do you want to group by quantInStepsOfTen?",
                null,
                "What do you want to evaluate?",
                null,
                // TODO: figure out order
                "lowerCaseBossname: the elder",
                "Cyclopean Coil: 33.3%",
                "Blasphemer's Grasp: 66.7%",
                "lowerCaseBossname: the maven",
                "Arn's Anguish: 20%",
                "Graven's Secret: 40%",
                "Legacy of Fury: 40%",
                "What would you like to do?"
        }, 0);
    }

}
