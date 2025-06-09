package main.com.company.dataanalyzer;

import main.com.company.datasets.KalandraMistDataSet;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class KalandraMistDataAnalyzer extends DataAnalyzer<KalandraMistDataSet> {
    public KalandraMistDataAnalyzer(String filename) throws IOException {
        super(filename);
        readData(KalandraMistDataSet.class);
    }

    @Override
    public void analyzeData() {
        Map<KalandraMistDataSet.MistType, List<KalandraMistDataSet>> filtered = filter(KalandraMistDataSet::getType);
        filtered.put(null, this.data);

        for (KalandraMistDataSet.MistType k : filtered.keySet()) {

        }
    }
}
