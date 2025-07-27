package com.company.dataanalyzer;

import com.company.datasets.datasets.BossDropDataSet;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.FileNotFoundException;

public class BossDropDataAnalyzer extends DataAnalyzer<BossDropDataSet> {

    public BossDropDataAnalyzer(String filename) throws FileNotFoundException, JsonProcessingException {
        super(filename, BossDropDataSet.class);
    }
}
