package com.company.DataCollectors;

import com.company.DataTypes.DataSet;
import com.company.DataTypes.Strategy;
import com.company.Exceptions.FileAlreadyExistsException;
import com.company.Exceptions.StrategyCreationInterruptedException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.company.FileOrganizer.append;
import static com.company.FileOrganizer.open;
import static com.company.IOSystem.*;
import static com.company.Utilities.toJson;

public class DataCollectorNew<T extends DataSet> {
    private static final String[] actions = {
            "AddData", "a",
            "AddFromSingleInput", "af",
            "ClearData", "c",
            "Save", "s",
            "PrintData", "p",
            "AddStrat", "as",
            "ChangeStrat", "cs",
            "Exit", "e"
    };

    private final String filename;
    private List<T> data;
    private Strategy currStrat;
    private Map<Integer, Strategy> strategyIds;

    public DataCollectorNew(String filename) {
        this.filename = filename;
        this.data = new ArrayList<>();
        try {
            open(filename, 'x');
        }
        catch (FileAlreadyExistsException ignored) {}
        catch (IOException e) {
            print(e);
        }
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

    private void addData() {
        // TODO
    }

    private void addDataFull() {
        print("Not supported yet");
        // TODO
    }
}
