package com.company;

import com.company.dataanalyzer.DataAnalyzer;
import com.company.dataanalyzer.KalandraMistDataAnalyzer;
import com.company.datacollector.*;

import java.io.IOException;
import java.util.Map;

import static com.company.utils.IOUtils.*;
import static java.util.Map.entry;

public class Main {

    public static void main(String[] args) throws IOException {
        final String dataPath = "Data/";
        final Map<String, String> typeToFilename = Map.ofEntries(
                entry("mist", "mist.txt"),
                entry("ultimatum", "ultimatum.txt"),
                entry("map drops", "mapDrops.txt"),
                entry("boss drops", "bossDrops.txt")
        );

        Map<String, String> options = Map.ofEntries(
                entry("a", "analyze"),
                entry("c", "collect"),
                entry("e", "exit")
        );
        String input1 = input("What would you like to do?", options).toLowerCase();
        if (input1.equals("exit")) {
            print("Exiting");
            return;
        }
        String input2 = input("Which type of Data would you like to work with?", typeToFilename.keySet()).toLowerCase();

        String filename = dataPath + typeToFilename.get(input2.toLowerCase());
        DataCollector<?> collector;
        DataAnalyzer<?> analyzer = null;
        switch (input2) {
            case "mist":
                collector = new KalandraMistDatacollector();
                analyzer = new KalandraMistDataAnalyzer(filename);
                break;
            case "map drops":
                collector = new MapDropDataCollector();
                break;
            case "ultimatum":
                collector = new UltimatumDataCollector();
                break;
            case "boss drops":
                collector = new BossDropDataCollector();
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
                collector.collectData(filename);
                break;
        }
    }
}
