package com.company.datacollector;

import com.company.datasets.datasets.DataSet;
import com.company.datasets.datasets.KalandraMistDataSet;

import static com.company.utils.IOUtils.input;
import static com.company.utils.IOUtils.multilineInput;

public class KalandraMistDatacollector extends DataCollector<KalandraMistDataSet> {

    public KalandraMistDatacollector() {
        super();
    }

    @Override
    protected Class<? extends DataSet> getGenericClass() {return KalandraMistDataSet.class;}
}
