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

    @Override
    public String toString() {
        return "DataSet{" +
                "metadata=" + metadata +
                '}';
    }
}
