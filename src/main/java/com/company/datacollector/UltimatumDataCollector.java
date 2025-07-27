package com.company.datacollector;

import com.company.datasets.datasets.UltimatumDataSet;

import static com.company.utils.IOUtils.print;

public class UltimatumDataCollector extends DataCollector<UltimatumDataSet> {

    public UltimatumDataCollector() {
        super(UltimatumDataSet.class, UltimatumDataSet::builder);
    }

    @Override
    protected boolean validateDataSet(UltimatumDataSet dataSet) {
        if (dataSet.getRewards().isEmpty()) {
            print("Reward list should not be empty");
            return false;
        }
        return true;
    }
}
