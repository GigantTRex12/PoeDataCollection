package com.company.datacollector_lib;

import collector.BaseDataCollector;
import com.company.api.DbReader;
import com.company.dataset_lib.DataSet;
import com.company.dataset_lib.Strategy;
import com.company.utils.IOUtils;
import dataset.BaseDataSet;

import java.util.*;

public abstract class DataCollector<T extends DataSet> extends BaseDataCollector<T> {

    private Map<Integer, Strategy> strategies;

    public DataCollector() {
        super();
        strategies = null;
        //this.actions.put("AddStrat", this::addStrategy, List.of("as"));
    }

    protected Strategy getMetadata() {
        return this.currMetadata instanceof Strategy ? (Strategy) this.currMetadata : null;
    }

    @Override
    protected boolean validateDataSet(BaseDataSet dataSet) {
        if (dataSet == null || dataSet.getMetadata() == null) {
            return false;
        }
        return super.validateDataSet(dataSet);
    }

    @Override
    protected void setMetadata() {
        if (strategies == null) {
            strategies = new LinkedHashMap<>();
            DbReader.readStrategies().stream()
                    .sorted(Comparator.comparingInt(Strategy::getId))
                    .forEach(s -> strategies.put(s.getId(), s));
        }
        List<String> ids = new ArrayList<>();
        strategies.forEach((id, strategy) -> {
            ids.add(String.valueOf(id));
            IO.println(strategy);
        });
        String id = IOUtils.input("Pick one of the above strategies by id", ids);
        this.currMetadata = strategies.get(Integer.parseInt(id));
    }

    protected void addStrategy() {
        Strategy newStrat = createStrategy();
        IO.println(newStrat);
        if (newStrat != null && IOUtils.inputBool("Is this correct?")) {
            this.strategies.put(newStrat.getId(), newStrat);
            this.currMetadata = newStrat;
        }
    }

    private Strategy createStrategy() {
        // TODO
        return null;
    }

}
