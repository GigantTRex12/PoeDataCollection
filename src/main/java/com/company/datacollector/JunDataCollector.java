package com.company.datacollector;

import com.company.datasets.builder.DataSetBuilderInterface;
import com.company.datasets.builder.JunDataSetBuilderInterface;
import com.company.datasets.datasets.DataSet;
import com.company.datasets.datasets.jundataset.JunDataSet;
import com.company.datasets.datasets.jundataset.JunEncounterDataSet;
import com.company.datasets.datasets.jundataset.SafehouseEncounterDataSet;
import com.company.datasets.other.jun.Board;
import com.company.datasets.other.jun.Encounter;
import com.company.exceptions.SomethingIsWrongWithMyCodeException;
import com.company.utils.FileUtils;
import com.company.utils.Utils;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.FileNotFoundException;
import java.util.*;
import java.util.stream.Collectors;

import static com.company.datasets.datasets.jundataset.JunDataSet.LastEncountered.*;
import static com.company.utils.IOUtils.*;
import static java.util.Map.entry;

public class JunDataCollector extends DataCollector<JunDataSet> {

    private Class<? extends JunDataSet> currClass;

    private final String leagueId;

    private int encounterId;

    private Board currBoard;

    public JunDataCollector(String filename) throws FileNotFoundException, JsonProcessingException {
        super();

        List<String> previousReps = Arrays.stream(FileUtils.read(filename).split("\\n"))
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());

        List<JunDataSet> previousDataSets = new ArrayList<>();
        for (String previousRep : previousReps) {
            previousDataSets.add(Utils.parseJson(previousRep, JunDataSet.class));
        }

