package main.com.company.datacollector;

import main.com.company.datasets.DataSet;
import main.com.company.datasets.metadata.Strategy;
import main.com.company.exceptions.StrategyCreationInterruptedException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Map.entry;
import static main.com.company.utils.FileUtils.append;
import static main.com.company.utils.FileUtils.createNew;
import static main.com.company.utils.IOUtils.*;
import static main.com.company.utils.Utils.toJson;

public abstract class DataCollectorNew<T extends DataSet> {
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

    private final String filename;
    protected List<T> data;
    protected Strategy currStrat;
    private Map<Integer, Strategy> strategyIds;

    public DataCollectorNew(String filename) throws IOException {
        this.filename = filename;
        this.data = new ArrayList<>();
        createNew(filename);
    }

    public void collectData() throws IOException {
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
                    this.addAllDataToFile();
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
                    this.addAllDataToFile();
                    exit = true;
                    break;
            }
            if (exit) {
                break;
            }
        }
    }

    private void addAllDataToFile() throws IOException {
        for (DataSet data : this.data) {
            String rep = toJson(data);
            this.addDataToFile(rep);
        }
        this.data.clear();
    }

    private void addDataToFile(String data) throws IOException {
        append(this.filename, data);
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
