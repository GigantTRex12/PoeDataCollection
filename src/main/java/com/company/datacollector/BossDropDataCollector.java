package com.company.datacollector;

import com.company.datasets.datasets.BossDropDataSet;
import com.company.datasets.datasets.DataSet;

import static com.company.utils.IOUtils.input;
import static com.company.utils.IOUtils.multilineInput;

public class BossDropDataCollector extends DataCollector<BossDropDataSet> {
    public BossDropDataCollector() {
        super();
    }

    @Override
    protected Class<? extends DataSet> getGenericClass() {return BossDropDataSet.class;}
}
