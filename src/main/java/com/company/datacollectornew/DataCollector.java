package com.company.datacollectornew;

import collector.BaseDataCollector;
import com.company.datasets.other.metadata.Strategy;
import dataset.BaseDataSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.company.utils.FileUtils.append;
import static com.company.utils.IOUtils.input;
import static com.company.utils.IOUtils.printList;

public abstract class DataCollector<T extends BaseDataSet> extends BaseDataCollector<T> {

    private final String filename;
    private Map<Integer, Strategy> strategyIds;

    public DataCollector(String filename) {
        strategyIds = Strategy.getAll();
        this.filename = filename;
    }

    @Override
    protected void saveData() {
        append(filename, this.data);
        this.clearData();
    }

    @Override
    protected void setMetadata() {
        printList(new ArrayList<>(strategyIds.values()));
        List<String> stratIds = strategyIds.keySet().stream().map(Object::toString).collect(Collectors.toList());
        this.currMetadata = strategyIds.get(Integer.parseInt(input("Pick one of the above strategies by id", stratIds)));
    }

}
