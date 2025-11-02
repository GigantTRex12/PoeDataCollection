package com.company.datasets.other.metadata;

import com.company.exceptions.StrategyCreationInterruptedException;
import com.company.utils.Counter;
import com.company.utils.Utils;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import dataset.Metadata;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static com.company.utils.FileUtils.append;
import static com.company.utils.FileUtils.readLines;
import static com.company.utils.IOUtils.input;
import static com.company.utils.IOUtils.print;
import static com.company.utils.Utils.parseJson;
import static com.company.utils.Utils.toJson;
import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@NoArgsConstructor(force = true)
@AllArgsConstructor
@Getter
public class Strategy extends Metadata {
    private static final String filename = "Data/strategies.txt";

    @JsonProperty("id")
    @JsonInclude(NON_NULL)
    private final Integer id;

    @JsonProperty("league")
    @JsonInclude(NON_NULL)
    private final String league;

    @JsonProperty("tree")
    @JsonInclude(NON_NULL)
    private final String tree;

    @JsonProperty("treeUrl")
    @JsonInclude(NON_NULL)
    private final String treeUrl;

    @JsonProperty("scarabs")
    @JsonInclude(NON_NULL)
    private final String[] scarabs;

    @JsonProperty("map")
    @JsonInclude(NON_NULL)
    private final String mapLayout;

    @JsonProperty("mapRolling")
    @JsonInclude(NON_NULL)
    private final String mapRolling;

    @JsonProperty("mapCraft")
    @JsonInclude(NON_NULL)
    private final String mapCraft;

    @Override
    public String toString() {
        String rep = "Strategy: {ID: " + this.id;
        if (league != null) {rep += ", league: " + league;}
        if (tree != null) {rep += ", tree: " + tree;}
        if (scarabs != null) {
            if (scarabs.length == 0) {rep += ", scarabs: none";}
            else {
                Counter<String> scarabsCounter = new Counter<>(this.scarabs);
                List<String> scarabsRep = scarabsCounter.keySet().stream()
                        .map(scarab -> scarabsCounter.get(scarab) + "*" + scarab)
                        .collect(Collectors.toList());
                rep += ", scarabs: " + Utils.join(scarabsRep, "; ");
            }
        }
        if (mapLayout != null && mapRolling != null) {rep += ", map: " + mapRolling + " " + mapLayout;}
        else if (mapLayout != null) {rep += ", map: " + mapLayout;}
        else if (mapRolling != null) {rep += ", map: " + mapRolling;}
        if (mapCraft != null) {rep += ", map craft: " + mapCraft;}
        rep += "}";
        return rep;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Strategy strategy = (Strategy) o;

        if (league != null ? !league.equalsIgnoreCase(strategy.league) : strategy.league != null) return false;
        if (tree != null ? !tree.equalsIgnoreCase(strategy.tree) : strategy.tree != null) return false;
        if (scarabs == null && strategy.scarabs == null);
        else if (scarabs == null || strategy.scarabs == null) return false;
        else if (scarabs.length != strategy.scarabs.length) return false;
        else {
            Counter<String> counter = new Counter<>(Arrays.stream(scarabs).map(String::toLowerCase).collect(Collectors.toList()));
            counter.substract(Arrays.stream(strategy.scarabs).map(String::toLowerCase).collect(Collectors.toList()));
            if (!counter.allZero()) return false;
        }
        if (mapLayout != null ? !mapLayout.equalsIgnoreCase(strategy.mapLayout) : strategy.mapLayout != null) return false;
        if (mapRolling != null ? !mapRolling.equalsIgnoreCase(strategy.mapRolling) : strategy.mapRolling != null) return false;
        return mapCraft != null ? mapCraft.equalsIgnoreCase(strategy.mapCraft) : strategy.mapCraft == null;
    }

    @Override
    public int hashCode() {
        int result = league != null ? league.toLowerCase().hashCode() : 0;
        result = 31 * result + (tree != null ? tree.toLowerCase().hashCode() : 0);
        result = 31 * result + (scarabs != null ? Arrays.stream(scarabs).map(String::toLowerCase).sorted().hashCode() : 0);
        result = 31 * result + (mapLayout != null ? mapLayout.toLowerCase().hashCode() : 0);
        result = 31 * result + (mapRolling != null ? mapRolling.toLowerCase().hashCode() : 0);
        result = 31 * result + (mapCraft != null ? mapCraft.toLowerCase().hashCode() : 0);
        return result;
    }

