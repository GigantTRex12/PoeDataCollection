package com.company.DataCollectors;

import com.company.DataTypes.DataSet;
import com.company.DataTypes.Strategy;
import com.company.Exceptions.FileAlreadyExistsException;
import com.company.Utilities;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.company.FileOrganizer.append;
import static com.company.FileOrganizer.open;
import static com.company.IOSystem.*;

public abstract class DataCollector {
    protected String filename;
    protected List<DataSet> dataSets;
    protected Strategy currStrat;

    public DataCollector(String filename) {
        this.filename = filename;
        this.dataSets = new ArrayList<DataSet>();
        try {
            open(filename, 'x');
        }
        catch (FileAlreadyExistsException e) {}
        catch (IOException e) {
            print(e);
        }
    }

    public abstract void collectData();

    protected void addTemporaryData(DataSet data) {
        this.dataSets.add(data);
    }

    protected void addAllDataToFile() {
        for (DataSet data : this.dataSets) {
            String rep = toJson(data);
            this.addDataToFile(rep);
        }
        this.dataSets.clear();
    }

    protected void addDataToFile(DataSet data) {
        this.addDataToFile(toJson(data));
    }

    private void addDataToFile(String data) {
        try {
            append(this.filename, data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected Strategy pickStrat(List<Strategy> strategies, Map<String, Strategy> strategyIds) {
        printList(strategies);
        return strategyIds.get(input("Pick one of the above strategies by id", strategyIds.keySet()));
    }

    private static String toJson(DataSet object) {
        return Utilities.toJson(object);
    }

    protected void pickCurrStrat() {
        List<Strategy> strategies = Strategy.getAllList();
        Map<String, Strategy> strategyIds = new HashMap<String, Strategy>();
        for (Strategy strat : strategies) {
            strategyIds.put(String.valueOf(strat.getId()), strat);
        }
        this.currStrat = pickStrat(strategies, strategyIds);
    }
    
}
