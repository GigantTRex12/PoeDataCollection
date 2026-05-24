package com.company.datacollector_lib;

import collector.BaseDataCollector;
import collector.Question;
import collector.Survey;
import com.company.api.DbReader;
import com.company.api.DbWriter;
import com.company.dataset_lib.DataSet;
import com.company.dataset_lib.Strategy;
import com.company.utils.IOUtils;

import java.util.*;

public abstract class DataCollector<T extends DataSet> extends BaseDataCollector<T> {

    private Map<Integer, Strategy> strategies;
    private final Survey strategySurvey;

    protected DataCollector() {
        super();
        strategies = null;
        strategySurvey = new Survey(List.of(
                Question.ask("league", "What league is it?").emptyToNull().build(),
                Question.ask("tree", "Enter a description of the atlas tree used.").emptyToNull().build(),
                Question.ask("treeUrl", "Paste an url to the atlas tree used.").emptyToNull().build(),
                Question.ask("scarabs", "What scarabs where used? (scarab1;2*scarab2, _ to not specify)")
                        .regex("^((\\d+\\*)?[\\w\\s]+)(;(\\d+\\*)?[\\w\\s]+)*$|^$|^_$")
                        .normalize(this::parseScarabs)
                        .build(),
                Question.ask("mapLayout", "What map layout was run?").emptyToNull().build(),
                Question.ask("mapRolling", "How where the maps rolled?").emptyToNull().build(),
                Question.ask("mapCraft", "What map craft was used?").when(_ -> false).build()
        ));
        this.actions.put("AddStrat", this::addStrategy, List.of("as"));
        this.actions.put("ChangeStrat", this::setMetadata, List.of("cs"));
    }

    protected Strategy getMetadata() {
        return this.currMetadata instanceof Strategy ? (Strategy) this.currMetadata : null;
    }

    @Override
    protected boolean validateDataSet(T dataSet) {
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
            int id = DbWriter.writeStrategy(newStrat);
            newStrat.setId(id);
            this.strategies.put(newStrat.getId(), newStrat);
            this.currMetadata = newStrat;
        }
    }

    private Strategy createStrategy() {
        Map<String, ?> map = strategySurvey.run();
        return new Strategy(
                null,
                (String) map.get("league"),
                (String) map.get("tree"),
                (String) map.get("treeUrl"),
                (String[]) map.get("scarabs"),
                (String) map.get("mapLayout"),
                (String) map.get("mapRolling"),
                (String) map.get("mapCraft")
        );
    }

    private String[] parseScarabs(String s) {
        if ("_".equals(s)) return null;
        List<String> scarabList = new ArrayList<>();
        for (String scarab : s.split(";")) {
            String[] rep = scarab.split("\\*");
            if (rep.length == 1) {
                scarabList.add(scarab);
            }
            else {
                for (int i = 0; i < Integer.parseInt(rep[0]); i++) {
                    scarabList.add(rep[1]);
                }
            }
        }
        return scarabList.toArray(new String[0]);
    }

}
