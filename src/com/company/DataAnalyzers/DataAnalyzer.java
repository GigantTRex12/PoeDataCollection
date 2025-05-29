package com.company.DataAnalyzers;

import com.company.DataTypes.DataSet;
import com.company.FileOrganizer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static com.company.FileOrganizer.open;
import static com.company.Utilities.parseJson;

public abstract class DataAnalyzer<T extends DataSet> {
    private String filename;
    protected List<T> data;

    public DataAnalyzer(String filename) {
        this.filename = filename;
        this.data = new ArrayList<T>();
    }

    public abstract void analyzeData();

    protected void readData(Class<T> clazz) throws IOException {
        FileOrganizer file = open(this.filename);
        List<String> lines = file.readLines();
        for (String line : lines) {
            if (line.trim().isEmpty()) {
                continue;
            }
            this.data.add(parseJson(line, clazz));
        }
        file.close();
    }

    protected <R> Map<R, List<T>> filter(Function<T, R> func) {
        Map<R, List<T>> map = new HashMap<>();

        for (T t : this.data) {
            R r = func.apply(t);
            map.computeIfAbsent(r, k -> new ArrayList<>());
            map.get(r).add(t);
        }

        return map;
    }
}
