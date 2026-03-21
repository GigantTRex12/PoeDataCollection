package com.company.dataanalyzer_lib.analyzers;

import analyzer.GroupingDefinition;
import analyzer.Question;
import com.company.api.DbReader;
import com.company.dataanalyzer_lib.DataAnalyzer;
import com.company.dataset_lib.datasets.BossDropDataSet;

import java.util.List;

public class BossDropDataAnalyzer extends DataAnalyzer<BossDropDataSet> {

    public BossDropDataAnalyzer() {
        super(DbReader::readBossDropDataSets);
    }

    @Override
    protected List<Question<BossDropDataSet>> getQuestions() {
        Class<BossDropDataSet> c = BossDropDataSet.class;
        return List.of(
                Question.ask("boss_unique", c)
                        .forcedGrouping(BossDropDataSet::lowerCaseBossname)
                        .groupings(new GroupingDefinition<>("league", d -> d.getStrategy().getLeague()))
                        .evaluator(d -> d.getGuaranteedDrop().getName(), WILSON_CONFIDENCE)
                        .conditionAll(d -> d.getGuaranteedDrop() != null)
                        .build()
        );
    }
}
