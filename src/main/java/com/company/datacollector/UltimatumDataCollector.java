package com.company.datacollector;

import com.company.datasets.datasets.DataSet;
import com.company.datasets.datasets.UltimatumDataSet;

import static com.company.utils.IOUtils.print;

public class UltimatumDataCollector extends DataCollector<UltimatumDataSet> {

    @Override
    protected Class<? extends DataSet> getGenericClass() {return UltimatumDataSet.class;}

    @Override
    protected boolean validateDataSet(UltimatumDataSet dataSet) {
        if (dataSet.getRewards().isEmpty()) {
            print("Reward list should not be empty");
            return false;
        }
        return true;
    }
}
