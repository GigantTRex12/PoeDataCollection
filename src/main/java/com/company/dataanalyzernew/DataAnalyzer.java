package com.company.dataanalyzernew;

import analyzer.BaseDataAnalyzer;
import com.company.utils.FileUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import dataset.BaseDataSet;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public abstract class DataAnalyzer<T extends BaseDataSet> extends BaseDataAnalyzer<T> {

    public DataAnalyzer(String filename, Class<T> t) throws FileNotFoundException, JsonProcessingException {
        super(new ArrayList<>());
        List<String> lines = FileUtils.readLines(filename);
        for (String line : lines) {
            this.data.add(parseJson(line));
        }
    }

    protected abstract T parseJson(String json);

}
