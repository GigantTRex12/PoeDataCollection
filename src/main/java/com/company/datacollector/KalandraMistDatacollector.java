package com.company.datacollector;

import com.company.datasets.datasets.KalandraMistDataSet;

public class KalandraMistDatacollector extends DataCollector<KalandraMistDataSet> {
    public KalandraMistDatacollector() {
        super(KalandraMistDataSet.class, KalandraMistDataSet::builder);
    }
}
