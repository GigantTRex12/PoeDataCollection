package com.company.dataanalyzer;

import com.company.datasets.datasets.KalandraMistDataSet;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.FileNotFoundException;

public class KalandraMistDataAnalyzer extends DataAnalyzer<KalandraMistDataSet> {

    public KalandraMistDataAnalyzer(String filename) throws FileNotFoundException, JsonProcessingException {
        super(filename, KalandraMistDataSet.class);
    }

}
