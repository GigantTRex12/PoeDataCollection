package com.company.dataset_lib;

import dataset.BaseDataSet;

public class DataSet extends BaseDataSet {

    public DataSet(Strategy metadata) {
        super(metadata);
    }

    @Override
    public Strategy getMetadata() {
        return (Strategy) this.metadata;
    }

    public Strategy getStrategy() {
        return this.getMetadata();
    }

    @Override
    public String toString() {
        return "DataSet{" +
                "metadata=" + metadata +
                '}';
    }
}
