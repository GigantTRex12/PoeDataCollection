package com.company;

import com.company.api.DbReader;
import com.company.api.DbWriter;
import com.company.dataanalyzer.BossDropDataAnalyzer;
import com.company.dataanalyzer.DataAnalyzer;
import com.company.dataanalyzer.KalandraMistDataAnalyzer;
import com.company.datacollector.*;
import com.company.datasets.datasets.BossDropDataSet;
import com.company.datasets.datasets.KalandraMistDataSet;
import com.company.datasets.datasets.MapDropDataSet;
import com.company.datasets.datasets.UltimatumDataSet;
import com.company.datasets.other.metadata.Strategy;
import com.company.utils.FileUtils;
import com.company.utils.IOUtils;
import com.company.utils.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import static com.company.utils.FileUtils.initLogs;
import static com.company.utils.IOUtils.input;
import static com.company.utils.IOUtils.print;
import static java.util.Map.entry;

public class Main {

    public static Properties properties;

    static void main() throws IOException {
        loadConfig();
        initLogs();
        final String dataPath = "Data/";
        final Map<String, String> typeToFilename = Map.ofEntries(
                entry("mist", "mist.txt"),
                entry("ultimatum", "ultimatum.txt"),
                entry("map drops", "mapDrops.txt"),
                entry("boss drops", "bossDrops.txt"),
                entry("cadiro", "cadiro.txt")
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
                    case "cadiro" -> collector = new CadiroDataCollector();
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
                    case "mist" -> analyzer = new KalandraMistDataAnalyzer(filename);
                    case "boss drops" -> analyzer = new BossDropDataAnalyzer(filename);
                    default -> {
                        print("Exiting");
                        return;
                    }
                }
                if (analyzer != null) analyzer.analyzeData();
            }
            else {
                print("Exiting");
                return;
            }
        }
    }

    public static void loadConfig() throws IOException {
        final Properties defaultProp = new Properties();
        defaultProp.load(Main.class.getResourceAsStream("/default.properties"));
        final Properties customProp = new Properties();
        InputStream f = Main.class.getResourceAsStream("/custom.properties");
        if (f != null) {
            customProp.load(f);
        }
        properties = new Properties();
        properties.putAll(defaultProp);
        properties.putAll(customProp);
    }

}

// mvn clean compile exec:java