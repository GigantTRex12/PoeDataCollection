package main.com.company;

import main.com.company.dataanalyzer.DataAnalyzer;
import main.com.company.dataanalyzer.KalandraMistDataAnalyzer;
import main.com.company.datacollector.DataCollector;
import main.com.company.datacollector.KalandraMistDataCollector;
import main.com.company.datacollector.MapDropDataCollector;
import main.com.company.datacollector.UltimatumDataCollector;

import java.io.IOException;

import static main.com.company.utils.IOUtils.input;
import static main.com.company.utils.IOUtils.print;

public class Main {

    public static void main(String[] args) throws IOException {
        test();

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

    public static void test() throws IOException {
    }
}
