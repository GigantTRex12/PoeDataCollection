package com.company.datacollector_lib.collectors;

import collector.Question;
import com.company.api.DbWriter;
import com.company.datacollector_lib.DataCollector;
import com.company.dataset_lib.datasets.MapDropDataSet;
import exceptions.InvalidInputFormatException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.company.dataset_lib.datasets.MapDropDataSet.MapType.*;
import static java.util.Map.entry;

public class MapDropDataCollector extends DataCollector<MapDropDataSet> {

    private static final Map<String, MapDropDataSet.MapType> MAP_TYPES = Map.ofEntries(
            entry("e", ELDER),
            entry("s", SHAPER),
            entry("y", SYNTH),
            entry("c", CONQUEROR),
            entry("u", UNIQUE),
            entry("r", REGULAR),
            entry("n", NIGHTMARE),
            entry("o",ORIGINATOR),
            // characters in keys need to be in alphabetical order
            entry("eo", ORIGINATOR_ELDER),
            entry("os", ORIGINATOR_SHAPER),
            entry("co", ORIGINATOR_CONQUEROR),
            entry("er", NON_GUARDIAN_ELDER),
            entry("rs", NON_GUARDIAN_SHAPER),
            entry("eor", ORIGINATOR_NON_GUARDIAN_ELDER),
            entry("ors", ORIGINATOR_NON_GUARDIAN_SHAPER)
    );

    @Override
    protected List<Question> getQuestions() {
        return List.of(
                Question.ask("mapsInOrder", "Enter maps dropped.\nr:regular, s:shaper, e:elder, c:conqueror, y:synthesis, n:nightmare, u:unique, o:originator")
                        .regex("^$|^[rsecynuo\\-]*$")
                        .normalize(string -> toMapDropList(string, '-'))
                        .build(),
                Question.ask("bossMapDrops", "Enter maps dropped by boss.\nEmpty for not killing boss, - for no drops")
                        .regex("^-?$|^[rsecynuo,]*$")
                        .normalize(string -> string.equals("-") ? new ArrayList<>() : toMapDropList(string, ','))
                        .emptyToNull()
                        .build()
        );
    }

    @Override
    protected MapDropDataSet mapToDataset(Map<String, Object> map) {
        return new MapDropDataSet(
                this.getMetadata(),
                ((List<?>) map.get("mapsInOrder")).stream().map(o -> (MapDropDataSet.MapType) o).collect(Collectors.toList()),
                ((List<?>) map.get("bossMapDrops")).stream().map(o -> (MapDropDataSet.MapType) o).collect(Collectors.toList())
        );
    }

    @Override
    protected void saveData() {
        DbWriter.writeMapDropDataSets(this.data);
        this.data.clear();
    }

    private static MapDropDataSet.MapType getMapType(String string) {
        char[] ar = string.toCharArray();
        Arrays.sort(ar);
        String key = String.valueOf(ar);
        return MAP_TYPES.get(key);
    }

    private static List<MapDropDataSet.MapType> toMapDropList(String string, char split) throws InvalidInputFormatException {
        if (string.isEmpty()) return List.of();
        List<MapDropDataSet.MapType> list = new ArrayList<>();
        for (String c : string.split(String.valueOf(split))) {
            MapDropDataSet.MapType nextType = getMapType(c);
            if (nextType == null) {
                throw new InvalidInputFormatException(c + "is not a valid combination");
            }
            list.add(nextType);
        }
        return list;
    }

}
