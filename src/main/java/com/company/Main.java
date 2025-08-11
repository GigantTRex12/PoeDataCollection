package com.company;

import com.company.dataanalyzer.DataAnalyzer;
import com.company.datacollector.*;

import java.io.IOException;
import java.util.Map;

import static com.company.utils.FileUtils.initLogs;
import static com.company.utils.IOUtils.*;
import static java.util.Map.entry;

public class Main {

    public static void main(String[] args) throws IOException {
        initLogs();
        final String dataPath = "Data/";
        final Map<String, String> typeToFilename = Map.ofEntries(
                entry("mist", "mist.txt"),
                entry("ultimatum", "ultimatum.txt"),
                entry("map drops", "mapDrops.txt"),
                entry("boss drops", "bossDrops.txt")
                //entry("jun", "junEncounters.txt")
        );

        Map<String, String> options = Map.ofEntries(
                entry("a", "analyze"),
                entry("c", "collect"),
                entry("e", "exit")
        );

        while (true) {
            String action = input("What would you like to do?", options).toLowerCase();
            if (action.equals("e") || action.equals("exit")) {
                print("Exiting");
                return;
            }
            String dataType = input("Which type of Data would you like to work with?", typeToFilename.keySet()).toLowerCase();

            String filename = dataPath + typeToFilename.get(dataType.toLowerCase());

            if (action.equals("c") || action.equals("collect")) {
                DataCollector<?> collector;
                switch (dataType) {
                    case "mist" -> collector = new KalandraMistDatacollector();
                    case "map drops" -> collector = new MapDropDataCollector();
                    case "ultimatum" -> collector = new UltimatumDataCollector();
                    case "boss drops" -> collector = new BossDropDataCollector();
                    case "jun" -> collector = new JunDataCollector(filename);
                    default -> {
                        print("Exiting");
                        return;
                    }
                }
                collector.collectData(filename);
            } else if (action.equals("a") || action.equals("analyze")) {
                DataAnalyzer<?> analyzer = null;
                switch (dataType) {
                    default -> {
                        print("Exiting");
                        return;
                    }
                }
                //analyzer.analyzeData();
            }
            else {
                print("Exiting");
                return;
            }
        }
    }
}
