package com.company.datacollector;

import com.company.datasets.datasets.CadiroDataSet;
import com.company.utils.ParseUtils;
import com.company.datacollector.Survey.Question;

import java.util.ArrayList;
import java.util.List;

public class CadiroDataCollector extends DataCollector<CadiroDataSet> {

    public CadiroDataCollector() {
        super(CadiroDataSet.class, CadiroDataSet::builder);
    }

    @Override
    protected List<Question> getQuestions() {
        List<Question> questions = new ArrayList<>();

        questions.add(Question.ask("tier", "What is the tier?")
                .regex("^[1-3]$")
                .normalize(ParseUtils::toInt)
                .build()
        );
        questions.add(Question.ask("uniquesWithCost", "Enter the prices.")
                .normalize(ParseUtils::toMap)
                .multiline()
                .build()
        );

        return questions;
    }

}
