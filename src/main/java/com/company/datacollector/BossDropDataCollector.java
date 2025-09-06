package com.company.datacollector;

import com.company.datasets.datasets.BossDropDataSet;
import com.company.utils.ParseUtils;
import com.company.datacollector.Survey.Question;

import java.util.List;

public class BossDropDataCollector extends DataCollector<BossDropDataSet> {

    public BossDropDataCollector() {
        super(BossDropDataSet.class, BossDropDataSet::builder);
    }

    @Override
    protected List<Question> getQuestions() {
        return List.of(
                Question.ask("boss", "Enter the name of the boss.")
                        .build(),

                Question.ask("uber", "Is the boss uber?")
                        .options(new String[]{"y", "n"})
                        .normalize(ParseUtils::toBool)
                        .build(),

                Question.ask("witnessed", "Was the boss witnessed by the Maven?")
                        .options(new String[]{"y", "n"})
                        .normalize(ParseUtils::toBool)
                        .build(),

                Question.ask("guaranteedDrop", "Which unique was the guaranteed drop?")
                        .normalize(ParseUtils::parseToBossLoot)
                        .emptyToNull()
                        .build(),

                Question.ask("extraDrops", "Input extra drops to track.")
                        .normalize(ParseUtils::toLootList)
                        .multiline()
                        .build(),

                Question.ask("quantity", "Enter the area quantity.")
                        .regex("^$|^\\d+$")
                        .normalize(ParseUtils::toInt)
                        .emptyToNull()
                        .build()
        );
    }

}
