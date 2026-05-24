package com.company.dataanalyzer_lib;

import analyzer.BaseDataAnalyzer;
import analyzer.GroupingDefinition;
import com.company.dataset_lib.DataSet;

import java.util.List;
import java.util.function.Supplier;

public abstract class DataAnalyzer<T extends DataSet> extends BaseDataAnalyzer<T> {

    protected final GroupingDefinition<T> strategyGrouper;
    protected final GroupingDefinition<T> leagueGrouper;

    protected DataAnalyzer(Supplier<List<T>> dataSupplier) {
        strategyGrouper = new GroupingDefinition<>("strategy", DataSet::getStrategy);
        leagueGrouper = new GroupingDefinition<>("league", d -> d.getStrategy().getLeague());
        super(dataSupplier.get());
    }

}
