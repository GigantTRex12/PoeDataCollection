package com.company;

import com.company.dataanalyzer.DataAnalyzer;
import com.company.dataanalyzer.KalandraMistDataAnalyzer;
import com.company.datacollector.DataCollector;
import com.company.datacollector.KalandraMistDataCollector;
import com.company.datacollector.MapDropDataCollector;
import com.company.datacollector.UltimatumDataCollector;
import com.company.datasets.datasets.DataSet;
import com.company.datasets.datasets.KalandraMistDataSet;
import com.company.datasets.datasets.MapDropDataSet;
import com.company.datasets.datasets.UltimatumDataSet;

import java.io.IOException;
import java.util.Map;

import static com.company.utils.IOUtils.*;
import static java.util.Map.entry;

public class Main {

    public static void main(String[] args) throws IOException {
        final String dataPath = "Data/";
        final Map<String, Class<? extends DataSet>> inputToClass = Map.ofEntries(
                entry("mist", KalandraMistDataSet.class),
                entry("map drops", MapDropDataSet.class),
                entry("ultimatum", UltimatumDataSet.class)
        );
        final Map<Class<? extends DataSet>, String> classToFilename = Map.ofEntries(
                entry(KalandraMistDataSet.class, "mist.txt"),
                entry(UltimatumDataSet.class, "ultimatum.txt"),
                entry(MapDropDataSet.class, "mapDrops.txt")
        );

        String[] options1 = {"a", "analyze", "c", "collect", "exit"};
        String input1 = input("What would you like to do?", options1).toLowerCase();
        if (input1.equals("exit")) {
            print("Exiting");
            return;
        }
        String[] options2 = {"mist", "map drops", "ultimatum", "exit"};
        String input2 = input("Which type of Data would you like to work with?", options2).toLowerCase();

        DataCollector collector;
        DataAnalyzer analyzer = null;
        switch (input2) {
            case "mist":
                String filename = "Data/mist.txt";
                collector = new KalandraMistDataCollector(filename);
                analyzer = new KalandraMistDataAnalyzer(filename);
                break;
            case "map drops":
                collector = new MapDropDataCollector("Data/mapDrops.txt");
                break;
            case "ultimatum":
                collector = new UltimatumDataCollector("Data/ultimatum.txt");
                break;
            default:
                print("Exiting");
                return;
        }

        switch (input1) {
            case "a":
            case "analyze":
                if (analyzer != null) {
                    analyzer.analyzeData();
                }
                break;
            case "c":
            case "collect":
                if (collector != null) {
                    collector.collectData();
                }
                break;
        }
    }
}
