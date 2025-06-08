package main.com.company.dataanalyzer;

import main.com.company.datasets.DataSet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public abstract class DataAnalyzer<T extends DataSet> {
    private String filename;
    protected List<T> data;

    public DataAnalyzer(String filename) {
        this.filename = filename;
        this.data = new ArrayList<T>();
    }

    public abstract void analyzeData();

    protected void readData(Class<T> clazz) throws IOException {
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