        List<String> possibleIds = previousDataSets.stream()
                .map(ds -> ds.getLeagueId().toLowerCase())
                .collect(Collectors.toList());
        Map<String, String> nrToId = new LinkedHashMap<>();
        for (int i = 0; i < possibleIds.size(); i++) {
            nrToId.put(Integer.toString(i), possibleIds.get(i));
        }
        leagueId = input("Which league to use?", nrToId, false).toLowerCase();
        List<JunDataSet> oldSets = previousDataSets.stream()
                .filter(ds -> ds.getLeagueId().equalsIgnoreCase(leagueId))
                .sorted(Comparator.comparingInt(JunDataSet::getEncounterId))
                .collect(Collectors.toList());
        if (oldSets.size() == 0) {
            encounterId = 0;
            String newBoard = input("Start with empty Board?",
                    Map.ofEntries(entry("y", "yes"), entry("n", "no"))).toLowerCase();
            if (newBoard.equals("y") || newBoard.equals("yes")) {
                currBoard = Board.createEmptyBoard();
            } else {
                currBoard = Board.createBoard();
            }
        } else {
            encounterId = oldSets.get(oldSets.size() - 1).getEncounterId() + 1;
            currBoard = oldSets.get(oldSets.size() - 1).getBoardAfter();
            print(currBoard);
            String newBoard = input("Is this Board correct?",
                    Map.ofEntries(entry("y", "yes"), entry("c", "create new"), entry("e", "edit"))).toLowerCase();
            if (newBoard.equals("e") || newBoard.equals("edit")) {
                currBoard.editBoard();
            } else if (newBoard.equals("c") || newBoard.equals("create new")) {
                currBoard = Board.createBoard();
            }
        }
    }

    @Override
    protected Class<? extends DataSet> getGenericClass() {
        return currClass;
    }

    @Override
    protected void beforeAddData() {
        String classChoice = input("Which type of dataset?",
                Map.ofEntries(entry("m", "in map"), entry("s", "safehouse"))).toLowerCase();
        if (classChoice.equals("m") || classChoice.equals("in map")) {
            currClass = JunEncounterDataSet.class;
        } else {
            currClass = SafehouseEncounterDataSet.class;
        }
    }

    @Override
    protected JunDataSetBuilderInterface<?> createBuilder() {
        JunDataSetBuilderInterface<?> builder;
        if (currClass == JunEncounterDataSet.class) {
            builder = JunEncounterDataSet.builder();
        }
        else if (currClass == SafehouseEncounterDataSet.class) {
            builder = SafehouseEncounterDataSet.builder();
        }
        else {
            throw new SomethingIsWrongWithMyCodeException("Invalid current class");
        }

        builder.strategy(currStrat);
        builder.leagueId(leagueId);
        builder.boardBefore(currBoard.deepCopy());

        String lastEncountered = input("When was the last encounter?", Map.ofEntries(
                entry("n", "nothing in between"),
                entry("l", "logged out in between"),
                entry("m", "maps with no encounters in between"),
                entry("e", "unknown amount of encounters in between"),
                entry("ek", "known amount of encounters in between")
        )).toLowerCase();
        if (lastEncountered.equals("n") || lastEncountered.equals("nothing in between")) {
            builder.encountersSinceLastSet(NOTHING_IN_BETWEEN);
        }
        else if (lastEncountered.equals("l") || lastEncountered.equals("logged out in between")) {
            builder.encountersSinceLastSet(LOGOUT_IN_BETWEEN);
        }
        else if (lastEncountered.equals("m") || lastEncountered.equals("maps with no encounters in between")) {
            builder.encountersSinceLastSet(MAPS_IN_BETWEEN);
        }
        else if (lastEncountered.equals("e") || lastEncountered.equals("unknown amount of encounters in between")) {
            builder.encountersSinceLastSet(ENCOUNTERS_IN_BETWEEN_UNKNOWN);
            encounterId++;
        }
        else {
            builder.encountersSinceLastSet(ENCOUNTERS_IN_BETWEEN_KNOWN);
            encounterId += Integer.parseInt(input("How many encounters in between?", "[1-9]\\d*"));
        }
        builder.encounterId(encounterId);

        return builder;
    }

    @Override
    protected DataSetBuilderInterface<JunDataSet> finalizeData(DataSetBuilderInterface<JunDataSet> builder) {
        if (currClass == JunEncounterDataSet.class) {
            if (builder.getClass() != JunEncounterDataSet.JunEncounterDataSetBuilder.class) {
                throw new SomethingIsWrongWithMyCodeException("Incorrect Builder");
            }
            JunEncounterDataSet.JunEncounterDataSetBuilder castedBuilder = (JunEncounterDataSet.JunEncounterDataSetBuilder) builder;

            for (Encounter encounter : castedBuilder.getEncounters()) {
                currBoard.applyEncounter(encounter);
            }

            return checkAndAddBoardAfter(castedBuilder);
        }
        else if (currClass == SafehouseEncounterDataSet.class) {
            if (builder.getClass() != SafehouseEncounterDataSet.SafehouseEncounterDataSetBuilder.class) {
                throw new SomethingIsWrongWithMyCodeException("Incorrect Builder");
            }
            SafehouseEncounterDataSet.SafehouseEncounterDataSetBuilder castedBuilder = (SafehouseEncounterDataSet.SafehouseEncounterDataSetBuilder) builder;

            currBoard.resetSafeHouseIntelligence(castedBuilder.getSafehouse());
            currBoard.editSafehouse(castedBuilder.getSafehouse());

            return checkAndAddBoardAfter(castedBuilder);
        }
        return builder;
    }

    private JunDataSetBuilderInterface<?> checkAndAddBoardAfter(JunDataSetBuilderInterface<?> builder) {
        print(currBoard);
        String boardCorrect = input("Is this Board correct?",
                Map.ofEntries(entry("y", "yes"), entry("n", "no"))).toLowerCase();
        if (boardCorrect.equals("n") || boardCorrect.equals("no")) {
            currBoard.editBoard();
        }

        builder.boardAfter(currBoard.deepCopy());
        return builder;
    }

}
