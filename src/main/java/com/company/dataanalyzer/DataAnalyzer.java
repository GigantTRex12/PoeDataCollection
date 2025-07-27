package com.company.dataanalyzer;

import com.company.datasets.datasets.DataSet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public abstract class DataAnalyzer<T extends DataSet> {

    protected List<T> data;

    public DataAnalyzer() {
        this.data = new ArrayList<T>();
    }

    public void analyzeData() {

    }
}
