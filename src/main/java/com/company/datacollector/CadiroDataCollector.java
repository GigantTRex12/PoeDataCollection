package com.company.datacollector;

import com.company.datacollector.Survey.Question;
import com.company.datasets.datasets.CadiroDataSet;
import com.company.utils.ParseUtils;

import java.util.List;

public class CadiroDataCollector extends DataCollector<CadiroDataSet> {

    public CadiroDataCollector() {
        super(CadiroDataSet.class, CadiroDataSet::builder);
    }

    @Override
    protected List<Question> getQuestions() {
        return List.of(
                Question.ask("tier", "What is the tier?")
                        .regex("^[1-3]$")
                        .normalize(ParseUtils::toInt)
                        .build(),

                Question.ask("uniquesWithCost", "Enter the prices.")
                        .normalize(ParseUtils::toUniqueCostPairs)
                        .multiline()
                        .regex("^.* \\d+$")
                        .build()
        );
    }

}
