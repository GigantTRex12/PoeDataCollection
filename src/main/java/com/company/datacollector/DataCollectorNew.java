package com.company.datacollector;

import com.company.datasets.datasets.DataSet;
import com.company.datasets.other.metadata.Strategy;
import com.company.exceptions.StrategyCreationInterruptedException;
import lombok.Getter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Map.entry;
import static com.company.utils.FileUtils.append;
import static com.company.utils.IOUtils.*;

public abstract class DataCollectorNew<T extends DataSet> {
    @Getter
    private static final Map<String, String> actions = Map.ofEntries(
            entry("AddData", "a"),
            entry("AddFromSingleInput", "af"),
            entry("AddMultipleFromSingleInput", "am"),
            entry("ClearData", "c"),
            entry("Save", "s"),
            entry("PrintData", "p"),
            entry("AddStrat", "as"),
            entry("ChangeStrat", "cs"),
            entry("Exit", "e")
    );

    protected List<T> data = new ArrayList<>();
    protected Strategy currStrat;
    private Map<Integer, Strategy> strategyIds;

    public DataCollectorNew() {}

    public void collectData(String filename) throws IOException {
        initializeAndPickStrat();
        while (true) {
            String action = input("What would you like to do?", actions).toLowerCase();
            boolean exit = false;
            switch (action) {
                case ("adddata"):
                case ("a"):
                    this.addData();
                    break;
                case ("addfromsingleinput"):
                case ("af"):
                    this.addDataFull();
                    break;
                case ("addmultiplefromsingleinput"):
                case ("am"):
                    this.addMultipleDataFull();
                    break;
                case ("cleardata"):
                case ("c"):
                    this.data.clear();
                    break;
                case ("save"):
                case ("s"):
                    this.addAllDataToFile(filename);
                    break;
                case ("printdata"):
                case ("p"):
                    for (DataSet ds : this.data) {
                        print(ds);
                    }
                    break;
                case ("addstrat"):
                case ("as"):
                    this.addStrat();
                    break;
                case ("changestrat"):
                case ("cs"):
                    this.currStrat = pickStrat();
                    break;
                case ("exit"):
                case ("e"):
                    this.addAllDataToFile(filename);
                    exit = true;
                    break;
            }
            if (exit) {
                break;
            }
        }
    }

    private void addAllDataToFile(String filename) {
        append(filename, this.data);
        this.data.clear();
    }

    private Strategy pickStrat() {
        printList(new ArrayList<>(strategyIds.values()));
        List<String> stratIds = strategyIds.keySet().stream().map(Object::toString).collect(Collectors.toList());
        return strategyIds.get(Integer.parseInt(input("Pick one of the above strategies by id", stratIds)));
    }

    private void initializeAndPickStrat() {
        strategyIds = Strategy.getAll();
        this.currStrat = pickStrat();
    }

    private void addStrat() {
        Strategy newStrat;
        try {
            newStrat = Strategy.create();
        } catch (StrategyCreationInterruptedException e) {
            print("Strategy creation stopped");
            return;
        }
        this.strategyIds.put(newStrat.getId(), newStrat);
        currStrat = newStrat;
        print("Added new Strategy and switched to new Strategy");
    }

    protected abstract void addData();

    protected void addDataFull() {
        print("Not supported for this type of data yet");
    }

    protected void addMultipleDataFull() {
        print("Not supported for this type of data yet");
    }
}
