package com.company.dataanalyzer;

import com.company.datasets.datasets.DataSet;
import com.company.utils.FileUtils;
import com.company.utils.Utils;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public abstract class DataAnalyzer<T extends DataSet> {

    protected List<T> data;

    public DataAnalyzer(String filename, Class<T> t) throws FileNotFoundException, JsonProcessingException {
        data = new ArrayList<T>();
        List<String> lines = FileUtils.readLines(filename);
        for (String line : lines) {
            data.add(Utils.parseJson(line, t));
        }
    }

    public void analyzeData() {

    }

}
