package com.company.datacollector;

import com.company.datasets.datasets.MapDropDataSet;

public class MapDropDataCollector extends DataCollector<MapDropDataSet> {
    public MapDropDataCollector() {
        super(MapDropDataSet.class, MapDropDataSet::builder);
    }
}
