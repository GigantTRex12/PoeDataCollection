package com.company.datacollector;

import com.company.datasets.datasets.DataSet;
import com.company.datasets.datasets.MapDropDataSet;

import static com.company.utils.IOUtils.input;

public class MapDropDataCollector extends DataCollector<MapDropDataSet> {

    public MapDropDataCollector() {
        super();
    }

    @Override
    protected Class<? extends DataSet> getGenericClass() {return MapDropDataSet.class;}
}
