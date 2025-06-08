package main.com.company.datacollector;

import main.com.company.datasets.DataSet;
import main.com.company.datasets.metadata.Strategy;
import main.com.company.exceptions.FileAlreadyExistsException;
import main.com.company.utils.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static main.com.company.utils.FileUtils.append;
import static main.com.company.utils.FileUtils.create;
import static main.com.company.utils.IOUtils.*;

public abstract class DataCollector {
    protected String filename;
    protected List<DataSet> dataSets;
    protected Strategy currStrat;

    public DataCollector(String filename) {
        this.filename = filename;
        this.dataSets = new ArrayList<DataSet>();
        try {
            create(filename);
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
        return Utils.toJson(object);
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