    public Strategy copyWithoutId() {
        return new Strategy(null, this.league, this.tree, this.treeUrl, this.scarabs, this.mapLayout, this.mapRolling, this.mapCraft);
    }

    private void addToFile() {
        try {
            if (this.id == null) {
                create(this.league, this.tree, this.treeUrl, this.scarabs, this.mapLayout, this.mapRolling, this.mapCraft);
            }
            else {
                append(Strategy.filename, toJson(this));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<Strategy> getAllList() {
        return new ArrayList<Strategy>(getAll().values());
    }

    public static Map<Integer, Strategy> getAll() {
        Map<Integer, Strategy> map = new HashMap<Integer, Strategy>();
        try {
            List<String> lines = readLines(filename);
            for (String line : lines) {
                if (line.isEmpty()) {
                    continue;
                }
                Strategy newStrat;
                try {
                    newStrat = parseJson(line, Strategy.class);
                }
                catch (JsonProcessingException e) {
                    continue;
                }
                map.put(newStrat.getId(), newStrat);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return map;
    }

    public static Strategy getById(int id) {
        Strategy strat = getAll().get(id);
        if (strat == null) {
            strat = new Strategy(id, null, null, null, null, null, null, null);
            strat.addToFile();
        }
        return strat;
    }

    public static Strategy create(int id, String league, String tree, String treeUrl, String[] scarabs, String map, String mapRolling, String mapCraft) {
        Strategy strat = getAll().get(id);
        if (strat == null) {
            strat = new Strategy(id, league, tree, treeUrl, scarabs, map, mapRolling, mapCraft);
            strat.addToFile();
        }
        else if (!strat.equals(new Strategy(id, league, tree, treeUrl, scarabs, map, mapRolling, mapCraft))) {
            print("Id conflicts with different existing Strategy");
        }
        return strat;
    }

    public static Strategy create(String league, String tree, String treeUrl, String[] scarabs, String map, String mapRolling, String mapCraft) {
        Map<Integer, Strategy> strats = getAll();
        int id = 0;
        while (true) {
            if (strats.containsKey(id)) {
                id++;
            }
            else {
                Strategy strat = new Strategy(id, league, tree, treeUrl, scarabs, map, mapRolling, mapCraft);
                strat.addToFile();
                return strat;
            }
        }
    }

    public static Strategy create() throws StrategyCreationInterruptedException {
        int id = getLowestFreeId();
        String league = input("Enter the league this strategy is run in");
        String tree = input("Enter a description of the tree");
        String treeUrl = input("Paste a Url for the tree");
        String scarabString = input("Enter which scarabs are used in the format scarab1;2*scarab2" +
                        "\nEnter _ to not specify and leave empty for no scarabs",
                "^((\\d+\\*)?[\\w\\s]+)(;(\\d+\\*)?[\\w\\s]+)*$|^$|^_$");
        List<String> scarabs = new ArrayList<String>();
        for (String scarab : scarabString.split(";")) {
            String[] rep = scarab.split("\\*");
            if (rep.length == 1) {
                scarabs.add(scarab);
            }
            else {
                for (int i = 0; i < Integer.parseInt(rep[0]); i++) {
                    scarabs.add(rep[1]);
                }
            }
        }
        String map = input("Enter the name of the map");
        String mapRolling = input("Enter a description of how the maps are rolled");
        String mapCraft = input("Enter which map craft is used");

        if (tree.isEmpty()) {tree = null;}
        if (treeUrl.isEmpty()) {treeUrl = null;}
        String[] scarabArray;
        if (scarabString.equals("_")) {scarabArray = null;}
        else if (scarabString.isEmpty()) {scarabArray = new String[0];}
        else {scarabArray = scarabs.toArray(new String[0]);}
        if (map.isEmpty()) {map = null;}
        if (mapRolling.isEmpty()) {mapRolling = null;}
        if (mapCraft.isEmpty()) {mapCraft = null;}

        Strategy newStrat = new Strategy(id, league, tree, treeUrl, scarabArray, map, mapRolling, mapCraft);
        print(newStrat);
        String[] options = {"y", "yes", "n", "no"};
        String validation = input("Is this correct?", options).toLowerCase();
        if (validation.equals("y") || validation.equals("yes")) {
            newStrat.addToFile();
            return newStrat;
        }
        throw new StrategyCreationInterruptedException("Creation of new Strategy interrupted");
    }

    private static int getLowestFreeId() {
        Map<Integer, Strategy> strats = getAll();
        int id = 0;
        while (true) {
            if (strats.containsKey(id)) {
                id++;
            }
            else {
                return id;
            }
        }
    }
}
