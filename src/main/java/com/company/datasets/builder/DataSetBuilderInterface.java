package com.company.datasets.builder;

import com.company.datasets.datasets.DataSet;
import com.company.datasets.other.metadata.Strategy;

public interface DataSetBuilderInterface<T extends DataSet> {

    DataSetBuilderInterface<T> strategy(Strategy strategy);

    T build();

}
