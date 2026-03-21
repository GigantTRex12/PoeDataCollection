package com.company.datacollector_lib;

import collector.BaseDataCollector;
import com.company.dataset_lib.DataSet;
import com.company.dataset_lib.Strategy;
import dataset.BaseDataSet;


public abstract class DataCollector<T extends DataSet> extends BaseDataCollector<T> {

    protected Strategy getMetadata() {
        return this.currMetadata instanceof Strategy ? (Strategy) this.currMetadata : null;
    }

    @Override
    protected boolean validateDataSet(BaseDataSet dataSet) {
        if (dataSet == null || dataSet.getMetadata() == null) {
            return false;
        }
        return super.validateDataSet(dataSet);
    }

    @Override
    protected void setMetadata() {
        // TODO: initialize strategy
    }

}
