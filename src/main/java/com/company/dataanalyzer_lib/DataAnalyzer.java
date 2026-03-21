package com.company.dataanalyzer_lib;

import analyzer.BaseDataAnalyzer;
import com.company.dataset_lib.DataSet;

import java.util.List;
import java.util.function.Supplier;

public abstract class DataAnalyzer<T extends DataSet> extends BaseDataAnalyzer<T> {

    public DataAnalyzer(Supplier<List<T>> dataSupplier) {
        super(dataSupplier.get());
    }

}
