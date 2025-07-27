package com.company.datacollector;

import com.company.datasets.datasets.BossDropDataSet;

public class BossDropDataCollector extends DataCollector<BossDropDataSet> {
    public BossDropDataCollector() {
        super(BossDropDataSet.class, BossDropDataSet::builder);
    }
}
